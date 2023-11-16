package com.tac.guns.client.render.model;

import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CachedModel {
    private final ResourceLocation modelLocation;
    private BakedModel cachedModel;

    public CachedModel(ResourceLocation location)
    {
        this.modelLocation = location;
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if(model == Minecraft.getInstance().getModelManager().getMissingModel())
            {
                ForgeModelBakery.addSpecialModel(this.modelLocation);
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    public void cleanCache(){
        this.cachedModel = null;
    }
}
