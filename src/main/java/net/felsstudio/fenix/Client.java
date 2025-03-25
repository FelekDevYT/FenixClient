package net.felsstudio.fenix;

import net.felsstudio.fenix.font.FontUtils;
import net.felsstudio.fenix.module.COMBAT.*;
import net.felsstudio.fenix.module.EXPLOIT.HackerDetector;
import net.felsstudio.fenix.module.EXPLOIT.NewChunks;
import net.felsstudio.fenix.module.EXPLOIT.Panic;
import net.felsstudio.fenix.module.FENIX.ClickGUI;
import net.felsstudio.fenix.module.MISC.*;
import net.felsstudio.fenix.module.MOVEMENT.*;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.fenix.module.PLAYER.BlockReach;
import net.felsstudio.fenix.module.EXPLOIT.FakeCreative;
import net.felsstudio.fenix.module.PLAYER.Freecam;
import net.felsstudio.fenix.module.RENDER.*;
import org.lwjgl.opengl.Display;
import net.felsstudio.clickgui.ClickGuiManager;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    public static String name = ExampleMod.NAME + " " + ExampleMod.VERSION;
    public static String inGameName = "§6" + ExampleMod.NAME + " §e" + ExampleMod.VERSION;
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

    public static ClickGuiManager clickGuiManager;


    public static void startup(){
        //Initialising libraries


        Display.setTitle(name);

        //Movement
        modules.add(new Fly());
        modules.add(new Sprint());
        modules.add(new WaterLeave());
        modules.add(new Jesus());
        modules.add(new Spider());
        modules.add(new AirJump());
        modules.add(new Glide());
        modules.add(new BoatFly());
        modules.add(new Speed());
        modules.add(new InventoryWalk());
        modules.add(new Bunnyhop());
        modules.add(new FastFall());
        modules.add(new Velocity());
        modules.add(new ElytraFly());
        modules.add(new AutoTunnel());
        modules.add(new AutoWalk());
        modules.add(new Step());
        modules.add(new ScaffoldWalk());
        modules.add(new ElytraBoost());
        modules.add(new NoClip());
        modules.add(new Jetpack());

        //Player
        modules.add(new BlockReach());
        modules.add(new Freecam());


        //Combat
        modules.add(new TriggerBot());
        modules.add(new AntiBot());
        modules.add(new Hitbox());
        modules.add(new AimAssist());
        modules.add(new Particles());
        modules.add(new AutoArmor());
        modules.add(new AutoTotem());
        modules.add(new Criticals());
        modules.add(new KillAura());
        modules.add(new EasyAura());
        modules.add(new AutoTrap());

        //Render
        modules.add(new GlowESP());
        modules.add(new Tracers());
        modules.add(new BoxESP());
        modules.add(new FullBright());
        modules.add(new ViewModel());
        modules.add(new AttakTrace());
        modules.add(new ChestESP());
        modules.add(new TargetHUD());
        modules.add(new PlayerEntity());
        modules.add(new SpawnerESP());
        modules.add(new FakePlayer());
        modules.add(new BlockESP());
        modules.add(new Freecam());
        modules.add(new Radar());

        //Exploit
        modules.add(new FakeCreative());
        modules.add(new Panic());
        modules.add(new HackerDetector());
        modules.add(new NewChunks());

        //misc
        modules.add(new NoClickDelay());
        modules.add(new AntiAFK());
        modules.add(new AutoEatModule());
        modules.add(new AutoTool());
        modules.add(new AutoFarm());

        //fenix
        modules.add(new ClickGUI());

        clickGuiManager = new ClickGuiManager();

        FontUtils.bootstrap();


        //auto module activation on start
        modules.get(modules.size()-1).setToggled(true);
        FenixVariableManager.isCords = true;
        FenixVariableManager.isWatermark = true;
    }

    public static void keyPress(int key){
        for(Module m : modules){
            if(m.getKey() == key){
                m.toggle();
            }
        }
    }

    public static ArrayList<Module> getModulesInCategory(Module.Category c) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
    }
}
