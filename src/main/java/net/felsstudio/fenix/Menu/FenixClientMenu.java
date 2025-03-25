package net.felsstudio.fenix.Menu;

import net.felsstudio.fenix.Menu.Tools.changeUser;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FenixClientMenu extends GuiScreen {
    private static final ResourceLocation texture = new ResourceLocation("texture.jpg");

    public FenixClientMenu() {
        super();
    }

    @Override
    public void initGui() {
        int i = this.height / 4 + 48;
        this.buttonList.clear();
        int spacing = 24;

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i + 72 + spacing * 3, 98,
                20, "Options"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, i + 72 + spacing * 3, 98,
                20, "Quit"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, i + 72 + spacing * 2, 98,
                20, "Change User"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, i + 72 + spacing * 2, 98,
                20, "Author"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 72 + spacing, 200,
                20, "Multiplayer"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 100, i + 72, 200,
                20, "Mods"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, i + 72 - spacing, 200,
                20, "Singleplayer"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } if (button.id == 1) {
            mc.shutdown();
        } if (button.id == 2) {
            mc.displayGuiScreen(new changeUser());
        } if (button.id == 3) {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("https://www.youtube.com/@FelsStudio_official"));
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } if (button.id == 4) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } if (button.id == 5) {
            mc.displayGuiScreen(new GuiWorldSelection(this));
        } if (button.id == 6) {
            mc.displayGuiScreen(new GuiModList(this));
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1, 1, 1, 1);
        drawDefaultBackground();
        mc.renderEngine.bindTexture(texture);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, this.width, this.height, this.width, this.height, this.width, this.height);

        drawLogo.drawString(5, "FenixClient", this.width / 10 - this.fontRenderer.getStringWidth("FenixClient") / 2,
                this.height / 20, Color.ORANGE.getRGB());

        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
