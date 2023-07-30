package com.tac.guns.interfaces;

import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

public interface IKeyBindingMixin {
    void setDefaultKeyModifierAndCode(KeyModifier keyModifier, InputMappings.Input keyCode);
}
