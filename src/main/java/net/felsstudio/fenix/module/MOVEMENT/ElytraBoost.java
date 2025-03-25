package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ElytraBoost extends Module {
    public ElytraBoost(){
        super("Elyta boost", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onLocalPlayerUpdate() {
        if(toggled){
            if (!Minecraft.getMinecraft().player.isElytraFlying()) return;
            float yaw = Minecraft.getMinecraft().player.rotationYaw;
            float pitch = Minecraft.getMinecraft().player.rotationPitch;
            if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
                Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.05;
                Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.05;
                Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * 0.05;
            }
            if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
                Minecraft.getMinecraft().player.motionY += 0.05;
            if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
                Minecraft.getMinecraft().player.motionY -= 0.05;
        }
    }
}
