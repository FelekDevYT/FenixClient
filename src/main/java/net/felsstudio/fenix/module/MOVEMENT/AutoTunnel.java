package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.BlockUtils;
import net.felsstudio.fenix.utils.PlayerUtil;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;
import java.util.List;

public class AutoTunnel extends Module {

    private List<BlockPos> blocksToDestroy = new ArrayList<>();
    private boolean needPause = false;

    public AutoTunnel() {
        super("AutoTunnel", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> opetions = new ArrayList<>();

        opetions.add("Tunnel1x2");
        opetions.add("Tunnel2x2");
        opetions.add("Tunnel2x3");
        opetions.add("Tunnel3x3");


        ArrayList<String> op2 = new ArrayList<>();
        op2.add("Normal");
        op2.add("Packet");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, opetions, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("MiningMode", this, op2, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Visualize", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("PauseAutoWalk", this, true));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(toggled){
            if (event.phase == TickEvent.Phase.START && event.player == Minecraft.getMinecraft().player) {
                blocksToDestroy.clear();

                BlockPos playerPos = event.player.getPosition();
                String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();

                switch (PlayerUtil.getFacing()) {
                    case EAST:
                        addBlocksToDestroy(playerPos, mode, 1, 0);
                        break;
                    case NORTH:
                        addBlocksToDestroy(playerPos, mode, 0, -1);
                        break;
                    case SOUTH:
                        addBlocksToDestroy(playerPos, mode, 0, 1);
                        break;
                    case WEST:
                        addBlocksToDestroy(playerPos, mode, -1, 0);
                        break;
                    default:
                        break;
                }

                BlockPos toDestroy = null;

                for (BlockPos pos : blocksToDestroy) {
                    Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();

                    if (block == Blocks.AIR || block instanceof BlockDynamicLiquid || block instanceof BlockStaticLiquid || block == Blocks.BEDROCK)
                        continue;

                    toDestroy = pos;
                    break;
                }

                if (toDestroy != null) {
                    float[] rotations = BlockUtils.getLegitRotations(new Vec3d(toDestroy.getX() + 0.5, toDestroy.getY() + 0.5, toDestroy.getZ() + 0.5));
                    PlayerUtil.facePitchAndYaw(rotations[1], rotations[0]);

                    String miningMode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "MiningMode").getValString();

                    switch (miningMode) {
                        case "Normal":
                            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                            Minecraft.getMinecraft().playerController.onPlayerDamageBlock(toDestroy, EnumFacing.UP);
                            break;
                        case "Packet":
                            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, toDestroy, EnumFacing.UP));
                            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, toDestroy, EnumFacing.UP));
                            break;
                    }

                    needPause = true;
                } else {
                    needPause = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(toggled){
            if (ExampleMod.instance.settingsManager.getSettingByName(this.name, "Visualize").getValBoolean()) {
                for (BlockPos pos : blocksToDestroy) {
                    Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();

                    if (block != Blocks.AIR && block != Blocks.BEDROCK && !(block instanceof BlockDynamicLiquid) && !(block instanceof BlockStaticLiquid)) {
                        AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                                pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY,
                                pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
                                pos.getX() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                                pos.getY() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosY,
                                pos.getZ() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);

                        RenderUtils.drawBox(bb, 1.0f, 0xFF0000);
                    }
                }
            }
        }
    }

    private void addBlocksToDestroy(BlockPos startPos, String mode, int xOffset, int zOffset) {
        for (int i = 0; i < 3; i++) {
            switch (mode) {
                case "Tunnel1x2":
                    blocksToDestroy.add(startPos.add(xOffset, 0, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 1, zOffset));
                    break;
                case "Tunnel2x2":
                    blocksToDestroy.add(startPos.add(xOffset, 0, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 1, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 0, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 1, zOffset + (xOffset == 0 ? 1 : 0)));
                    break;
                case "Tunnel2x3":
                    blocksToDestroy.add(startPos.add(xOffset, 0, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 1, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 2, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 0, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 1, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 2, zOffset + (xOffset == 0 ? 1 : 0)));
                    break;
                case "Tunnel3x3":
                    blocksToDestroy.add(startPos.add(xOffset, 0, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 1, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset, 2, zOffset));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 0, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 1, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 1 : 0), 2, zOffset + (xOffset == 0 ? 1 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 2 : 0), 0, zOffset + (xOffset == 0 ? 2 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 2 : 0), 1, zOffset + (xOffset == 0 ? 2 : 0)));
                    blocksToDestroy.add(startPos.add(xOffset + (zOffset == 0 ? 2 : 0), 2, zOffset + (xOffset == 0 ? 2 : 0)));
                    break;
            }
            startPos = startPos.add(xOffset, 0, zOffset);
        }
    }

    public boolean shouldPauseAutoWalk() {
        return needPause && ExampleMod.instance.settingsManager.getSettingByName(this.name, "PauseAutoWalk").getValBoolean();
    }
}