package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class AutoArmor extends Module {

    public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_NONE, Category.COMBAT);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Delay", this, 50.0, 0.0, 1000.0, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Curse", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Elytra", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("ElytraReplace", this, false));
    }

    private long lastEquipTime = 0;

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (toggled) {
            if (mc.currentScreen instanceof GuiInventory) return;

            double delay = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Delay").getValDouble();
            boolean curse = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Curse").getValBoolean();
            boolean preferElytra = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Elytra").getValBoolean();
            boolean elytraReplace = ExampleMod.instance.settingsManager.getSettingByName(this.name, "ElytraReplace").getValBoolean();

            if (System.currentTimeMillis() - lastEquipTime < delay) return;

            switchItemIfNeeded(mc.player.inventoryContainer.getSlot(5).getStack(), EntityEquipmentSlot.HEAD, 5, curse);
            switchItemIfNeeded(mc.player.inventoryContainer.getSlot(6).getStack(), EntityEquipmentSlot.CHEST, 6, curse);
            switchItemIfNeeded(mc.player.inventoryContainer.getSlot(7).getStack(), EntityEquipmentSlot.LEGS, 7, curse);
            switchItemIfNeeded(mc.player.inventoryContainer.getSlot(8).getStack(), EntityEquipmentSlot.FEET, 8, curse);

            if (elytraReplace && !mc.player.inventoryContainer.getSlot(6).getStack().isEmpty()) {
                ItemStack stack = mc.player.inventoryContainer.getSlot(6).getStack();

                if (stack.getItem() instanceof ItemElytra) {
                    if (!ItemElytra.isUsable(stack) && getDurabilityPercent(stack) < 3) {
                        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
                            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;

                            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
                            if (s != null && s.getItem() != Items.AIR) {
                                if (s.getItem() instanceof ItemElytra && ItemElytra.isUsable(s)) {
                                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
                                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void switchItemIfNeeded(ItemStack stack, EntityEquipmentSlot slot, int armorSlot, boolean curse) {
        if (stack.getItem() == Items.AIR) {
            int foundSlot = findArmorSlot(slot, curse);

            if (foundSlot != -1) {
                lastEquipTime = System.currentTimeMillis();

                if (foundSlot <= 4) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, armorSlot, 0, ClickType.PICKUP, mc.player);
                } else {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.QUICK_MOVE, mc.player);
                }
            }
        }
    }

    private int findArmorSlot(EntityEquipmentSlot type, boolean curse) {
        int slot = -1;
        float damage = 0;

        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;

            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
            if (s != null && s.getItem() != Items.AIR) {
                if (s.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) s.getItem();
                    if (armor.armorType == type) {
                        float currentDamage = (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s));

                        boolean isCursed = curse && EnchantmentHelper.hasBindingCurse(s);

                        if (currentDamage > damage && !isCursed) {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                } else if (type == EntityEquipmentSlot.CHEST && ExampleMod.instance.settingsManager.getSettingByName(this.name, "Elytra").getValBoolean() && s.getItem() instanceof ItemElytra && getDurabilityPercent(s) > 3) {
                    return i;
                }
            }
        }

        return slot;
    }

    private float getDurabilityPercent(ItemStack stack) {
        return (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100.0f;
    }
}