package net.buycraft.plugin.forge.command;

import net.buycraft.plugin.forge.BuycraftPlugin;
import net.buycraft.plugin.forge.util.ForgeMessageUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class BuyCommand extends CommandBase {

    private static final String BREAK = "                                            ";

    private final BuycraftPlugin plugin;
    private final String alias;

    public BuyCommand(BuycraftPlugin plugin, String alias) {
        this.plugin = plugin;
        this.alias = alias;
    }

    @Override
    public String getCommandName() {
        return this.alias;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.alias;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(plugin.getServerInformation() == null) {
            ForgeMessageUtil.sendMessage(sender,
                    new ChatComponentText(ForgeMessageUtil.format("information_no_server"))
                    .setChatStyle(BuycraftPlugin.ERROR_STYLE));
            return;
        }

        this.sendBreak(sender);
        ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("To view the webstore, click this link: "))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, plugin.getServerInformation().getAccount().getDomain()))));
        ForgeMessageUtil.sendMessage(sender, new ChatComponentText(plugin.getServerInformation().getAccount().getDomain()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)
        .setUnderlined(true)
        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, plugin.getServerInformation().getAccount().getDomain()))));
        this.sendBreak(sender);
    }

    private void sendBreak(ICommandSender sender) {
        ForgeMessageUtil.sendMessage(sender, new ChatComponentText(BREAK).setChatStyle(new ChatStyle().setStrikethrough(true)));
    }

}
