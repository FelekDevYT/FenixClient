package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoClip extends Module {

    public NoClip() {
        super("NoClip", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ChatUtils.sendMessage("§c§lWARNING:§r You will take damage while moving through blocks!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.noClip = false;
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (!toggled) return;

        mc.player.noClip = true;
        mc.player.fallDistance = 0;
        mc.player.onGround = false;

        mc.player.capabilities.isFlying = false;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;

        float speed = 0.2F;
        mc.player.jumpMovementFactor = speed;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += speed;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= speed;
        }
    }
}