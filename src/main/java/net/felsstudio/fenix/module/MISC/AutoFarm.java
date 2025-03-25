package net.felsstudio.fenix.module.MISC;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.BlockInteractionHelper;
import net.felsstudio.fenix.utils.Timer;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AutoFarm extends Module {

    private final Timer _timer = new Timer();
    private final HashMap<BlockPos, Item> plants = new HashMap<>();
    private BlockPos currentBlock;
    private float progress;
    private float prevProgress;

    public AutoFarm() {
        super("AutoFarm", Keyboard.KEY_NONE, Category.MISC);
        ExampleMod.instance.settingsManager.rSetting(new Setting("Range", this, 5.0, 1.0, 6.0, true));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        if (toggled) {
            double range = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Range").getValDouble();
            Vec3d eyesVec = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ).subtract(0.5, 0.5, 0.5);
            BlockPos eyesBlock = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            double rangeSq = Math.pow(range, 2);
            int blockRange = (int) Math.ceil(range);

            List<BlockPos> blocks = getBlockStream(eyesBlock, blockRange)
                    .filter(pos -> eyesVec.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
                    .filter(pos -> BlockInteractionHelper.canBeClicked(pos))
                    .collect(Collectors.toList());

            registerPlants(blocks);

            List<BlockPos> blocksToHarvest = blocks.parallelStream()
                    .filter(this::shouldBeHarvested)
                    .sorted(Comparator.comparingDouble(pos -> eyesVec.squareDistanceTo(new Vec3d(pos))))
                    .collect(Collectors.toList());

            List<BlockPos> blocksToReplant = getBlockStream(eyesBlock, blockRange)
                    .filter(pos -> eyesVec.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
                    .filter(pos -> mc.world.getBlockState(pos).getMaterial().isReplaceable())
                    .filter(pos -> plants.containsKey(pos))
                    .filter(this::canBeReplanted)
                    .sorted(Comparator.comparingDouble(pos -> eyesVec.squareDistanceTo(new Vec3d(pos))))
                    .collect(Collectors.toList());

            while (!blocksToReplant.isEmpty()) {
                BlockPos pos = blocksToReplant.get(0);
                Item neededItem = plants.get(pos);
                if (tryToReplant(pos, neededItem))
                    break;

                blocksToReplant.removeIf(p -> plants.get(p) == neededItem);
            }

            if (blocksToReplant.isEmpty())
                harvest(blocksToHarvest);
        }
    }

    private Stream<BlockPos> getBlockStream(BlockPos center, int range) {
        return StreamSupport.stream(
                BlockPos.getAllInBox(
                        center.add(range, range, range),
                        center.add(-range, -range, -range)
                ).spliterator(), false // Параллельный поток не нужен
        );
    }

    private boolean shouldBeHarvested(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (block instanceof BlockCrops)
            return ((BlockCrops) block).isMaxAge(mc.world.getBlockState(pos));
        else if (block instanceof BlockPumpkin || block instanceof BlockMelon)
            return true;
        else if (block instanceof BlockReed)
            return mc.world.getBlockState(pos.down()).getBlock() instanceof BlockReed
                    && !(mc.world.getBlockState(pos.down(2)).getBlock() instanceof BlockReed);
        else if (block instanceof BlockCactus)
            return mc.world.getBlockState(pos.down()).getBlock() instanceof BlockCactus
                    && !(mc.world.getBlockState(pos.down(2)).getBlock() instanceof BlockCactus);
        else if (block instanceof BlockNetherWart)
            return mc.world.getBlockState(pos).getValue(BlockNetherWart.AGE) >= 3;

        return false;
    }

    private void registerPlants(List<BlockPos> blocks) {
        HashMap<Block, Item> seeds = new HashMap<>();
        seeds.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        seeds.put(Blocks.CARROTS, Items.CARROT);
        seeds.put(Blocks.POTATOES, Items.POTATO);
        seeds.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        seeds.put(Blocks.PUMPKIN_STEM, Items.PUMPKIN_SEEDS);
        seeds.put(Blocks.MELON_STEM, Items.MELON_SEEDS);
        seeds.put(Blocks.NETHER_WART, Items.NETHER_WART);

        plants.putAll(blocks.parallelStream()
                .filter(pos -> seeds.containsKey(mc.world.getBlockState(pos).getBlock()))
                .collect(Collectors.toMap(pos -> pos, pos -> seeds.get(mc.world.getBlockState(pos).getBlock()))));
    }

    private boolean canBeReplanted(BlockPos pos) {
        Item item = plants.get(pos);

        if (item == Items.WHEAT_SEEDS || item == Items.CARROT
                || item == Items.POTATO || item == Items.BEETROOT_SEEDS
                || item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS)
            return mc.world.getBlockState(pos.down()).getBlock() instanceof BlockFarmland;

        if (item == Items.NETHER_WART)
            return mc.world.getBlockState(pos.down()).getBlock() instanceof BlockSoulSand;

        return false;
    }

    private boolean tryToReplant(BlockPos pos, Item neededItem) {
        ItemStack heldItem = mc.player.getHeldItemMainhand();

        if (!heldItem.isEmpty() && heldItem.getItem() == neededItem) {
            BlockInteractionHelper.place(pos, 5.0f, false, false, true);
            return true;
        }

        for (int slot = 0; slot < 36; slot++) {
            if (slot == mc.player.inventory.currentItem)
                continue;

            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getItem() != neededItem)
                continue;

            if (slot < 9)
                mc.player.inventory.currentItem = slot;
            else if (mc.player.inventory.getFirstEmptyStack() < 9)
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
            else if (mc.player.inventory.getFirstEmptyStack() != -1) {
                mc.playerController.windowClick(0, mc.player.inventory.currentItem + 36, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
            } else {
                mc.playerController.windowClick(0, mc.player.inventory.currentItem + 36, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, mc.player.inventory.currentItem + 36, 0, ClickType.PICKUP, mc.player);
            }

            return true;
        }

        return false;
    }

    private void harvest(List<BlockPos> blocksToHarvest) {
        for (BlockPos pos : blocksToHarvest) {
            if (BlockInteractionHelper.breakBlock(pos)) {
                currentBlock = pos;
                break;
            }
        }

        if (currentBlock == null)
            mc.playerController.resetBlockRemoving();

        if (currentBlock != null && mc.world.getBlockState(currentBlock).getBlockHardness(mc.world, currentBlock) < 1) {
            prevProgress = progress;

            if (progress < prevProgress)
                prevProgress = progress;
        } else {
            progress = 1;
            prevProgress = 1;
        }
    }
}