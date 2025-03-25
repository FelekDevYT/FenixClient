package net.felsstudio.fenix.utils;

import net.felsstudio.fenix.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils {
    private static final String prefix = "[" + Client.inGameName + "§f] ";

    public static void sendMessage(String msg) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(prefix + msg));
    }
}