package net.felsstudio.fenix.module;

import net.felsstudio.fenix.Client;
import net.felsstudio.fenix.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public String name;
    public boolean toggled;
    public int keyCode;
    public Category category;
    public Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, int key, Category c){
        this.name = name;
        this.keyCode = key;
        this.category = c;
    }

    public boolean isEnabled(){
        return this.toggled;
    }

    public int getKey(){
        return this.keyCode;
    }

    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setKey(int key){
        this.keyCode = key;
    }

    public Category getCategory(){
        return this.category;
    }

    public String getName(){
        return  this.name;
    }

    public static List<Module> getModulesInCategory(Category c){
        List<Module> moduleList = new ArrayList<>();
        for (Module m : Client.modules){
            if (m.getCategory() == c){
                moduleList.add(m);
            }
        }

        return moduleList;
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        PLAYER,
        RENDER,
        MISC,
        MINIGAMES,
        EXPLOIT,
        FENIX,
        BARITONE
    }

    public void toggle(){
        toggled = !toggled;
        if(this.toggled){
            this.onEnable();
            ChatUtils.sendMessage("Module " + getName() + " has been enabled!");
        }else{
            this.onDisable();
            ChatUtils.sendMessage("Module " + getName() + " has been disabled!");
        }
    }

    public void setToggled(boolean t){
        this.toggled = t;
        if(this.toggled){
            this.onEnable();
        }else{
            this.onDisable();
        }
    }
}
