package net.felsstudio.fenix.module.PLAYER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

public class BlockReach extends Module {
    public BlockReach(){
        super("Block reach", Keyboard.KEY_NONE, Category.PLAYER);

        ExampleMod.instance.settingsManager.rSetting(new Setting("Size", this, 0.5, 0, 4, false));
    }

    @Override
    public void onEnable(){
        EntityPlayer player = mc.player;
        IAttributeInstance setBlockReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier(player.getUniqueID(), "custom_reach", ExampleMod.instance.settingsManager.getSettingByName(this.name, "Size").getValDouble(), 1));
    }

    @Override
    public void onDisable(){
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(mc.player.getUniqueID());
    }
}
