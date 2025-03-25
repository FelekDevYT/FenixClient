package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ChestESP extends Module {
    public ChestESP(){
        super("Chest ESP", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if(toggled){
            for (Object c : mc.world.loadedTileEntityList) {
                if (c instanceof TileEntityChest) {
                    RenderUtils.blockESP(((TileEntityChest) c).getPos());
                }
            }
        }
    }
}
