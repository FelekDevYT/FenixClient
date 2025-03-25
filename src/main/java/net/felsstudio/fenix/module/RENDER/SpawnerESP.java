package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class SpawnerESP extends Module {
    public SpawnerESP(){
        super("Spawner ESP",
                Keyboard.KEY_NONE,
                Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if(toggled){
            for (Object c : mc.world.loadedTileEntityList) {
                if (c instanceof TileEntityMobSpawner) {
                    RenderUtils.blockESP(((TileEntityMobSpawner) c).getPos());
                }
            }
        }
    }
}
