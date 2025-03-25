package net.felsstudio.fenix.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    // Получаем позицию игрока (округленную вниз)
    public static BlockPos getPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean IsEating() {
        if (mc.player == null || mc.player.getHeldItemMainhand().isEmpty()) {
            return false;
        }

        ItemStack heldItem = mc.player.getHeldItemMainhand();

        // Проверяем, является ли предмет едой или зельем
        boolean isFood = heldItem.getItem() instanceof ItemFood;
        boolean isPotion = heldItem.getItem() instanceof ItemPotion;

        // Проверяем, использует ли игрок предмет (например, ест или пьет)
        return (isFood || isPotion) && mc.player.isHandActive();
    }

    // Получаем направление, в котором смотрит игрок
    public static EnumFacing getFacing() {
        float yaw = mc.player.rotationYaw;
        if (yaw < 0) yaw += 360;
        yaw %= 360;

        if (yaw >= 45 && yaw < 135) {
            return EnumFacing.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return EnumFacing.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return EnumFacing.EAST;
        } else {
            return EnumFacing.SOUTH;
        }
    }

    // Поворачиваем игрока к указанным координатам
    public static void facePitchAndYaw(float pitch, float yaw) {
        mc.player.rotationPitch = pitch;
        mc.player.rotationYaw = yaw;
    }

    // Получаем углы для поворота к указанной позиции
    public static float[] getLegitRotations(Vec3d target) {
        double diffX = target.x - mc.player.posX;
        double diffY = target.y - (mc.player.posY + mc.player.getEyeHeight());
        double diffZ = target.z - mc.player.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }
}