package net.felsstudio.fenix.module.MISC;

import net.felsstudio.fenix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AutoTool extends Module {

    private int previousSlot = -1;
    private boolean isMining = false;

    public AutoTool() {
        super("AutoTool", Keyboard.KEY_NONE, Category.MISC);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player == Minecraft.getMinecraft().player) {
            if (Minecraft.getMinecraft().playerController.getIsHittingBlock()) {
                if (!isMining) {
                    isMining = true;
                    previousSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                }
                BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
                if (pos != null) {
                    int bestSlot = getBestToolSlot(pos);
                    if (bestSlot != -1) {
                        Minecraft.getMinecraft().player.inventory.currentItem = bestSlot;
                    }
                }
            } else {
                if (isMining) {
                    isMining = false;
                    if (previousSlot != -1) {
                        Minecraft.getMinecraft().player.inventory.currentItem = previousSlot;
                        previousSlot = -1;
                    }
                }
            }
        }
    }

    private int getBestToolSlot(BlockPos pos) {
        int bestSlot = -1;
        float bestSpeed = 1.0F;

        IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
        Block block = state.getBlock();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (stack != null && stack != ItemStack.EMPTY) {
                float speed = getDestroySpeed(stack, state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }

        return bestSlot;
    }

    private float getDestroySpeed(ItemStack stack, IBlockState state) {
        float speed = stack.getDestroySpeed(state);

        if (speed > 1.0F) {
            int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            if (efficiency > 0) {
                speed += efficiency * efficiency + 1;
            }
        }

        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.HASTE)) {
            speed *= 1.0F + (Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float fatigueMultiplier;
            switch (Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    fatigueMultiplier = 0.3F;
                    break;
                case 1:
                    fatigueMultiplier = 0.09F;
                    break;
                case 2:
                    fatigueMultiplier = 0.0027F;
                    break;
                default:
                    fatigueMultiplier = 8.1E-4F;
            }
            speed *= fatigueMultiplier;
        }

        return speed;
    }
}