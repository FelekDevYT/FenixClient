package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ViewModel extends Module {
    public ViewModel(){
        super("View model", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderSpecificHandEvent e){
        if(toggled){
            GL11.glTranslated(0, 0, -7);
        }
    }
}
