package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class FullBright extends Module {
    private float old_gamma;

    public FullBright(){
        super("Fullbright", Keyboard.KEY_NONE, Category.RENDER);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Gamma", this, 999, 1, 2048, true));
    }

    @Override
    public void onEnable() {
        old_gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = (float) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Gamma").getValDouble();
    }

    @Override
    public void onDisable(){
        mc.gameSettings.gammaSetting = old_gamma;
    }
}
