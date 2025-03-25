package net.felsstudio.fenix.module.RENDER;

import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.utils.EntityFakePlayer;
import net.felsstudio.fenix.utils.RenderUtils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.felsstudio.clickgui.Setting;

public class Freecam extends Module {

    private final Setting speed = new Setting("Speed", this, 1, 0.05, 10,true);
    private final Setting tracer = new Setting("Tracer", this, false);

    private EntityFakePlayer fakePlayer;
    private int playerBox;

    public Freecam() {
        super("Freecam", Keyboard.KEY_NONE, Category.RENDER);
        ExampleMod.instance.settingsManager.rSetting(speed);
        ExampleMod.instance.settingsManager.rSetting(tracer);
    }

    @Override
    public void onEnable() {
        fakePlayer = new EntityFakePlayer();

        // Сбрасываем состояние клавиш управления
        GameSettings gs = mc.gameSettings;
        KeyBinding[] bindings = {gs.keyBindForward, gs.keyBindBack, gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak};
        for (KeyBinding binding : bindings) {
            KeyBinding.setKeyBindState(binding.getKeyCode(), GameSettings.isKeyDown(binding));
        }

        // Создаем GL-список для отрисовки коробки
        playerBox = GL11.glGenLists(1);
        GL11.glNewList(playerBox, GL11.GL_COMPILE);
        AxisAlignedBB bb = new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
        RenderUtils.drawOutlinedBox(bb);
        GL11.glEndList();
    }

    @Override
    public void onDisable() {
        // Возвращаем камеру и игрока в исходное состояние
        fakePlayer.resetPlayerPosition();
        fakePlayer.despawn();

        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;

        mc.renderGlobal.loadRenderers();

        // Удаляем GL-список
        GL11.glDeleteLists(playerBox, 1);
        playerBox = 0;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(!toggled)
            return;

        // Управление движением камеры
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;

        mc.player.onGround = false;
        mc.player.jumpMovementFactor = (float) speed.getValDouble();


        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
            mc.player.motionY += speed.getValDouble();
        }

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.player.motionY -= speed.getValDouble();
        }
    }

    @SubscribeEvent
    public void onRender(float partialTicks) {
        if (fakePlayer == null || !tracer.getValBoolean()) {
            return;
        }

        // Настройки OpenGL
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glPushMatrix();
        GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

        GL11.glColor4f(1, 1, 1, 0.5F);

        // Отрисовка коробки
        GL11.glPushMatrix();
        GL11.glTranslated(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ);
        GL11.glScaled(fakePlayer.width + 0.1, fakePlayer.height + 0.1, fakePlayer.width + 0.1);
        GL11.glCallList(playerBox);
        GL11.glPopMatrix();

        // Отрисовка линии
        Vec3d start = mc.player.getLook(partialTicks)
                .add(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ))
                .add(new Vec3d(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ));
        Vec3d end = fakePlayer.getEntityBoundingBox().getCenter();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(start.x, start.y, start.z);
        GL11.glVertex3d(end.x, end.y, end.z);
        GL11.glEnd();

        GL11.glPopMatrix();

        // Сброс настроек OpenGL
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
}