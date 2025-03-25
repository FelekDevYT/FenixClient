package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoTrap extends Module {

    private final Vec3d[] offsetsDefault = new Vec3d[]
            {
                    new Vec3d(0.0, 0.0, -1.0), // left
                    new Vec3d(1.0, 0.0, 0.0),  // right
                    new Vec3d(0.0, 0.0, 1.0), // forwards
                    new Vec3d(-1.0, 0.0, 0.0), // back
                    new Vec3d(0.0, 1.0, -1.0), // +1 left
                    new Vec3d(1.0, 1.0, 0.0), // +1 right
                    new Vec3d(0.0, 1.0, 1.0), // +1 forwards
                    new Vec3d(-1.0, 1.0, 0.0), // +1 back
                    new Vec3d(0.0, 2.0, -1.0), // +2 left
                    new Vec3d(1.0, 2.0, 0.0), // +2 right
                    new Vec3d(0.0, 2.0, 1.0), // +2 forwards
                    new Vec3d(-1.0, 2.0, 0.0), // +2 backwards
                    new Vec3d(0.0, 3.0, -1.0), // +3 left
                    new Vec3d(0.0, 3.0, 0.0) // +3 middle
            };
    private final Vec3d[] offsetsTall = new Vec3d[]
            {
                    new Vec3d(0.0, 0.0, -1.0), // left
                    new Vec3d(1.0, 0.0, 0.0),  // right
                    new Vec3d(0.0, 0.0, 1.0), // forwards
                    new Vec3d(-1.0, 0.0, 0.0), // back
                    new Vec3d(0.0, 1.0, -1.0), // +1 left
                    new Vec3d(1.0, 1.0, 0.0), // +1 right
                    new Vec3d(0.0, 1.0, 1.0), // +1 forwards
                    new Vec3d(-1.0, 1.0, 0.0), // +1 back
                    new Vec3d(0.0, 2.0, -1.0), // +2 left
                    new Vec3d(1.0, 2.0, 0.0), // +2 right
                    new Vec3d(0.0, 2.0, 1.0), // +2 forwards
                    new Vec3d(-1.0, 2.0, 0.0), // +2 backwards
                    new Vec3d(0.0, 3.0, -1.0), // +3 left
                    new Vec3d(0.0, 3.0, 0.0), // +3 middle
                    new Vec3d(0.0, 4.0, 0.0) // +4 middle
            };

    private String lastTickTargetName = "";
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun = true;

    public AutoTrap() {
        super("AutoTrap", Keyboard.KEY_NONE, Category.COMBAT);

        ExampleMod.instance.settingsManager.rSetting(new Setting("ToggleMode", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Range", this, 5.5, 0, 10, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("BlocksPerTick", this, 4, 1, 10, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Rotate", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("AnnounceUsage", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("EChests", this, false));

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Full");
        modes.add("Tall");
        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Full"));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (!toggled) return;

        EntityPlayer closestTarget = findClosestTarget();
        if (closestTarget == null) {
            if (firstRun) {
                firstRun = false;
                if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "AnnounceUsage").getValBoolean()) {
                    ChatUtils.sendMessage("[AutoTrap] Enabled, waiting for target.");
                }
            }
            return;
        }

        if (firstRun) {
            firstRun = false;
            lastTickTargetName = closestTarget.getName();
            if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "AnnounceUsage").getValBoolean()) {
                ChatUtils.sendMessage("[AutoTrap] Enabled, target: " + lastTickTargetName);
            }
        } else if (!lastTickTargetName.equals(closestTarget.getName())) {
            lastTickTargetName = closestTarget.getName();
            offsetStep = 0;
            if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "AnnounceUsage").getValBoolean()) {
                ChatUtils.sendMessage("[AutoTrap] New target: " + lastTickTargetName);
            }
        }

        if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "ToggleMode").getValBoolean()) {
            if (isEntityTrapped(closestTarget)) {
                toggle();
                return;
            }
        }

        final List<Vec3d> placeTargets = new ArrayList<>();

        String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
        switch (mode) {
            case "Full":
                Collections.addAll(placeTargets, offsetsDefault);
                break;
            case "Tall":
                Collections.addAll(placeTargets, offsetsTall);
                break;
            default:
                break;
        }

        int blocksPlaced = 0;
        while (blocksPlaced < ExampleMod.instance.settingsManager.getSettingByName(this.name, "BlocksPerTick").getValDouble()) {
            if (offsetStep >= placeTargets.size()) {
                offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(placeTargets.get(offsetStep));
            final BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean shouldTryToPlace = mc.world.getBlockState(targetPos).getMaterial().isReplaceable();

            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    shouldTryToPlace = false;
                    break;
                }
            }

            if (shouldTryToPlace && placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++offsetStep;
        }

        if (blocksPlaced > 0) {
            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.player == null) {
            toggle();
            return;
        }

        firstRun = true;
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;

        if (findObiInHotbar() == -1) {
            ChatUtils.sendMessage(String.format("[AutoTrap] You do not have any %s in your hotbar!", (ExampleMod.instance.settingsManager.getSettingByName(this.name, "EChests").getValBoolean() ? "Ender Chests" : "Obsidian")));
            toggle();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1)
            mc.player.inventory.currentItem = playerHotbarSlot;

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
        if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "AnnounceUsage").getValBoolean())
            ChatUtils.sendMessage("[AutoTrap] Disabled!");
    }

    private boolean placeBlock(final BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return false;
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= ExampleMod.instance.settingsManager.getSettingByName(this.name, "Range").getValDouble()) {
                    final int obiSlot = findObiInHotbar();
                    if (obiSlot == -1) {
                        toggle();
                        return false;
                    }
                    if (lastHotbarSlot != obiSlot) {
                        mc.player.inventory.currentItem = obiSlot;
                        lastHotbarSlot = obiSlot;
                    }
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }

    private int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "EChests").getValBoolean()) {
                    if (block instanceof BlockEnderChest)
                        return i;
                } else if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }

    private EntityPlayer findClosestTarget() {
        EntityPlayer closestTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player || player.isDead || player.getHealth() <= 0) continue;

            double distance = mc.player.getDistance(player);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = player;
            }
        }

        return closestTarget;
    }

    private boolean isEntityTrapped(EntityPlayer player) {
        BlockPos playerPos = new BlockPos(player.getPositionVector());
        for (Vec3d offset : offsetsDefault) {
            BlockPos pos = playerPos.add(offset.x, offset.y, offset.z);
            if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return true;
    }
}