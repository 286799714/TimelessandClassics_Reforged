package com.tac.guns.client.keybind;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.minecraft.client.util.InputMappings.Type.MOUSE;
import static net.minecraftforge.client.settings.KeyModifier.*;

/**
 * @author Arcomit
 * @updateDate 2023/7/28
 */
public class KeyHandler {
    //用于注册Key
    public static final ArrayList<KeyBindingTac> TAC_KEYBINDS = new ArrayList<>();

    public static KeyBindingTac PULL_TRIGGER;

    public static void init() {
        PULL_TRIGGER = new KeyBindingTac("key.tac.pull_trigger");
        PULL_TRIGGER.setKeyModifierAndCode(NONE,MOUSE,GLFW.GLFW_MOUSE_BUTTON_LEFT);//设置按键
        for (KeyBindingTac keyBind : TAC_KEYBINDS){
            ClientRegistry.registerKeyBinding(keyBind);
        }
    }
}
