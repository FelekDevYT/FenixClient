package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraft.entity.item.EntityBoat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class BoatFly extends Module {
    public BoatFly(){
        super("Boat fly", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.getRidingEntity() != null) {
            if (mc.player.getRidingEntity() instanceof EntityBoat) {
                mc.player.getRidingEntity().motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1 : 0;
            }
        }
    }
}
