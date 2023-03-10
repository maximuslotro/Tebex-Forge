package net.buycraft.plugin.forge.command;

import net.buycraft.plugin.forge.BuycraftPlugin;
import net.buycraft.plugin.forge.util.ForgeMessageUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class ForceCheckCmd extends Subcommand {

    private final BuycraftPlugin plugin;

    public ForceCheckCmd(final BuycraftPlugin plugin) {
        super("forcecheck", "forcecheck");
        this.plugin = plugin;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (plugin.getApiClient() == null) {
            ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("need_secret_key"))
                    .setChatStyle(BuycraftPlugin.ERROR_STYLE));
        } else if (plugin.getDuePlayerFetcher().inProgress()) {
            ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("already_checking_for_purchases"))
                    .setChatStyle(BuycraftPlugin.ERROR_STYLE));
        } else {
            plugin.getExecutor().submit(() -> plugin.getDuePlayerFetcher().run(false));
            ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("forcecheck_queued"))
                    .setChatStyle(BuycraftPlugin.SUCCESS_STYLE));
        }
    }

    @Override
    public String getI18n() {
        return "usage_forcecheck";
    }
}
