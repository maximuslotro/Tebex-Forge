package net.buycraft.plugin.forge;

import net.buycraft.plugin.BuyCraftAPI;
import net.buycraft.plugin.IBuycraftPlatform;
import net.buycraft.plugin.UuidUtil;
import net.buycraft.plugin.data.QueuedPlayer;
import net.buycraft.plugin.data.responses.ServerInformation;
import net.buycraft.plugin.execution.placeholder.PlaceholderManager;
import net.buycraft.plugin.execution.strategy.CommandExecutor;
import net.buycraft.plugin.platform.PlatformInformation;
import net.buycraft.plugin.platform.PlatformType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ForgeBuycraftPlatform implements IBuycraftPlatform {

    private static final Map<Level, org.apache.logging.log4j.Level> LOG_LEVEL_MAP = new HashMap<Level, org.apache.logging.log4j.Level>() {{
        put(Level.OFF, org.apache.logging.log4j.Level.OFF);
        put(Level.SEVERE, org.apache.logging.log4j.Level.ERROR);
        put(Level.WARNING, org.apache.logging.log4j.Level.WARN);
        put(Level.INFO, org.apache.logging.log4j.Level.INFO);
        put(Level.FINE, org.apache.logging.log4j.Level.DEBUG);
        put(Level.FINER, org.apache.logging.log4j.Level.TRACE);
        put(Level.ALL, org.apache.logging.log4j.Level.ALL);
    }};

    private final BuycraftPlugin plugin;

    public ForgeBuycraftPlatform(BuycraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BuyCraftAPI getApiClient() {
        return plugin.getApiClient();
    }

    @Override
    public PlaceholderManager getPlaceholderManager() {
        return plugin.getPlaceholderManager();
    }

    @Override
    public void dispatchCommand(String command) {
    	plugin.getServer().getCommandManager().executeCommand(plugin.getServer(), command);
    }

    @Override
    public void executeAsync(Runnable runnable) {
        plugin.getExecutor().submit(runnable);
    }

    @Override
    public void executeAsyncLater(Runnable runnable, long l, TimeUnit timeUnit) {
        plugin.getExecutor().schedule(runnable, l, timeUnit);
    }

    @Override
    public void executeBlocking(Runnable runnable) {
        BuycraftPlugin.scheduler.schedule(runnable,0);
    }

    @Override
    public void executeBlockingLater(Runnable runnable, long l, TimeUnit timeUnit) {
        plugin.getExecutor().schedule(() -> BuycraftPlugin.scheduler.schedule(runnable,0), l, timeUnit);
    }

    private EntityPlayerMP getPlayer(QueuedPlayer player) {
        if (player.getUuid() != null && plugin.getServer().isServerInOnlineMode()) {
            UUID uuid = UuidUtil.mojangUuidToJavaUuid(player.getUuid());
            List<Object> playersO = plugin.getServer().getConfigurationManager().playerEntityList;
            List<EntityPlayerMP> players = playersO.stream()
                    .map(element->(EntityPlayerMP) element)
                    .collect(Collectors.toList());
            return players
                    .stream()
                    .filter(entityPlayerMP -> entityPlayerMP.getUniqueID().equals(uuid))
                    .findFirst().orElse(null);
        } else {
        	List<Object> playersO = plugin.getServer().getConfigurationManager().playerEntityList;
            List<EntityPlayerMP> players = playersO.stream()
                    .map(element->(EntityPlayerMP) element)
                    .collect(Collectors.toList());
            return players
                    .stream()
                    .filter(entityPlayerMP -> entityPlayerMP.getDisplayName().equalsIgnoreCase(player.getName()))
                    .findFirst().orElse(null);
        }
    }

    @Override
    public boolean isPlayerOnline(QueuedPlayer queuedPlayer) {
        return getPlayer(queuedPlayer) != null;
    }

    @Override
    public int getFreeSlots(QueuedPlayer queuedPlayer) {
        InventoryPlayer inventory = getPlayer(queuedPlayer).inventory;
        int NumberEmpty = 0;
        for (ItemStack slot : inventory.mainInventory) {
        	if (slot == null) {
        		NumberEmpty+=1;
        	}
        }
        return NumberEmpty;
    }

    @Override
    public void log(Level level, String s) {
        plugin.getLogger().log(LOG_LEVEL_MAP.get(level), s);
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        plugin.getLogger().log(LOG_LEVEL_MAP.get(level), s, throwable);
    }

    @Override
    public CommandExecutor getExecutor() {
        return plugin.getCommandExecutor();
    }

    @Override
    public PlatformInformation getPlatformInformation() {
        return new PlatformInformation(PlatformType.FORGE, plugin.getServer().getMinecraftVersion() + "-" + ForgeVersion.getVersion());
    }

    @Override
    public String getPluginVersion() {
        return BuycraftPlugin.PLUGIN_VERSION;
    }

    @Override
    public ServerInformation getServerInformation() {
        return plugin.getServerInformation();
    }
}
