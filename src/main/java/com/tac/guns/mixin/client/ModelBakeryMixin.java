package com.tac.guns.mixin.client;

import com.tac.guns.client.gunskin.SkinLoader;
import com.tac.guns.client.gunskin.SkinManager;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin({ModelBakery.class})
public abstract class ModelBakeryMixin {
    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation modelLocation);
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;
    @Shadow @Final public static ModelResourceLocation MISSING_MODEL_LOCATION;
    @Unique
    private boolean timelessandClassics_Reforged$flag = true;

    @Inject(method = "processLoading",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0
            ),
            remap = true)
    public void addSpecialModels(ProfilerFiller p_119249_, int p_119250_, CallbackInfo ci) {

        SkinLoader.missingModel = getModel(MISSING_MODEL_LOCATION);
        SkinLoader.unbakedModels = unbakedCache;
        SkinLoader.topUnbakedModels = topLevelModels;

        if (timelessandClassics_Reforged$flag) {
            SkinManager.loadDefaultSkins();
            timelessandClassics_Reforged$flag = false;
        }

        SkinManager.reload();



//        ResourceLocation raw = new ResourceLocation(Reference.MOD_ID,"special/ak47");
//
//        BlockModel r = (BlockModel) getUnbakedModel(raw);
//
//        List<BlockPart> list = Lists.newArrayList();
//        Map<String, Either<RenderMaterial, String>> map = Maps.newHashMap();
//
//        BlockModel model = new BlockModel(raw,list,map,true,null,r.getAllTransforms(),r.getOverrides());
//        ResourceLocation rl = new ResourceLocation(Reference.MOD_ID,"special/ak47/test");
//        model.name = rl.toString();
//        model.parent = r;
//
//        ResourceLocation al = new ResourceLocation("minecraft:textures/atlas/blocks.png");
//        ResourceLocation tl = new ResourceLocation("tac:gunskin/ak47/ak47_golden");
//
//        Either<RenderMaterial, String> t = Either.left(new RenderMaterial(al, tl));
//
    }
}