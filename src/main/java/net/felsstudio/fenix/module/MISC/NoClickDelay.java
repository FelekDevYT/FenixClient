package net.felsstudio.fenix.module.MISC;

import net.felsstudio.fenix.module.Module;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class NoClickDelay extends Module {
    public NoClickDelay(){
        super("NCD", Keyboard.KEY_NONE, Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(Event event) {
        if(toggled){
            if (mc.gameSettings.keyBindAttack.isKeyDown())
                mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }
}
