package com.tac.guns.mixin.client;

import com.tac.guns.client.settings.TacKeyBingding;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyBindingMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 * @Notes:Used to resolve KeyBinding collisions.
 */
@Mixin(KeyBinding.class)
public class KeyBindingMixin{
    @Shadow
    private int pressTime;

    @Final
    @Shadow
    private static KeyBindingMap HASH;

    @Inject(method = "onTick",at = @At("HEAD"),cancellable = true)
    private static void onTick(InputMappings.Input key, CallbackInfo callbackInfo) {
        callbackInfo.cancel();

        HashSet<TacKeyBingding> pressedTacKeyBindings = new HashSet<>();
        boolean isTriggerOtherKey = true;

        for (TacKeyBingding tac : TacKeyBingding.TAC_KEYBINDS) {
            if (tac.getKey().equals(key)) {
                ++((KeyBindingMixin)((Object)tac)).pressTime;
                pressedTacKeyBindings.add(tac);
                isTriggerOtherKey = tac.isTriggerOtherKey;
            }
        }//Let the Tac key be triggered first.

        if (isTriggerOtherKey){
            for (KeyBinding keybinding : HASH.lookupAll(key)) {
                if (keybinding != null && !pressedTacKeyBindings.contains(keybinding)) {
                    ++((KeyBindingMixin)((Object)keybinding)).pressTime;
                }
            }//The same key other than Tac.
        }
    }

}
