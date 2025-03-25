package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.felsstudio.clickgui.Setting;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockESP extends Module {

    public static final Set<Block> espBlocks = new HashSet<>();

    public BlockESP() {
        super("BlockESP", Keyboard.KEY_NONE, Category.RENDER);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Radius", this,16, 8, 512, true));
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        // Пример добавления блоков по умолчанию
        espBlocks.add(Blocks.DIAMOND_ORE);
        espBlocks.add(Blocks.GOLD_ORE);
        espBlocks.add(Blocks.EMERALD_ORE);
    }//(int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Radius").getValDouble();

    private int tickCounter = 0;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Set<BlockPos> cachedPositions = new HashSet<>();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.player == null || mc.world == null || !toggled) return;

        // Обновляем кэш только раз в 20 тиков
        if (tickCounter++ % 20 == 0) {
            executor.submit(this::updateBlockCache);
        }

        // Рендерим блоки из кэша
        for (BlockPos pos : cachedPositions) {
            drawBlockESP(pos, event.getPartialTicks());
        }
    }

    private void updateBlockCache() {
        BlockPos playerPos = mc.player.getPosition();
        int radius = (int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Radius").getValDouble();
        Set<BlockPos> newCache = new HashSet<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();

                    if (espBlocks.contains(block)) {
                        newCache.add(pos);
                    }
                }
            }
        }

        // Обновляем кэш
        cachedPositions = newCache;
    }

    private void drawBlockESP(BlockPos pos, float partialTicks) {
        // Настройки рендера
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        // Цвет ESP (красный)
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);

        // Получаем AABB блока
        AxisAlignedBB bb = new AxisAlignedBB(pos).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

        // Рисуем контур блока
        RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 1.0f, 1.0f, 1.0f);

        // Восстанавливаем настройки рендера
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void addBlock(Block block) {
        espBlocks.add(block);
    }

    public static void removeBlock(Block block) {
        espBlocks.remove(block);
    }

    public static Set<Block> getEspBlocks() {
        return espBlocks;
    }
}