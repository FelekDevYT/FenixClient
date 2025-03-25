package net.felsstudio.fenix.module.FENIX;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.ui.ui;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.fenix.FenixVariableManager;
import net.felsstudio.clickgui.Setting;

import java.awt.*;

public class ClickGUI extends Module {
    public ClickGUI(){
        super("ClickGUI", Keyboard.KEY_NONE, Category.FENIX);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Rainbow", this, false));

        ExampleMod.instance.settingsManager.rSetting(new Setting("RColor", this, 255,  1, 255, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("GColor", this, 200, 1, 255, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("BColor", this, 0, 1, 255, true));
    }

    int counter = 0;

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if(toggled){
            if(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Rainbow").getValBoolean()){
                FenixVariableManager.mainColor = new Color(ui.rainbow(300));
                counter++;
            }else{
                FenixVariableManager.mainColor = new Color((int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "RColor").getValDouble(),
                        (int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "GColor").getValDouble(),
                        (int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "BColor").getValDouble());
            }
        }
    }
}
