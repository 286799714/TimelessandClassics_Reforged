package com.tac.guns.client.resource.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public interface CacheableModel {
    void render(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay);

    void cleanCache();
}
