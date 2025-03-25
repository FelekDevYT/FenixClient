package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;

public class AutoTotem extends Module {

    public AutoTotem() {
        super("AutoTotem", Keyboard.KEY_NONE, Category.COMBAT);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Totem");
        modes.add("Gap");
        modes.add("Crystal");
        modes.add("Pearl");
        modes.add("Chorus");
        modes.add("Strength");
        modes.add("Shield");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Fallback", this, modes, "FallbackMode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Health", this, 16.0, 0.0, 20.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("FallDistance", this, 15.0, 0.0, 100.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("TotemOnElytra", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("SwordGap", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Strength", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("HotbarFirst", this, false));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory)) return;

            double health = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Health").getValDouble();
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
            String fallbackMode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Fallback").getValString();
            double fallDistance = ExampleMod.instance.settingsManager.getSettingByName(this.name, "FallDistance").getValDouble();
            boolean totemOnElytra = ExampleMod.instance.settingsManager.getSettingByName(this.name, "TotemOnElytra").getValBoolean();
            boolean swordGap = ExampleMod.instance.settingsManager.getSettingByName(this.name, "SwordGap").getValBoolean();
            boolean strength = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Strength").getValBoolean();
            boolean hotbarFirst = ExampleMod.instance.settingsManager.getSettingByName(this.name, "HotbarFirst").getValBoolean();

            if (!mc.player.getHeldItemMainhand().isEmpty()) {
                if (health <= getHealthWithAbsorption() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && strength && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                    switchOffHandIfNeeded(AutoTotemMode.Strength);
                    return;
                }

                if (health <= getHealthWithAbsorption() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && swordGap) {
                    switchOffHandIfNeeded(AutoTotemMode.Gap);
                    return;
                }
            }

            if (health > getHealthWithAbsorption() || mode.equals("Totem") || (totemOnElytra && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallDistance && !mc.player.isElytraFlying()) || noNearbyPlayers()) {
                switchOffHandIfNeeded(AutoTotemMode.Totem);
                return;
            }

            switchOffHandIfNeeded(AutoTotemMode.valueOf(mode));
        }
    }

    private void switchOffHandIfNeeded(AutoTotemMode mode) {
        Item item = getItemFromMode(mode);

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = ExampleMod.instance.settingsManager.getSettingByName(this.name, "HotbarFirst").getValBoolean() ? getRecursiveItemSlot(item) : getItemSlot(item);

            Item fallback = getItemFromMode(AutoTotemMode.valueOf(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Fallback").getValString()));

            String display = getItemNameFromMode(mode);

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = getRecursiveItemSlot(fallback);
                display = getItemNameFromMode(AutoTotemMode.valueOf(ExampleMod.instance.settingsManager.getSettingByName(this.name, "Fallback").getValString()));

                if (slot == -1 && fallback != Items.TOTEM_OF_UNDYING) {
                    fallback = Items.TOTEM_OF_UNDYING;

                    if (item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                        slot = getRecursiveItemSlot(fallback);
                        display = "Emergency Totem";
                    }
                }
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();

                ChatUtils.sendMessage("Offhand now has a " + display);
            }
        }
    }

    private Item getItemFromMode(AutoTotemMode mode) {
        switch (mode) {
            case Crystal:
                return Items.END_CRYSTAL;
            case Gap:
                return Items.GOLDEN_APPLE;
            case Pearl:
                return Items.ENDER_PEARL;
            case Chorus:
                return Items.CHORUS_FRUIT;
            case Strength:
                return Items.POTIONITEM;
            case Shield:
                return Items.SHIELD;
            default:
                return Items.TOTEM_OF_UNDYING;
        }
    }

    private String getItemNameFromMode(AutoTotemMode mode) {
        switch (mode) {
            case Crystal:
                return "End Crystal";
            case Gap:
                return "Gap";
            case Pearl:
                return "Pearl";
            case Chorus:
                return "Chorus";
            case Strength:
                return "Strength";
            case Shield:
                return "Shield";
            default:
                return "Totem";
        }
    }

    private boolean noNearbyPlayers() {
        return ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString().equals("Crystal") && mc.world.playerEntities.stream().noneMatch(e -> e != mc.player);
    }

    private float getHealthWithAbsorption() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private int getItemSlot(Item item) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    private int getRecursiveItemSlot(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                return i + 36;
            }
        }
        return getItemSlot(item);
    }

    public enum AutoTotemMode {
        Totem,
        Gap,
        Crystal,
        Pearl,
        Chorus,
        Strength,
        Shield
    }
}