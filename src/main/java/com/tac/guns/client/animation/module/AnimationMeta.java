package com.tac.guns.client.animation.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMeta {
    private final ResourceLocation resourceLocation;

    public AnimationMeta(ResourceLocation resourceLocation){
        this.resourceLocation = resourceLocation;
    }
    public ResourceLocation getResourceLocation(){
        return resourceLocation;
    }

    @Override
    public boolean equals(Object meta){
        if(meta instanceof AnimationMeta) {
            AnimationMeta meta1 = (AnimationMeta) meta;
            return meta1.resourceLocation.equals(resourceLocation);
        }else
            return false;
    }
}
