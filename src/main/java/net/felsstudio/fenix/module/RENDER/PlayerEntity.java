package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class PlayerEntity extends Module{
    public PlayerEntity(){
        super("Player entity", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event){
        if(toggled){
            switch (event.getType()) {
                case TEXT:
                    RenderUtils.renderEntity(mc.player, 30, 40, 100);
                    break;
                default:
                    break;
            }
        }
    }
}
