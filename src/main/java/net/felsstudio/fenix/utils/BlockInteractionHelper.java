package net.felsstudio.fenix.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockInteractionHelper {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public enum ValidResult {
        Ok,
        AlreadyBlockThere,
        NotReplaceable,
        NoNeighbor,
        NoPlacement
    }

    public enum PlaceResult {
        Placed,
        NotPlaced,
        Failed
    }

    public static boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    public static boolean breakBlock(BlockPos pos) {
        if (!canBeClicked(pos))
            return false;

        mc.playerController.clickBlock(pos, EnumFacing.UP);
        return true;
    }


    public static ValidResult valid(BlockPos pos) {
        if (mc.world == null || pos == null)
            return ValidResult.NoPlacement;

        if (mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return ValidResult.Ok;

        if (mc.world.getBlockState(pos).getBlock().getMaterial(mc.world.getBlockState(pos)).isSolid())
            return ValidResult.AlreadyBlockThere;

        return ValidResult.NotReplaceable;
    }

    public static PlaceResult place(BlockPos pos, float range, boolean rotate, boolean rayTrace, boolean useSlabRule) {
        if (mc.player == null || mc.world == null || pos == null)
            return PlaceResult.Failed;

        // Сохраняем текущие углы поворота игрока
        float prevYaw = mc.player.rotationYaw;
        float prevPitch = mc.player.rotationPitch;

        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                Vec3d hitVec = new Vec3d(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= range) {
                    float[] rotations = getFacingRotations(pos.getX(), pos.getY(), pos.getZ(), side);

                    if (rotate) {
                        mc.player.rotationYaw = rotations[1];
                        mc.player.rotationPitch = rotations[0];
                    }

                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    // Восстанавливаем углы поворота игрока
                    mc.player.rotationYaw = prevYaw;
                    mc.player.rotationPitch = prevPitch;

                    return PlaceResult.Placed;
                }
            }
        }

        // Восстанавливаем углы поворота игрока, если блок не был размещен
        mc.player.rotationYaw = prevYaw;
        mc.player.rotationPitch = prevPitch;

        return PlaceResult.NotPlaced;
    }

    public static float[] getFacingRotations(int x, int y, int z, EnumFacing facing) {
        Vec3d vec = new Vec3d(x + 0.5, y + 0.5, z + 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        return getRotations(vec);
    }

    public static float[] getRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{pitch, yaw};
    }
}