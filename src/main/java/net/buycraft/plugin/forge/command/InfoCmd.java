package net.buycraft.plugin.forge.command;

import net.buycraft.plugin.forge.BuycraftPlugin;
import net.buycraft.plugin.forge.util.ForgeMessageUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.stream.Stream;

public class InfoCmd extends Subcommand {

    private final BuycraftPlugin plugin;

    public InfoCmd(final BuycraftPlugin plugin) {
        super("info", "info");
        this.plugin = plugin;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (plugin.getApiClient() == null) {
            ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("generic_api_operation_error"))
                    .setChatStyle(BuycraftPlugin.ERROR_STYLE));
        } else if (plugin.getServerInformation() == null) {
            ForgeMessageUtil.sendMessage(sender, new ChatComponentText(ForgeMessageUtil.format("information_no_server"))
                    .setChatStyle(BuycraftPlugin.ERROR_STYLE));
        } else {
            String webstoreURL = plugin.getServerInformation().getAccount().getDomain();
            ChatComponentText webstore = new ChatComponentText(webstoreURL);//.setColor(EnumChatFormatting.GREEN);
            webstore.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)
            		.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, webstoreURL))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(webstoreURL))));

            ChatComponentText serverInfo = new ChatComponentText(plugin.getServerInformation().getServer().getName());
            serverInfo.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN));
            Stream.of(
                    new ChatComponentText(ForgeMessageUtil.format("information_title") + " ")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)),
                    new ChatComponentText(ForgeMessageUtil.format("information_sponge_server") + " ")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)).appendSibling(serverInfo),
                    new ChatComponentText(ForgeMessageUtil.format("information_currency",
                            plugin.getServerInformation().getAccount().getCurrency().getIso4217()))
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)),
                    new ChatComponentText(ForgeMessageUtil.format("information_domain", ""))
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)).appendSibling(webstore)
            ).forEach(message -> ForgeMessageUtil.sendMessage(sender, message));
        }
    }

    @Override
    public String getI18n() {
        return "usage_information";
    }
}
