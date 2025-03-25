package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Tracers extends Module {
    public Tracers(){
        super("Tracers", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e){
        if(toggled){
            for (Entity player : mc.world.playerEntities) {
                if (player != null && player != mc.player) {
                    RenderUtils.trace(mc, player, mc.getRenderPartialTicks(), 1);
                }
            }
        }
    }
}
