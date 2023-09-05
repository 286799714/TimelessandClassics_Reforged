package com.tac.guns.mixin.client;

import com.tac.guns.duck.CurrentFpsGetter;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public class MinecraftStaticMixin implements CurrentFpsGetter {
    @Shadow
    private static int fps;

    @Override
    public int getCurrentFps() {
        return fps;
    }
}