package net.buycraft.plugin.forge.command;

import net.buycraft.plugin.BuyCraftAPIException;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.forge.BuycraftPlugin;
import net.buycraft.plugin.shared.util.CouponUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.util.Arrays;

public class CouponCmd extends Subcommand {
    private final BuycraftPlugin plugin;

    public CouponCmd(final BuycraftPlugin plugin) {
        super("coupon", "coupon <create/delete>");
        this.plugin = plugin;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage((new ChatComponentText(plugin.getI18n().get("usage_coupon_subcommands"))
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))));
            return;
        }
        switch (args[0]) {
            case "create":
                createCoupon(sender, args);
                break;
            case "delete":
                deleteCoupon(sender, args);
                break;
            default:
                sender.addChatMessage(new ChatComponentText(this.plugin.getI18n().get("usage_coupon_subcommands"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                break;
        }
    }

    private void createCoupon(final ICommandSender sender, String[] args) {
        String[] stripped = Arrays.copyOfRange(args, 1, args.length);
        final Coupon coupon;
        try {
            coupon = CouponUtil.parseArguments(stripped);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(e.getMessage()));
            sender.addChatMessage(new ChatComponentText(plugin.getI18n().get("coupon_creation_arg_parse_failure",
                    e.getMessage())).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            return;
        }

        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().createCoupon(coupon).execute();
                sender.addChatMessage(new ChatComponentText(plugin.getI18n().get("coupon_creation_success",
                        coupon.getCode())).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            } catch (IOException e) {
                sender.addChatMessage(new ChatComponentText(e.getMessage())
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));

            }
        });
    }

    private void deleteCoupon(final ICommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.addChatMessage(new ChatComponentText(
                    plugin.getI18n().get("no_coupon_specified"))
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            return;
        }

        final String code = args[1];

        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().deleteCoupon(code).execute();
                sender.addChatMessage(new ChatComponentText(plugin.getI18n().get("coupon_deleted"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            } catch (IOException | BuyCraftAPIException e) {
                sender.addChatMessage(new ChatComponentText(e.getMessage())
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }
        });
    }

    @Override
    public String getI18n() {
        return "usage_coupon";
    }
}
