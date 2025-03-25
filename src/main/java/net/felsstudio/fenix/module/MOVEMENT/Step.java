package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;

public class Step extends Module {

    private float prevStepHeight;
    private float prevEntityStepHeight;

    public Step() {
        super("Step", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> arr = new ArrayList<>();
        arr.add("Normal");
        arr.add("AAC");
        arr.add("Vanilla");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, arr, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Height", this, 1.0, 0.5, 2.5, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("EntityStep", this, false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        prevStepHeight = mc.player.stepHeight;

        if (mc.player.isRiding()) {
            prevEntityStepHeight = mc.player.getRidingEntity().stepHeight;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.stepHeight = prevStepHeight;

        if (mc.player.isRiding()) {
            mc.player.getRidingEntity().stepHeight = prevEntityStepHeight;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player == Minecraft.getMinecraft().player) {
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
            float height = (float) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Height").getValDouble();
            boolean entityStep = ExampleMod.instance.settingsManager.getSettingByName(this.name, "EntityStep").getValBoolean();

            // Устанавливаем высоту шага для сущности
            if (entityStep && mc.player.isRiding()) {
                mc.player.getRidingEntity().stepHeight = height;
            }

            switch (mode) {
                case "Normal":
                    handleNormalMode(height);
                    break;
                case "AAC":
                    handleAACMode(height);
                    break;
                case "Vanilla":
                    handleVanillaMode(height);
                    break;
            }
        }
    }

    private void handleNormalMode(float height) {
        if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
            AxisAlignedBB box = mc.player.getEntityBoundingBox().offset(0.0, 0.05, 0.0).grow(0.05);
            if (!mc.world.getCollisionBoxes(mc.player, box.offset(0.0, height, 0.0)).isEmpty()) {
                return;
            }

            double stepHeight = -1.0;
            for (final AxisAlignedBB bb : mc.world.getCollisionBoxes(mc.player, box)) {
                if (bb.maxY > stepHeight) {
                    stepHeight = bb.maxY;
                }
            }

            stepHeight -= mc.player.posY;

            if (stepHeight < 0.0 || stepHeight > height) {
                return;
            }

            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, mc.player.onGround));
            mc.player.setPosition(mc.player.posX, mc.player.posY + stepHeight, mc.player.posZ);
        }
    }

    private void handleAACMode(float height) {
        // Логика для режима AAC (аналогично оригинальной реализации)
    }

    private void handleVanillaMode(float height) {
        mc.player.stepHeight = height;
    }
}