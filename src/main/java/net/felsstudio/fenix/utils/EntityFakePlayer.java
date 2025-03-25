package net.felsstudio.fenix.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class EntityFakePlayer extends AbstractClientPlayer {

    private final EntityPlayer realPlayer;

    public EntityFakePlayer() {
        super(
                Minecraft.getMinecraft().world, // Мир, в котором находится игрок
                Minecraft.getMinecraft().player.getGameProfile() // Профиль игрока
        );

        this.realPlayer = Minecraft.getMinecraft().player;

        // Копируем позицию и состояние реального игрока
        this.copyLocationAndAnglesFrom(realPlayer);
        this.rotationYawHead = realPlayer.rotationYawHead;
        this.renderYawOffset = realPlayer.renderYawOffset;

        // Копируем инвентарь
        this.inventory.copyInventory(realPlayer.inventory);

        // Копируем состояние
        this.setHealth(realPlayer.getHealth());
        this.setAbsorptionAmount(realPlayer.getAbsorptionAmount());
        this.setSprinting(realPlayer.isSprinting());
        this.setSneaking(realPlayer.isSneaking());

        // Добавляем фейкового игрока в мир
        Minecraft.getMinecraft().world.addEntityToWorld(getEntityId(), this);
    }

    /**
     * Возвращает реального игрока в исходное положение.
     */
    public void resetPlayerPosition() {
        realPlayer.copyLocationAndAnglesFrom(this);
        realPlayer.rotationYawHead = this.rotationYawHead;
        realPlayer.renderYawOffset = this.renderYawOffset;
    }

    /**
     * Удаляет фейкового игрока из мира.
     */
    public void despawn() {
        Minecraft.getMinecraft().world.removeEntityFromWorld(getEntityId());
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public NetworkPlayerInfo getPlayerInfo() {
        return Minecraft.getMinecraft().getConnection().getPlayerInfo(getUniqueID());
    }
}