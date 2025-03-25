package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class Particles extends Module {
    public Particles() {
        super("Particles", Keyboard.KEY_NONE, Category.COMBAT);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Count", this, 12, 2, 32, true));
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        if(toggled){
            for (int i = (int)ExampleMod.instance.settingsManager.getSettingByName(this.name, "Count").getValDouble(); i >= 0; i = i - 1) {
                mc.player.onCriticalHit(e.getTarget());
            }
        }
    }
}
