package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class TriggerBot extends Module {
    private Entity entity;

    public TriggerBot(){
        super("Triger bot", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e){
        if(toggled){
            RayTraceResult o = Minecraft.getMinecraft().objectMouseOver;

            if (o != null) {
                if (o.typeOfHit == RayTraceResult.Type.ENTITY) {
                    entity = o.entityHit;

                    if (entity instanceof EntityPlayer) {
                        if (Minecraft.getMinecraft().player.getCooledAttackStrength(0) == 1) {
                            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, entity);
                            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                            Minecraft.getMinecraft().player.resetCooldown();
                        }
                    }
                }
            }
        }
    }
}
