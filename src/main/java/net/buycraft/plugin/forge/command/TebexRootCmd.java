package net.buycraft.plugin.forge.command;

import com.mojang.authlib.GameProfile;
import net.buycraft.plugin.forge.BuycraftPlugin;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TebexRootCmd extends CommandBase {

    private final BuycraftPlugin plugin;
    private final MinecraftServer server;
    private final String alias;
    private final Map<String, Subcommand> subcommands = new HashMap<>();

    public TebexRootCmd(BuycraftPlugin plugin, MinecraftServer server, String alias) {
        this.plugin = plugin;
        this.server = server;
        this.alias = alias;
    }

    public TebexRootCmd addChild(Subcommand command) {
        this.subcommands.put(command.getCommandName(), command);
        return this;
    }

    @Override
    public String getCommandName() {
        return this.alias;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.alias + " <args>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(isOp(sender) || isConsole(sender)) {
            if(args.length > 0) {
                for(Map.Entry<String, Subcommand> entry : this.subcommands.entrySet()) {
                    if(entry.getKey().equalsIgnoreCase(args[0])) {
                        String[] withoutSubcommand = Arrays.copyOfRange(args, 1, args.length);
                        entry.getValue().execute(this.server, sender, withoutSubcommand);
                        return;
                    }
                }
            }
            showHelp(sender);
        }
    }

    private void showHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(plugin.getI18n().get("usage"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA).setBold(true)));
        for (Map.Entry<String, Subcommand> entry : this.subcommands.entrySet()) {
            sender.addChatMessage(new ChatComponentText("/" + this.alias + " " + entry.getValue().getUsage())
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                    .appendSibling(new ChatComponentText(": " +
                            this.plugin.getI18n().get(entry.getValue().getI18n()))
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))));
        }
    }

    private boolean isOp(ICommandSender sender) {
        if(sender == null || !(sender instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) sender;
        GameProfile profile = player.getGameProfile();
        if(profile == null) {
            return false;
        }
        return server.getConfigurationManager().func_152596_g(profile);
    }

    private boolean isConsole(ICommandSender sender) {
        return sender == this.server;
    }
}
