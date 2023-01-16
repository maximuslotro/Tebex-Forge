package net.buycraft.plugin.forge;


public class TickTimer {	
	long runAt;

	Runnable task;
	boolean concurrent = true;

	long interval;
	long runUntil;

	public TickTimer(final Runnable method, final long runAfter) {
		runAt = System.currentTimeMillis() + runAfter;
		task = method;
		interval = 0;
		runUntil = -1;
	}

	public TickTimer(final Runnable method, final long runAfter, final long interval) {
		this(method, runAfter);
		this.interval = interval;
	}

	public TickTimer(final Runnable method, final long runAfter, final long interval, final long runUntil) {
		this(method, runAfter, interval);
		this.runUntil = runUntil;
	}

	public void cancel() {
		BuycraftPlugin.scheduler.cancel(this);
	}

	public void runAndCancel() {
		cancel();
		task.run();
	}
}