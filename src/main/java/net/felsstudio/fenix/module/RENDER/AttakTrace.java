package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AttakTrace extends Module {
    public static Entity attackEntity = null;

    public AttakTrace(){
        super("Attak trace", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (attackEntity != null && !attackEntity.isDead && toggled) {
            if (mc.player.getDistance(attackEntity) < 10) {
                RenderUtils.trace(mc, attackEntity, mc.getRenderPartialTicks(), 0);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        attackEntity = e.getTarget();
    }
}
