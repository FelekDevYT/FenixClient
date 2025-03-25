package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Bunnyhop extends Module {
    public Bunnyhop(){
        super("BHOP", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent e) {
        if(toggled){
            if (mc.player.onGround) {
                mc.player.jump();
            }
        }
    }
}
