package net.felsstudio.fenix.module.PLAYER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;

public class Freecam extends Module {

    public Freecam() {
        super("Freecam", Keyboard.KEY_NONE, Category.RENDER);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Normal");
        modes.add("Camera");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Speed", this, 1.0, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("CancelPackets", this, true));
    }

    private Entity riding;
    private EntityOtherPlayerMP Camera;
    private Vec3d position;
    private float yaw;
    private float pitch;

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
            double speed = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble();

            if (mode.equals("Normal")) {
                mc.player.noClip = true;
                mc.player.setVelocity(0, 0, 0);

                double[] dir = directionSpeed(speed);

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = dir[0];
                    mc.player.motionZ = dir[1];
                } else {
                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;
                }

                mc.player.setSprinting(false);

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY += speed;
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY -= speed;
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (toggled && event.getEntity() == mc.player) {
            toggle();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.world == null) return;

        String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();

        if (mode.equals("Normal")) {
            riding = null;

            if (mc.player.getRidingEntity() != null) {
                riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            Camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            Camera.copyLocationAndAnglesFrom(mc.player);
            Camera.prevRotationYaw = mc.player.rotationYaw;
            Camera.rotationYawHead = mc.player.rotationYawHead;
            Camera.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-69, Camera);

            position = mc.player.getPositionVector();
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;

            mc.player.noClip = true;
        } else if (mode.equals("Camera")) {
            Camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            Camera.copyLocationAndAnglesFrom(mc.player);
            Camera.prevRotationYaw = mc.player.rotationYaw;
            Camera.rotationYawHead = mc.player.rotationYawHead;
            Camera.inventory.copyInventory(mc.player.inventory);
            Camera.noClip = true;
            mc.world.addEntityToWorld(-69, Camera);
            mc.setRenderViewEntity(Camera);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.world != null) {
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();

            if (mode.equals("Normal")) {
                if (riding != null) {
                    mc.player.startRiding(riding, true);
                    riding = null;
                }
                if (Camera != null) {
                    mc.world.removeEntity(Camera);
                }
                if (position != null) {
                    mc.player.setPosition(position.x, position.y, position.z);
                }
                mc.player.rotationYaw = yaw;
                mc.player.rotationPitch = pitch;
                mc.player.noClip = false;
                mc.player.setVelocity(0, 0, 0);
            } else if (mode.equals("Camera")) {
                if (Camera != null) {
                    mc.world.removeEntity(Camera);
                }
                mc.setRenderViewEntity(mc.player);
            }
        }
    }

    private double[] directionSpeed(double speed) {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.moveForward;
        float strafe = mc.player.moveStrafing;

        if (forward != 0) {
            if (strafe > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (strafe < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }

        double sin = Math.sin(Math.toRadians(yaw + 90));
        double cos = Math.cos(Math.toRadians(yaw + 90));
        double motionX = (forward * speed * cos + strafe * speed * sin);
        double motionZ = (forward * speed * sin - strafe * speed * cos);

        return new double[]{motionX, motionZ};
    }
}