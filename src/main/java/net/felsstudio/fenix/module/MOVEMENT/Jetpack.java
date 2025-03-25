package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class Jetpack extends Module {

    private final Setting flightKickBypass;
    private double flyHeight;

    public Jetpack() {
        super("Jetpack", Keyboard.KEY_NONE, Category.MOVEMENT);

        flightKickBypass = new Setting("Flight-Kick-Bypass", this, false);
        ExampleMod.instance.settingsManager.rSetting(flightKickBypass);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (!toggled) return;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.jump();
        }

        if (flightKickBypass.getValBoolean()) {
            updateFlyHeight();
            mc.player.connection.sendPacket(new CPacketPlayer(true));

            if (flyHeight <= 290 && hasTimePassedM(500) || flyHeight > 290 && hasTimePassedM(100)) {
                goToGround();
                updateLastMS();
            }
        }
    }

    private void updateFlyHeight() {
        double h = 1;
        AxisAlignedBB box = mc.player.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);
        for (flyHeight = 0; flyHeight < mc.player.posY; flyHeight += h) {
            AxisAlignedBB nextBox = box.offset(0, -flyHeight, 0);

            if (mc.world.checkBlockCollision(nextBox)) {
                if (h < 0.0625)
                    break;

                flyHeight -= h;
                h /= 2;
            }
        }
    }

    private void goToGround() {
        if (flyHeight > 300)
            return;

        double minY = mc.player.posY - flyHeight;

        if (minY <= 0)
            return;

        for (double y = mc.player.posY; y > minY; ) {
            y -= 8;
            if (y < minY)
                y = minY;

            CPacketPlayer.Position packet = new CPacketPlayer.Position(mc.player.posX, y, mc.player.posZ, true);
            mc.player.connection.sendPacket(packet);
        }

        for (double y = minY; y < mc.player.posY; ) {
            y += 8;
            if (y > mc.player.posY)
                y = mc.player.posY;

            CPacketPlayer.Position packet = new CPacketPlayer.Position(mc.player.posX, y, mc.player.posZ, true);
            mc.player.connection.sendPacket(packet);
        }
    }

    private boolean hasTimePassedM(long milliseconds) {
        return System.currentTimeMillis() - lastMS >= milliseconds;
    }

    private void updateLastMS() {
        lastMS = System.currentTimeMillis();
    }

    private long lastMS = 0;
}