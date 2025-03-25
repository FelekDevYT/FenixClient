package net.felsstudio.fenix.chat;

import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager {

    private static final Map<String, CommandBase> commands = new HashMap<>();

    public CommandManager() {
        // Регистрируем этот класс как обработчик событий
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerCommand(CommandBase command) {
        commands.put(command.getName(), command);
    }

    public static boolean execute(String message){
        if (message.startsWith(".")) {

            String[] args = message.substring(1).split(" ");
            String commandName = args[0];

            CommandBase command = commands.get(commandName);
            if (command != null) {
                try {
                    command.execute(Objects.requireNonNull(Minecraft.getMinecraft().getIntegratedServer()), Minecraft.getMinecraft().player, args);
                } catch (CommandException e) {
                    ChatUtils.sendMessage(e.getMessage());
                }
            } else {
                ChatUtils.sendMessage("Команда не найдена!");
            }
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        event.setCanceled(execute(message));
    }
}