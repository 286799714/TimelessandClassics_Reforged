package com.tac.guns.client.keybind;

import com.tac.guns.interfaces.IKeyBindingMixin;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

import static com.tac.guns.client.keybind.KeyHandler.TAC_KEYBINDS;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 */
public class KeyBindingTac extends KeyBinding {


    public boolean isTriggerOtherKey = true;

    public KeyBindingTac(String description,String category) {
        super(description, InputMappings.INPUT_INVALID.getKeyCode(), category);
        TAC_KEYBINDS.add(this);
    }

    public KeyBindingTac(String description) {
        this(description, "key.categories.tac");
    }

    //If set to False, no other bind with the same key will fire.
    public void setIsTriggerOtherKey(boolean isTriggerOtherKey){
        this.isTriggerOtherKey = isTriggerOtherKey;
    }

    public void setKeyModifierAndCode(KeyModifier keyModifier,InputMappings.Type type, int keyCode) {
        super.setKeyModifierAndCode(keyModifier,type.getOrMakeInput(keyCode));
        ((IKeyBindingMixin)this).setDefaultKeyModifierAndCode(keyModifier,type.getOrMakeInput(keyCode));
    }
}
