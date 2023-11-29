package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tac.guns.Reference;
import com.tac.guns.client.animation.AnimationListener;
import com.tac.guns.client.animation.AnimationListenerSupplier;
import com.tac.guns.client.animation.ObjectAnimationChannel;
import com.tac.guns.client.model.bedrock.BedrockModel;
import com.tac.guns.client.model.bedrock.BedrockPart;
import com.tac.guns.client.model.bedrock.ModelRendererWrapper;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.resource.model.bedrock.BedrockVersion;
import com.tac.guns.client.resource.model.bedrock.pojo.BedrockModelPOJO;
import com.tac.guns.client.resource.model.bedrock.pojo.BonesItem;
import com.tac.guns.client.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BedrockGunModel extends BedrockModel implements IOverrideModel, AnimationListenerSupplier {
    /**以下用来给每个基岩版模型提供专门的 RenderType，用于提供VertexConsumer。*/ //todo 是否有更好的方法？
    private static int registryCounter = 1;
    private static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    private static final RenderStateShard.ShaderStateShard SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeSolidShader);
    public final ResourceLocation textureLocation;

    public final RenderType MODEL_RENDER_TYPE;

    public BedrockGunModel(BedrockModelPOJO pojo, BedrockVersion version, ResourceLocation textureLocation) {
        super(pojo, version);
        this.textureLocation = textureLocation;
        MODEL_RENDER_TYPE = RenderType.create(Reference.MOD_ID + ":bedrock_model$" + registryCounter++, DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false)).createCompositeState(true));
    }

    public void setVisible(String bone, boolean visible){
        modelMap.get(bone).getModelRenderer().visible = visible;
    }

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        matrixStack.pushPose();
        //todo 游戏里不知为何模型会翻过来，原因尚未查明，暂时直接翻回去
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180f));

        VertexConsumer builder = buffer.getBuffer(MODEL_RENDER_TYPE);
        for (BedrockPart model : shouldRender) {
            model.render(matrixStack, builder, light, overlay);
        }

        matrixStack.popPose();
    }

    @Override
    public List<Pair<String, AnimationListener>> supplyListeners() {
        List<Pair<String, AnimationListener>> listeners = new ArrayList<>();

        for(Map.Entry<String, ModelRendererWrapper> entry : modelMap.entrySet()){
            AnimationListener translationListener = new AnimationListener() {
                final ModelRendererWrapper rendererWrapper = entry.getValue();
                BonesItem bonesItem;

                {
                    if(shouldRender.contains(rendererWrapper.getModelRenderer()))
                         bonesItem = indexBones.get(entry.getKey());
                }
                @Override
                public void update(float[] values) {
                    //todo 因为模型是上下颠倒的，因此此处x轴和y轴的偏移也进行取反，如果上下颠倒的问题修复了，这里记得改
                    if(bonesItem != null){
                        LogUtils.getLogger().info(bonesItem.getName() + ":" + values[0] * 16f + "," + values[1] * 16f + "," + values[2] * 16f + "   " + bonesItem.getPivot().get(0) + "," + bonesItem.getPivot().get(1) + "," + bonesItem.getPivot().get(2));
                        //因为要达成所有位移都是相对位移，所以如果当前node是根node，则减去根node的pivot坐标。
                        rendererWrapper.setOffsetX(-values[0] + bonesItem.getPivot().get(0) / 16f);
                        rendererWrapper.setOffsetY(-values[1] + bonesItem.getPivot().get(1) / 16f);
                        rendererWrapper.setOffsetZ(values[2] - bonesItem.getPivot().get(2) / 16f);
                    }else {
                        //虽然方法名称写的是getRotationPoint，但其实还是相对父级node的坐标移动量。因此此处与listener提供的local translation相减。
                        rendererWrapper.setOffsetX(-values[0] + rendererWrapper.getRotationPointX() / 16f);
                        rendererWrapper.setOffsetY(-values[1] + rendererWrapper.getRotationPointY() / 16f);
                        rendererWrapper.setOffsetZ(values[2] - rendererWrapper.getRotationPointZ() / 16f);
                    }
                }

                @Override
                public ObjectAnimationChannel.ChannelType getType() {
                    return ObjectAnimationChannel.ChannelType.TRANSLATION;
                }
            };
            AnimationListener rotationListener = new AnimationListener() {
                final ModelRendererWrapper rendererWrapper = entry.getValue();
                @Override
                public void update(float[] values) {
                    //todo 因为模型是上下颠倒的，因此此处x、y轴的旋转需要进行取反
                    Quaternion quaternion = new Quaternion(values[0], values[1], values[2], values[3]);
                    Vector3f vector3f = quaternion.toXYZ();
                    vector3f.setX(-vector3f.x());
                    vector3f.setY(-vector3f.y());
                    quaternion = Quaternion.fromXYZ(vector3f);
                    rendererWrapper.setAdditionalQuaternion(quaternion);
                }
                @Override
                public ObjectAnimationChannel.ChannelType getType() {
                    return ObjectAnimationChannel.ChannelType.ROTATION;
                }
            };
            AnimationListener scaleListener = new AnimationListener() {
                final ModelRendererWrapper rendererWrapper = entry.getValue();
                @Override
                public void update(float[] values) {
                    rendererWrapper.setScaleX(values[0]);
                    rendererWrapper.setScaleY(values[1]);
                    rendererWrapper.setScaleZ(values[2]);
                }
                @Override
                public ObjectAnimationChannel.ChannelType getType() {
                    return ObjectAnimationChannel.ChannelType.SCALE;
                }
            };
            listeners.add(new Pair<>(entry.getKey(), translationListener));
            listeners.add(new Pair<>(entry.getKey(), rotationListener));
            listeners.add(new Pair<>(entry.getKey(), scaleListener));
        }

        return listeners;
    }
}
