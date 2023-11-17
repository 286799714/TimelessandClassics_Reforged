package com.tac.guns.duck;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public interface CacheableModelBakery {
    void cacheModel(ResourceLocation resourceLocation, UnbakedModel model);
    UnbakedModel getMissingModel();
}
