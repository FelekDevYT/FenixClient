package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e){
        if(mc.player.moveForward > 0 && !mc.player.collidedHorizontally && toggled){
            mc.player.setSprinting(true);
        }
    }
}
