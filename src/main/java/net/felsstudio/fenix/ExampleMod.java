package net.felsstudio.fenix;

import net.felsstudio.fenix.Menu.onGuiOpenEvent;
import net.felsstudio.fenix.chat.BlockESPCommand;
import net.felsstudio.fenix.chat.CommandManager;
import net.felsstudio.fenix.chat.GotoCommand;
import net.felsstudio.fenix.key.Key;
import net.felsstudio.fenix.ui.ui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import net.felsstudio.clickgui.ClickGuiManager;
import net.felsstudio.clickgui.SettingsManager;

import java.lang.reflect.Field;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "fenixclient";
    public static final String NAME = "Fenix client";
    public static final String VERSION = "0.1.0";
    public static final String BUILD = "B 1";

    private static Logger logger;

    public static ExampleMod instance;
    public SettingsManager settingsManager;
    public ClickGuiManager clickGuiManager;

    public CommandManager commandManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Display.setTitle("Loading "+ Client.name);
        logger = event.getModLog();
        commandManager = new CommandManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        instance = this;
        settingsManager = new SettingsManager();
        clickGuiManager = new ClickGuiManager();
        commandManager.registerCommand(new BlockESPCommand());
        commandManager.registerCommand(new GotoCommand());

        Client.startup();
        MinecraftForge.EVENT_BUS.register(new Key());
        MinecraftForge.EVENT_BUS.register(new ui());
        MinecraftForge.EVENT_BUS.register(new onGuiOpenEvent());
    }

    public static void setSession(Session s) {
        Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();

        try {
            Field session = null;

            for (Field f : mc.getDeclaredFields()) {
                if (f.getType().isInstance(s)) {
                    session = f;
                }
            }

            if (session == null) {
                throw new IllegalStateException("Session Null");
            }

            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), s);
            session.setAccessible(false);

            Client.name = "FenixClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername();
            Display.setTitle(Client.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
