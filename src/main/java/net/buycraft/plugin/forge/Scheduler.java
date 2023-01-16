package net.buycraft.plugin.forge;

import java.util.concurrent.CopyOnWriteArrayList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class Scheduler {
	private final CopyOnWriteArrayList<TickTimer> timers = new CopyOnWriteArrayList<TickTimer>();

	public TickTimer schedule(final Runnable m, final long runAfter) {
		final TickTimer t = new TickTimer(m, runAfter);
		schedule(t);
		return t;
	}

	public TickTimer schedule(final Runnable m, final long runAfter, final long interval, final boolean concurrent) {
		final TickTimer t = new TickTimer(m, runAfter, interval);
		t.concurrent = concurrent;
		schedule(t);
		return t;
	}

	public TickTimer schedule(final Runnable m, final long runAfter, final long interval) {
		return schedule(m, runAfter, interval, true);
	}

	public void schedule(final TickTimer t) {
		timers.add(t);
	}

	public void cancel(final TickTimer t) {
		timers.remove(t);
	}

	@SubscribeEvent
	public void serverTick(final ServerTickEvent e) {
		if (e.phase != Phase.START) {
			return;
		}

		for (final TickTimer t : timers) {
			if (System.currentTimeMillis() >= t.runAt) {
				if (t.task == null) {
					timers.remove(t);
				} else if (t.runUntil != -1 && System.currentTimeMillis() >= t.runUntil) {
					timers.remove(t);
				} else {
					if (!t.concurrent) {
						new Thread(t.task).start();
					} else {
						t.task.run();
					}

					if (t.interval == 0) {
						timers.remove(t);
					} else {
						t.runAt += t.interval;
					}
				}
			}
		}
	}
}