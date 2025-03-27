package net.felsstudio.fenix.chat;

import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class GotoCommand extends CommandBase {

    @Override
    public String getName(){
        return "goto";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return ".goto <x> <y> <z>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 3){
            ChatUtils.sendMessage("Usage: "+getUsage(null));
            return;
        }

        GotoCommand gotoCommand = new GotoCommand();
        gotoCommand.execute(server, sender, args);
    }
}
