package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import com.tac.guns.Reference;
import com.tac.guns.client.model.bedrock.BedrockModel;
import com.tac.guns.client.model.bedrock.BedrockPart;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.resource.model.bedrock.BedrockVersion;
import com.tac.guns.client.resource.model.bedrock.pojo.BedrockModelPOJO;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BedrockGunModel extends BedrockModel implements IOverrideModel {
    /**以下用来给每个基岩版模型提供专门的 RenderType，用于提供VertexConsumer。*/ //todo 是否有更好的方法？
    private static int registryCounter = 1;
    private static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    private static final RenderStateShard.ShaderStateShard CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
    public final ResourceLocation textureLocation;

    public final RenderType MODEL_RENDER_TYPE;

    public BedrockGunModel(BedrockModelPOJO pojo, BedrockVersion version, ResourceLocation textureLocation) {
        super(pojo, version);
        this.textureLocation = textureLocation;
        MODEL_RENDER_TYPE = RenderType.create(Reference.MOD_ID + ":bedrock_model$" + registryCounter++, DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false)).createCompositeState(true));
    }

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        matrixStack.pushPose();
        //todo 游戏里不知为何模型会翻过来，原因尚未查明，暂时直接翻回去
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180f));

        for (BedrockPart model : shouldRender) {
            model.render(matrixStack, buffer.getBuffer(MODEL_RENDER_TYPE), light, overlay);
        }

        matrixStack.popPose();
    }
}
