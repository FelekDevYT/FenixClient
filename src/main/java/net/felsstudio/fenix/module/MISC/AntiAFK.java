package net.felsstudio.fenix.module.MISC;

import net.felsstudio.fenix.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiAFK extends Module {
    private long lastActionTime = 0;
    private static final long ACTION_INTERVAL = 120000; // 2 минуты в миллисекундах

    public AntiAFK() {
        super("AntiAFK", 0, Module.Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastActionTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(toggled){
            if (event.player == null || event.player != mc.player) return;

            long currentTime = System.currentTimeMillis();

            // Проверяем, прошло ли достаточно времени для выполнения действия
            if (currentTime - lastActionTime >= ACTION_INTERVAL) {
                performAntiAFKAction();
                lastActionTime = currentTime;
            }
        }
    }

    private void performAntiAFKAction() {
        if (mc.player == null || mc.world == null) return;

        mc.player.sendChatMessage("/stats");
    }
}