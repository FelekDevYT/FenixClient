package net.felsstudio.fenix.module.MOVEMENT;

import net.felsstudio.fenix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ScaffoldWalk extends Module {
    private static Minecraft mc = Minecraft.getMinecraft();

    public ScaffoldWalk() {
        super("ScaffoldWalk", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!toggled)
            return;

        BlockPos belowPlayer = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

        if (!mc.world.getBlockState(belowPlayer).getMaterial().isReplaceable())
            return;

        int newSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock))
                continue;

            Block block = Block.getBlockFromItem(stack.getItem());
            if (!block.getDefaultState().isFullBlock())
                continue;

            newSlot = i;
            break;
        }

        if (newSlot == -1)
            return;

        int oldSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = newSlot;
        placeBlockScaffold(belowPlayer);
        mc.player.inventory.currentItem = oldSlot;
    }


    public static void placeBlockScaffold(BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return;

        ItemStack heldItem = mc.player.getHeldItemMainhand();

        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemBlock))
            return;

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing oppositeSide = side.getOpposite();

            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5)
                        .add(new Vec3d(oppositeSide.getDirectionVec()).scale(0.5));

                if (mc.player.getPositionVector().distanceTo(hitVec) <= 5.0) {
                    mc.playerController.processRightClickBlock(
                            mc.player,
                            mc.world,
                            neighbor,
                            oppositeSide,
                            hitVec,
                            EnumHand.MAIN_HAND
                    );
                    return;
                }
            }
        }
    }
}