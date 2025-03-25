package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", Keyboard.KEY_NONE, Category.COMBAT);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Packet");
        modes.add("Jump");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Mode"));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();

            if (mc.player == null || mc.world == null) return;

            if (mc.player.getLastAttackedEntity() instanceof EntityLivingBase && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                switch (mode) {
                    case "Jump":
                        mc.player.jump();
                        break;
                    case "Packet":
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}