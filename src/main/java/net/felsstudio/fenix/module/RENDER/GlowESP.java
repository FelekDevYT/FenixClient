package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class GlowESP extends Module {
    private static List<Entity> glowed = new ArrayList<>();

    public GlowESP(){
        super("Glow ESP", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e){
        for(EntityPlayer player : mc.world.playerEntities){
            if(player != mc.player && player != glowed){
                player.setGlowing(true);
                glowed.add(player);
            }
        }
    }

    @Override
    public void onDisable() {
        for(Entity e : glowed){
            e.setGlowing(false);
        }

        glowed.clear();
    }
}
