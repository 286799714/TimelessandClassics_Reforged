package com.tac.guns.mixin.client;

import com.tac.guns.client.keybind.KeyBindingTac;
import com.tac.guns.interfaces.IKeyBindingMixin;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 * @Notes:Used to resolve KeyBinding collisions.
 */
@Mixin(KeyBinding.class)
public class KeyBindingMixin implements IKeyBindingMixin {
    @Shadow
    private int pressTime;

    @Final
    @Shadow
    private static KeyBindingMap HASH;

    @Shadow
    private KeyModifier keyModifierDefault;

    @Mutable
    @Final
    @Shadow
    private InputMappings.Input keyCodeDefault;

    private static HashSet<KeyBinding> otherKeyBindings = new HashSet<>();
    private static boolean isTriggerOtherKey = true;
    @Inject(method = "onTick",at = @At("HEAD"),cancellable = true)
    private static void onTick(InputMappings.Input key, CallbackInfo callbackInfo) {
        callbackInfo.cancel();

        for (KeyBinding keybinding : HASH.lookupAll(key)){
            if (keybinding != null) {
                if (keybinding instanceof KeyBindingTac){
                    ++((KeyBindingMixin)((Object)keybinding)).pressTime;
                    if (!((KeyBindingTac) keybinding).isTriggerOtherKey){
                        isTriggerOtherKey = false;
                    }
                }else {
                    otherKeyBindings.add(keybinding);
                }
            }
        }//Let the Tac key be triggered first.

        for (KeyBinding keybinding : otherKeyBindings){
            if (isTriggerOtherKey){
                ++((KeyBindingMixin)((Object)keybinding)).pressTime;
            }else {
                keybinding.setPressed(false);
                ((KeyBindingMixin)((Object)keybinding)).pressTime = 0;
            }
        }//The same key other than Tac.(if can trigger)

        isTriggerOtherKey = true;
        otherKeyBindings.clear();
    }

    @Override
    public void setDefaultKeyModifierAndCode(KeyModifier keyModifier, InputMappings.Input keyCode) {
        this.keyModifierDefault = keyModifier;
        this.keyCodeDefault = keyCode;
    }
}
