package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.module.EXPLOIT.Panic;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import org.lwjgl.input.Keyboard;

public class FakePlayer extends Module {
    EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super("Fake player", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    public void onEnable() {
        fakePlayer = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        fakePlayer.setEntityId(-1882);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
    }

    @Override
    public void onDisable() {
        if (!Panic.isPanic) {
            mc.world.removeEntityFromWorld(fakePlayer.getEntityId());
        }
    }
}
