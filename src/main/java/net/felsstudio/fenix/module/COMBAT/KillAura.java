package net.felsstudio.fenix.module.COMBAT;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.felsstudio.clickgui.Setting;

import java.util.ArrayList;
import java.util.Comparator;

public class KillAura extends Module {

    private final Timer _timer = new Timer();
    private Entity _currentTarget;

    public KillAura() {
        super("Kill Aura", Keyboard.KEY_NONE, Category.COMBAT);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Closest");
        modes.add("Priority");
        modes.add("Switch");

        ExampleMod.instance.settingsManager.rSetting(new Setting("Mode", this, modes, "Mode"));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Range", this, 5.0, 0.0, 10.0, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("HitDelay", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("TPSSync", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Players", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Monsters", this, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Neutrals", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Animals", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Tamed", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Projectiles", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("SwordOnly", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("PauseIfCrystal", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("PauseIfEating", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("AutoSwitch", this, false));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Ticks", this, 10, 0, 40, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("Iterations", this, 1, 1, 10, true));
        ExampleMod.instance.settingsManager.rSetting(new Setting("32kOnly", this, false));
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (toggled) {
            String mode = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Mode").getValString();
            double range = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Range").getValDouble();
            boolean hitDelay = ExampleMod.instance.settingsManager.getSettingByName(this.name, "HitDelay").getValBoolean();
            boolean tpsSync = ExampleMod.instance.settingsManager.getSettingByName(this.name, "TPSSync").getValBoolean();
            boolean players = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Players").getValBoolean();
            boolean monsters = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Monsters").getValBoolean();
            boolean neutrals = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Neutrals").getValBoolean();
            boolean animals = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Animals").getValBoolean();
            boolean tamed = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Tamed").getValBoolean();
            boolean projectiles = ExampleMod.instance.settingsManager.getSettingByName(this.name, "Projectiles").getValBoolean();
            boolean swordOnly = ExampleMod.instance.settingsManager.getSettingByName(this.name, "SwordOnly").getValBoolean();
            boolean pauseIfCrystal = ExampleMod.instance.settingsManager.getSettingByName(this.name, "PauseIfCrystal").getValBoolean();
            boolean pauseIfEating = ExampleMod.instance.settingsManager.getSettingByName(this.name, "PauseIfEating").getValBoolean();
            boolean autoSwitch = ExampleMod.instance.settingsManager.getSettingByName(this.name, "AutoSwitch").getValBoolean();
            int ticks = (int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Ticks").getValDouble();
            int iterations = (int) ExampleMod.instance.settingsManager.getSettingByName(this.name, "Iterations").getValDouble();
            boolean only32k = ExampleMod.instance.settingsManager.getSettingByName(this.name, "32kOnly").getValBoolean();

            if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && pauseIfCrystal)
                    return;

                if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && pauseIfEating)
                    return;

                int slot = -1;

                if (autoSwitch) {
                    for (int i = 0; i < 9; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemSword) {
                            slot = i;
                            mc.player.inventory.currentItem = slot;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }

                if (swordOnly && slot == -1)
                    return;
            }

            if (only32k && !is32k(mc.player.getHeldItemMainhand()))
                return;

            Entity targetToHit = _currentTarget;

            switch (mode) {
                case "Closest":
                    targetToHit = mc.world.loadedEntityList.stream()
                            .filter(entity -> isValidTarget(entity, range, players, monsters, neutrals, animals, tamed, projectiles))
                            .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                            .orElse(null);
                    break;
                case "Priority":
                    if (targetToHit == null) {
                        targetToHit = mc.world.loadedEntityList.stream()
                                .filter(entity -> isValidTarget(entity, range, players, monsters, neutrals, animals, tamed, projectiles))
                                .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                                .orElse(null);
                    }
                    break;
                case "Switch":
                    targetToHit = mc.world.loadedEntityList.stream()
                            .filter(entity -> isValidTarget(entity, range, players, monsters, neutrals, animals, tamed, projectiles))
                            .min(Comparator.comparing(entity -> mc.player.getDistance(entity)))
                            .orElse(null);

                    if (targetToHit == null)
                        targetToHit = _currentTarget;

                    break;
                default:
                    break;
            }

            if (targetToHit == null || targetToHit.getDistance(mc.player) > range) {
                _currentTarget = null;
                return;
            }

            _currentTarget = targetToHit;

            float[] rotations = getRotations(targetToHit);
            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1];

            boolean isAttackReady = !hitDelay || (mc.player.getCooledAttackStrength(tpsSync ? -ticks : 0.0f) >= 1);

            if (!isAttackReady)
                return;

            if (!hitDelay && _timer.passed(ticks * 50))
                return;

            _timer.reset();

            for (int i = 0; i < iterations; ++i) {
                mc.player.connection.sendPacket(new CPacketUseEntity(targetToHit));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.resetCooldown();
            }
        }
    }

    private boolean isValidTarget(Entity entity, double range, boolean players, boolean monsters, boolean neutrals, boolean animals, boolean tamed, boolean projectiles) {
        if (!(entity instanceof EntityLivingBase)) {
            boolean isProjectile = (entity instanceof EntityShulkerBullet || entity instanceof EntityFireball);

            if (!isProjectile)
                return false;

            if (isProjectile && !projectiles)
                return false;
        }

        if (entity == mc.player)
            return false;

        if (entity instanceof EntityPlayer) {
            if (!players)
                return false;

        }

        if (isHostileMob(entity) && !monsters)
            return false;

        if (isPassive(entity)) {
            if (entity instanceof AbstractChestHorse) {
                AbstractChestHorse horse = (AbstractChestHorse) entity;

                if (horse.isTame() && !tamed)
                    return false;
            }

            if (!animals)
                return false;
        }

        if (isNeutralMob(entity) && !neutrals)
            return false;

        boolean healthCheck = true;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase base = (EntityLivingBase) entity;

            healthCheck = !base.isDead && base.getHealth() > 0.0f;
        }

        return healthCheck && entity.getDistance(mc.player) <= range;
    }

    private boolean isHostileMob(Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast;
    }

    private boolean isPassive(Entity entity) {
        return entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityBat;
    }

    private boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityEnderman || entity instanceof EntityWolf;
    }

    private boolean is32k(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSword))
            return false;

        // Логика проверки на 32k (например, проверка на наличие определенных NBT-тегов)
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("32k");
    }

    private float[] getRotations(Entity entity) {
        double diffX = entity.posX - mc.player.posX;
        double diffY = entity.posY + entity.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight());
        double diffZ = entity.posZ - mc.player.posZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0 / Math.PI);

        return new float[]{yaw, pitch};
    }
}