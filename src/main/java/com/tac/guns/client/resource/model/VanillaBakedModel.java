package com.tac.guns.client.resource.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.Reference;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VanillaBakedModel implements CacheableModel{
    public final ResourceLocation modelLocation;
    private BakedModel cachedModel;

    public VanillaBakedModel(ResourceLocation location)
    {
        this.modelLocation = location;
    }

    @OnlyIn(Dist.CLIENT)
    protected BakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if(model == Minecraft.getInstance().getModelManager().getMissingModel())
                return model;
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay){
        BakedModel bakedModel = getModel();
        RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
    }

    @Override
    public void cleanCache(){
        this.cachedModel = null;
    }

    public static final VanillaBakedModel MISSING_MODEL = new VanillaBakedModel(null){
        @Override
        protected BakedModel getModel(){
            return Minecraft.getInstance().getModelManager().getMissingModel();
        }

        @Override
        public void cleanCache(){}
    };

    public class Laser extends VanillaBakedModel{
        public Laser(ResourceLocation location) {
            super(location);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void render(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay){
            BakedModel bakedModel = getModel();
            RenderUtil.renderLaserModuleModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
        }
    }
}
