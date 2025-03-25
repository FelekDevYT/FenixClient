package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Radar extends Module {

    private final Setting radius = new Setting("Radius", this, 1, 100, 1, false);
    private final Setting rotate = new Setting("Rotate with player",this, true);

    private final Setting filterPlayers = new Setting("Filter players", this, false);
    private final Setting filterSleeping = new Setting("Filter sleeping", this, false);
    private final Setting filterMonsters = new Setting("Filter monsters", this, false);
    private final Setting filterAnimals = new Setting("Filter animals", this, false);
    private final Setting filterInvisible = new Setting("Filter invisible", this, false);

    private final List<Entity> entities = new ArrayList<>();

    public Radar() {
        super("Radar", Keyboard.KEY_NONE, Category.RENDER);

        ExampleMod.instance.settingsManager.rSetting(radius);
        ExampleMod.instance.settingsManager.rSetting(rotate);
        ExampleMod.instance.settingsManager.rSetting(filterPlayers);
        ExampleMod.instance.settingsManager.rSetting(filterSleeping);
        ExampleMod.instance.settingsManager.rSetting(filterMonsters);
        ExampleMod.instance.settingsManager.rSetting(filterAnimals);
        ExampleMod.instance.settingsManager.rSetting(filterInvisible);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayerSP player = mc.player;
        World world = mc.world;

        entities.clear();
        Stream<Entity> stream = world.loadedEntityList.parallelStream()
                .filter(e -> !e.isDead && e != player)
                .filter(e -> e instanceof EntityLivingBase)
                .filter(e -> ((EntityLivingBase) e).getHealth() > 0);

        if (filterPlayers.getValBoolean()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer));
        }

        if (filterSleeping.getValBoolean()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer && ((EntityPlayer) e).isPlayerSleeping()));
        }

        if (filterMonsters.getValBoolean()) {
            stream = stream.filter(e -> !(e instanceof IMob));
        }

        if (filterAnimals.getValBoolean()) {
            stream = stream.filter(e -> !(e instanceof EntityAnimal || e instanceof EntityAmbientCreature || e instanceof EntityWaterMob));
        }

        if (filterInvisible.getValBoolean()) {
            stream = stream.filter(e -> !e.isInvisible());
        }

        entities.addAll(stream.collect(Collectors.toList()));
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public double getRadius() {
        return radius.getValDouble();
    }

    public boolean isRotateEnabled() {
        return rotate.getValBoolean();
    }
}