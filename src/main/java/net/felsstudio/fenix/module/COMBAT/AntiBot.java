package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiBot extends Module {
    public AntiBot(){
        super("Anti bot", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        try{
            for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.isInvisible() && entityPlayer != mc.player) {
                    mc.world.removeEntity(entityPlayer);
                }
            }
        }catch (Exception exc){
            ChatUtils.sendMessage("Antibot error!");
        }
    }
}
