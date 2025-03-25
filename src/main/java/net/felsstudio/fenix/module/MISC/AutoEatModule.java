package net.felsstudio.fenix.module.MISC;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class AutoEatModule extends Module {

    public AutoEatModule() {
        super("AutoEat", Keyboard.KEY_NONE, Module.Category.MISC);

        // Настройки модуля
        ExampleMod.instance.settingsManager.rSetting(new Setting("HealthToEatAt", this, 15.0, 0.0, 36.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("RequiredHunger", this, 18.0, 0.0, 20.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("PreferGoldenApples", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("EatInGui", this, false));
    }

    private boolean wasEating = false;

    @Override
    public void onDisable() {
        super.onDisable();

        // Отключаем удержание кнопки использования, если модуль выключен
        if (wasEating) {
            wasEating = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            double healthToEatAt = ExampleMod.instance.settingsManager.getSettingByName(this.name, "HealthToEatAt").getValDouble();
            double requiredHunger = ExampleMod.instance.settingsManager.getSettingByName(this.name, "RequiredHunger").getValDouble();
            boolean preferGoldenApples = ExampleMod.instance.settingsManager.getSettingByName(this.name, "PreferGoldenApples").getValBoolean();
            boolean eatInGui = ExampleMod.instance.settingsManager.getSettingByName(this.name, "EatInGui").getValBoolean();

            // Едим золотые яблоки, если здоровье ниже порога
            if (healthToEatAt >= health && !PlayerUtil.IsEating()) {
                if (preferGoldenApples) {
                    if (tryEatItem(Items.GOLDEN_APPLE, eatInGui)) {
                        wasEating = true;
                        return;
                    }
                }
            }

            // Едим обычную еду, если уровень голода ниже порога
            if (!PlayerUtil.IsEating() && requiredHunger >= mc.player.getFoodStats().getFoodLevel()) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);

                    if (stack.isEmpty()) continue;

                    if (stack.getItem() instanceof ItemFood) {
                        if (tryEatItem(stack.getItem(), eatInGui)) {
                            wasEating = true;
                            break;
                        }
                    }
                }
            }

            // Отключаем удержание кнопки использования, если не едим
            if (wasEating && !PlayerUtil.IsEating()) {
                wasEating = false;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
        }
    }

    private boolean tryEatItem(Item item, boolean eatInGui) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.isEmpty() || stack.getItem() != item) continue;

            mc.player.inventory.currentItem = i;
            mc.playerController.updateController();

            if (mc.currentScreen == null || eatInGui) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                return true;
            } else {
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                return true;
            }
        }
        return false;
    }
}