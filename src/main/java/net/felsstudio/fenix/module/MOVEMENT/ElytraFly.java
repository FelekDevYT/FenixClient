package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;

public class ElytraFly extends Module {

    public ElytraFly() {
        super("ElytraFly", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Normal");
        modes.add("Tarzan");
        modes.add("Superior");
        modes.add("Packet");
        modes.add("Control");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Speed", this, 1.82, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("DownSpeed", this, 1.82, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("GlideSpeed", this, 1.0, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("UpSpeed", this, 2.0, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Accelerate", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("AccelerationTimer", this, 1000, 0, 10000, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("RotationPitch", this, 0.0, -90.0, 90.0, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("CancelInWater", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("CancelAtHeight", this, 5, 0, 10, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("InstantFly", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("EquipElytra", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("PitchSpoof", this, false));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            EntityPlayerSP player = mc.player;

            if (player == null) return;

            // Проверка на наличие элитры
            if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "EquipElytra").getValBoolean()) {
                    equipElytra();
                }
                return;
            }

            // Проверка на полет
            if (!player.isElytraFlying()) {
                if (!player.onGround && ExampleMod.instance.settingsManager.getSettingByName(this.name, "InstantFly").getValBoolean()) {
                    player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
                return;
            }

            // Обработка режимов полета
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
            switch (mode) {
                case "Normal":
                case "Tarzan":
                case "Packet":
                    handleNormalMode();
                    break;
                case "Superior":
                    handleImmediateMode();
                    break;
                case "Control":
                    handleControlMode();
                    break;
            }
        }
    }

    private void equipElytra() {
        EntityPlayerSP player = mc.player;
        for (int i = 0; i < 44; ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.ELYTRA) {
                boolean hasArmorAtChest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
                mc.playerController.windowClick(player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, player);
                mc.playerController.windowClick(player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, player);
                if (hasArmorAtChest) {
                    mc.playerController.windowClick(player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, player);
                }
                break;
            }
        }
    }

    private void handleNormalMode() {
        EntityPlayerSP player = mc.player;
        double yHeight = player.posY;

        if (yHeight <= ExampleMod.instance.settingsManager.getSettingByName(this.name, "CancelAtHeight").getValDouble()) {
            ChatUtils.sendMessage("WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
            return;
        }

        boolean isMoveKeyDown = player.movementInput.moveForward > 0 || player.movementInput.moveStrafe > 0;
        boolean cancelInWater = !player.isInWater() && !player.isInLava() && ExampleMod.instance.settingsManager.getSettingByName(this.name, "CancelInWater").getValBoolean();

        if (player.movementInput.jump) {
            accelerate();
            return;
        }

        if (!isMoveKeyDown) {
            // Сброс таймера ускорения
        } else if ((player.rotationPitch <= ExampleMod.instance.settingsManager.getSettingByName(this.name, "RotationPitch").getValDouble() || ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString().equals("Tarzan") && cancelInWater)) {
            if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "Accelerate").getValBoolean()) {
                accelerate();
            }
            return;
        }

        accelerate();
    }

    private void handleImmediateMode() {
        EntityPlayerSP player = mc.player;

        if (player.movementInput.jump) {
            double motionSq = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
            if (motionSq > 1.0) {
                return;
            } else {
                double[] dir = directionSpeed(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble());
                player.motionX = dir[0];
                player.motionY = -(ExampleMod.instance.settingsManager.getSettingByName(this.name, "GlideSpeed").getValDouble() / 10000f);
                player.motionZ = dir[1];
            }
            return;
        }

        player.setVelocity(0, 0, 0);

        double[] dir = directionSpeed(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble());

        if (player.movementInput.moveStrafe != 0 || player.movementInput.moveForward != 0) {
            player.motionX = dir[0];
            player.motionY = -(ExampleMod.instance.settingsManager.getSettingByName(this.name, "GlideSpeed").getValDouble() / 10000f);
            player.motionZ = dir[1];
        }

        if (player.movementInput.sneak) {
            player.motionY = -ExampleMod.instance.settingsManager.getSettingByName(this.name, "DownSpeed").getValDouble();
        }

        player.prevLimbSwingAmount = 0;
        player.limbSwingAmount = 0;
        player.limbSwing = 0;
    }

    private void handleControlMode() {
        EntityPlayerSP player = mc.player;
        double[] dir = directionSpeed(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble());

        if (player.movementInput.moveStrafe != 0 || player.movementInput.moveForward != 0) {
            player.motionX = dir[0];
            player.motionZ = dir[1];

            player.motionX -= (player.motionX * (Math.abs(player.rotationPitch) + 90) / 90) - player.motionX;
            player.motionZ -= (player.motionZ * (Math.abs(player.rotationPitch) + 90) / 90) - player.motionZ;
        } else {
            player.motionX = 0;
            player.motionZ = 0;
        }

        player.motionY = (-Math.toRadians(player.rotationPitch)) * player.movementInput.moveForward;

        player.prevLimbSwingAmount = 0;
        player.limbSwingAmount = 0;
        player.limbSwing = 0;
    }

    private void accelerate() {
        EntityPlayerSP player = mc.player;
        float speed = (float) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Speed").getValDouble();
        double[] dir = directionSpeed(speed);

        player.motionY = -(ExampleMod.instance.settingsManager.getSettingByName(this.name, "GlideSpeed").getValDouble() / 10000f);

        if (player.movementInput.moveStrafe != 0 || player.movementInput.moveForward != 0) {
            player.motionX = dir[0];
            player.motionZ = dir[1];
        } else {
            player.motionX = 0;
            player.motionZ = 0;
        }

        if (player.movementInput.sneak) {
            player.motionY = -ExampleMod.instance.settingsManager.getSettingByName(this.name, "DownSpeed").getValDouble();
        }

        player.prevLimbSwingAmount = 0;
        player.limbSwingAmount = 0;
        player.limbSwing = 0;
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