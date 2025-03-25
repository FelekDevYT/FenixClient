package net.felsstudio.fenix.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    // Получаем состояние блока по позиции
    public static IBlockState getBlockState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    // Получаем блок по позиции
    public static Block getBlock(BlockPos pos) {
        return getBlockState(pos).getBlock();
    }

    // Проверяем, можно ли разрушить блок
//    public static boolean canBreakBlock(BlockPos pos) {
//        IBlockState state = getBlockState(pos);
//        Block block = state.getBlock();
//        return block != null && !block.isAir(state, mc.world, pos) && !block.getBlockHardness(state, mc.world, pos) < 0;
//    }

    // Получаем углы для поворота к блоку
    public static float[] getLegitRotations(Vec3d target) {
        double diffX = target.x - mc.player.posX;
        double diffY = target.y - (mc.player.posY + mc.player.getEyeHeight());
        double diffZ = target.z - mc.player.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    // Получаем центральную точку блока
    public static Vec3d getBlockCenter(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    // Проверяем, является ли блок жидкостью
    public static boolean isLiquid(BlockPos pos) {
        IBlockState state = getBlockState(pos);
        return state.getMaterial().isLiquid();
    }

    // Проверяем, является ли блок твердым
    public static boolean isSolid(BlockPos pos) {
        IBlockState state = getBlockState(pos);
        return state.getMaterial().isSolid();
    }

    // Получаем прочность блока
    public static float getBlockHardness(BlockPos pos) {
        IBlockState state = getBlockState(pos);
        return state.getBlockHardness(mc.world, pos);
    }

    // Получаем направление для разрушения блока
    public static EnumFacing getFacingToBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        Vec3d blockCenter = getBlockCenter(pos);

        double diffX = blockCenter.x - eyesPos.x;
        double diffY = blockCenter.y - eyesPos.y;
        double diffZ = blockCenter.z - eyesPos.z;

        double distX = Math.abs(diffX);
        double distY = Math.abs(diffY);
        double distZ = Math.abs(diffZ);

        EnumFacing facing;

        if (distX > distZ) {
            if (diffX > 0) {
                facing = EnumFacing.WEST;
            } else {
                facing = EnumFacing.EAST;
            }
        } else {
            if (diffZ > 0) {
                facing = EnumFacing.NORTH;
            } else {
                facing = EnumFacing.SOUTH;
            }
        }

        if (distY > Math.max(distX, distZ)) {
            if (diffY > 0) {
                facing = EnumFacing.DOWN;
            } else {
                facing = EnumFacing.UP;
            }
        }

        return facing;
    }
}