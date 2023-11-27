package com.tac.guns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.resource.model.VanillaBakedModel;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class VanillaBakedItemModelRenderer implements ItemModelRenderer<VanillaBakedModel> {
    private static VanillaBakedItemModelRenderer instance;

    private VanillaBakedItemModelRenderer(){}

    public static VanillaBakedItemModelRenderer getInstance(){
        if(instance == null) instance = new VanillaBakedItemModelRenderer();
        return instance;
    }

    @Override
    public void render(VanillaBakedModel model, ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        BakedModel bakedModel = model.getModel();
        RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
    }
}
