package net.felsstudio.fenix.chat;

import net.felsstudio.fenix.module.RENDER.BlockESP;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class BlockESPCommand extends CommandBase {

    @Override
    public String getName() {
        return "blockesp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return ".blockesp add <id> or .blockesp remove <id>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            ChatUtils.sendMessage("Usage: .blockesp add <id> or .blockesp remove <id>");
            return;
        }

        String action = args[1];
        String blockId = args[2];

        Block block = Block.getBlockFromName(blockId);
        if (block == null) {
            ChatUtils.sendMessage("Блок не найден!");
            return;
        }

        if (action.equalsIgnoreCase("add")) {
            BlockESP.addBlock(block);
            ChatUtils.sendMessage("Блок " + blockId + " добавлен в ESP.");
        } else if (action.equalsIgnoreCase("remove")) {
            BlockESP.removeBlock(block);
            ChatUtils.sendMessage("Блок " + blockId + " удален из ESP.");
        } else if (action.equalsIgnoreCase("list")){
            for(Block b : BlockESP.espBlocks){
                ChatUtils.sendMessage(b.toString());
            }
        }else {
            ChatUtils.sendMessage("Неизвестная команда!");
        }
    }
}