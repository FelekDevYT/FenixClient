package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AutoWalk extends Module {

    private AutoTunnel autoTunnel;

    public AutoWalk() {
        super("AutoWalk", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @SubscribeEvent
    public void onPlayerUpdate(RenderWorldLastEvent event) {
        if (!toggled) return;

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }
}