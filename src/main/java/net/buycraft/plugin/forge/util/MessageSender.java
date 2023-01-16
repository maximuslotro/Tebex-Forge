package net.buycraft.plugin.forge.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MessageSender {
	public static void sendMessage(ICommandSender recipient, String message, EnumChatFormatting color)
    {
		ChatComponentText component = new ChatComponentText(message);
        component.getChatStyle().setColor(color);
        recipient.addChatMessage(component);
    }
}