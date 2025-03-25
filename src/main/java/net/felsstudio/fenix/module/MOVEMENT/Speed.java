package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class Speed extends Module {
    public Speed(){
        super("Speed", Keyboard.KEY_NONE, Category.MOVEMENT);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Speed", this, 0.5, 0, 2, false));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e){
        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava() && toggled) {
            double speed = ExampleMod.instance.settingsManager.getSettingByName(this.name ,"Speed").getValDouble();

            mc.player.setSprinting(true);
            mc.player.motionY = 0.1;

            float yaw = mc.player.rotationYaw * 0.0174532920F;

            mc.player.motionX -= MathHelper.sin(yaw) * (speed / 5);
            mc.player.motionZ += MathHelper.cos(yaw) * (speed / 5);
        }

//        if(mc.player.onGround){
//            mc.player.jump();
//        }
    }
}
