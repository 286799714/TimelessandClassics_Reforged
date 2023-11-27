package com.tac.guns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.resource.model.CacheableModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public interface ItemModelRenderer<T extends CacheableModel>{
    void render(T model, ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay);
}
