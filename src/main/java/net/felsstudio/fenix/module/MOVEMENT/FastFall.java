package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class FastFall extends Module {
    public FastFall(){
        super("Fast fall", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if(toggled){
            if (mc.player.fallDistance >= 1) {
                mc.player.posY = mc.player.posY - mc.player.fallDistance;
            }
        }
    }
}
