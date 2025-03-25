package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;
import java.util.Objects;

public class Fly extends Module {
    public Fly(){
        super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> options = new ArrayList<>();

        options.add("Creative");
        options.add("WellMore");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, options, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Speed", this, 2.11, 1, 8, false));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if(toggled){
            String Mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();

            if (Objects.equals(Mode, "WellMore")) {
                float speed = (float) (float) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble();;

                mc.player.noClip = true;
                mc.player.fallDistance = 0;
                mc.player.onGround = false;

                mc.player.capabilities.isFlying = false;

                mc.player.motionX = 0;
                mc.player.motionY = 0;
                mc.player.motionZ = 0;

                mc.player.jumpMovementFactor = speed;

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY += speed;
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY -= speed;
                }
            } else {
                mc.player.capabilities.isFlying = true;
                mc.player.capabilities.setFlySpeed((float) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble());
            }
        }
    }
}
