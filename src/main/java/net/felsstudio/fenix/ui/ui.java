package net.felsstudio.fenix.ui;

import net.felsstudio.fenix.Client;
import net.felsstudio.fenix.FenixVariableManager;
import net.felsstudio.fenix.font.FontUtils;
import net.felsstudio.fenix.module.EXPLOIT.Panic;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static net.minecraft.client.gui.Gui.drawRect;

public class ui {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        switch (e.getType()) {
            case TEXT:
                if (!Panic.isPanic) {
                    if (FenixVariableManager.isCords) {
                        Minecraft mc = Minecraft.getMinecraft();
                        ScaledResolution sr = new ScaledResolution(mc);
                        int posX = 5; // Позиция по X
                        int posY = sr.getScaledHeight() - 30; // Позиция по Y (нижний угол)

                        // Получаем координаты игрока
                        int playerX = (int) mc.player.posX;
                        int playerY = (int) mc.player.posY;
                        int playerZ = (int) mc.player.posZ;

                        // Координаты для верхнего мира
                        String overworldCoords = String.format("Overworld: X: %d, Y: %d, Z: %d", playerX, playerY, playerZ);
                        // Координаты для нижнего мира (пересчитанные)
                        String netherCoords = String.format("nether: X: %d, Y: %d, Z: %d", playerX / 8, playerY, playerZ / 8);

                        // Отображаем координаты для верхнего мира
                        FontUtils.normal.drawString(overworldCoords, posX, posY, rainbow(0)); // Разноцветный текст
                        // Отображаем координаты для нижнего мира
                        FontUtils.normal.drawString(netherCoords, posX, posY + 10, rainbow(200)); // Разноцветный текст с другим оттенком
                    }

                    if(FenixVariableManager.isWatermark){
                        int y = 10;
                        final int[] counter = {1};

                        Minecraft mc = Minecraft.getMinecraft();
                        FontRenderer fr = mc.fontRenderer;
                        ScaledResolution sr = new ScaledResolution(mc);

                        int posY = 10;

                        try {
                            String text = Client.inGameName + "§f | " + mc.getSession().getUsername() + " | " + Objects.requireNonNull(mc.getCurrentServerData()).serverIP +
                                    " | FPS: §a" + Minecraft.getDebugFPS() + "§f | Ping: §a" + mc.getCurrentServerData().pingToServer;

                            drawRect(5, 5, FontUtils.normal.getStringWidth(text) > 190 ? (int) (FontUtils.normal.getStringWidth(text) + 14) : 200, 18, new Color(0x151515).hashCode());
                            drawRect(5, 5, FontUtils.normal.getStringWidth(text) > 190 ? (int) (FontUtils.normal.getStringWidth(text) + 14) : 200, 4, rainbow(300));

                            FontUtils.normal.drawString(text, 10, posY, -1);
                        } catch (Exception ex) {
                            drawRect(5, 5, 200, 18, new Color(0x151515).hashCode());
                            drawRect(5, 5, 200, 4, rainbow(300));

                            FontUtils.normal.drawString(Client.inGameName + "§f | " + mc.getSession().getUsername() +
                                    " | FPS: §a" + Minecraft.getDebugFPS(), 10, posY, -1);
                        }

                        ArrayList<Module> enabledMods = new ArrayList<>();

                        for (Module module : Client.modules) {
                            if (module.toggled) {
                                enabledMods.add(module);
                            }
                        }

                        enabledMods.sort((module1, module2) -> mc.fontRenderer.getStringWidth(module2.getName()) - mc.fontRenderer.getStringWidth(module1.getName()));

                        for (Module module : enabledMods) {
                            Gui.drawRect(sr.getScaledWidth(), y, sr.getScaledWidth() - 2,
                                    y + 10, rainbow(counter[0] * 300));

                            fr.drawStringWithShadow(module.name, sr.getScaledWidth() - 4 - fr.getStringWidth(module.name),
                                    y, rainbow(counter[0] * 300));
                            y += 10;
                            counter[0]++;
                        }
                    }
                }else{
                    Minecraft.getMinecraft().fontRenderer.drawString("§fFPS: §a" + Minecraft.getDebugFPS(), 5, 15, -1);
                }
                break;
            default:
                break;
        }
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f).getRGB();
    }

    public static int[] rainbowRGB(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        java.awt.Color color = java.awt.Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f);
        return new int[] {
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha()
        };
    }
}