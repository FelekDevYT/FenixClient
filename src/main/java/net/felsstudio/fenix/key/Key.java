package net.felsstudio.fenix.key;

import net.felsstudio.fenix.Client;
import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.EXPLOIT.Panic;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Key {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e){
        if(Keyboard.isKeyDown(Keyboard.getEventKey())){
            if(Keyboard.getEventKey() != Keyboard.KEY_NONE){
                Client.keyPress(Keyboard.getEventKey());

                if(Keyboard.getEventKey() == Keyboard.KEY_RSHIFT && !Panic.isPanic){
                    Minecraft.getMinecraft().displayGuiScreen(ExampleMod.instance.clickGuiManager);
                }
            }
        }
    }
}
