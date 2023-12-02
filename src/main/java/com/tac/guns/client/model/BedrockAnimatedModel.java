package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tac.guns.Reference;
import com.tac.guns.client.animation.AnimationListener;
import com.tac.guns.client.animation.AnimationListenerSupplier;
import com.tac.guns.client.animation.ObjectAnimationChannel;
import com.tac.guns.client.model.bedrock.BedrockModel;
import com.tac.guns.client.model.bedrock.BedrockPart;
import com.tac.guns.client.model.bedrock.IModelRenderer;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class BedrockAnimatedModel extends BedrockModel implements IOverrideModel, AnimationListenerSupplier {
    /**以下用来给每个基岩版模型提供专门的 RenderType，用于提供VertexConsumer。*/ //todo 是否有更好的方法？
    private static int registryCounter = 1;
    private static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    private static final RenderStateShard.ShaderStateShard CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
    public final ResourceLocation textureLocation;

    public final RenderType MODEL_RENDER_TYPE;

    private Map<String, Function<BedrockPart, IModelRenderer>> functionRenderers;
    private final CameraAnimationObject cameraAnimationObject = new CameraAnimationObject();

    public BedrockAnimatedModel(BedrockModelPOJO pojo, BedrockVersion version, ResourceLocation textureLocation) {
        super(pojo, version);
        this.textureLocation = textureLocation;
        MODEL_RENDER_TYPE = RenderType.create(Reference.MOD_ID + ":bedrock_model$" + registryCounter++, DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false)).createCompositeState(true));
    }

    public void setVisible(String bone, boolean visible){
        ModelRendererWrapper rendererWrapper = modelMap.get(bone);
        if(rendererWrapper != null)
            rendererWrapper.setHidden(visible);
    }

    /**
     * @param node 想要进行编程渲染流程的node名称
     * @param function 输入为BedrockPart，返回IModelRenderer以替换渲染
     */
    public void setFunctionalRenderer(String node, Function<BedrockPart, IModelRenderer> function){
        functionRenderers.put(node, function);
    }

    public void removeFunctionalRenderer(String node){
        functionRenderers.remove(node);
    }

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        matrixStack.pushPose();
        //游戏中模型是上下颠倒的，需要翻转过来。
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180f));

        VertexConsumer builder = buffer.getBuffer(MODEL_RENDER_TYPE);
        for (BedrockPart model : shouldRender) {
            model.render(matrixStack, builder, light, overlay);
        }

        matrixStack.popPose();
    }

    public @Nonnull CameraAnimationObject getCameraAnimationObject(){
        return cameraAnimationObject;
    }

    @Override
    protected void loadNewModel(BedrockModelPOJO pojo) {
        if(functionRenderers == null) functionRenderers = new HashMap<>();
        assert pojo.getGeometryModelNew() != null;
        pojo.getGeometryModelNew().deco();
        for (BonesItem bones : pojo.getGeometryModelNew().getBones()) {
            //将FunctionalBedrockPart先塞入modelMap中，以支持functionalRender操作
            modelMap.putIfAbsent(bones.getName(), new ModelRendererWrapper(new FunctionalBedrockPart(functionRenderers, bones.getName())));
        }
        super.loadNewModel(pojo);
    }

    @Override
    protected void loadLegacyModel(BedrockModelPOJO pojo) {
        if(functionRenderers == null) functionRenderers = new HashMap<>();
        assert pojo.getGeometryModelLegacy() != null;
        pojo.getGeometryModelLegacy().deco();
        for (BonesItem bones : pojo.getGeometryModelLegacy().getBones()) {
            //将FunctionalBedrockPart先塞入modelMap中，以支持functionalRender操作
            modelMap.putIfAbsent(bones.getName(), new ModelRendererWrapper(new FunctionalBedrockPart(functionRenderers, bones.getName())));
        }
        super.loadLegacyModel(pojo);
    }

    private static float dot(float[] a, float[] b)
    {
        float sum = 0;
        for (int i=0; i<a.length; i++)
        {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static void toQuaternion(float roll, float pitch, float yaw, @Nonnull Quaternion quaternion){
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);

        quaternion.set(
                (float) (cy * cp * sr - sy * sp * cr),
                (float) (sy * cp * sr + cy * sp * cr),
                (float) (sy * cp * cr - cy * sp * sr),
                (float) (cy * cp * cr + sy * sp * sr)
        );
    }

    private static void quaternionToMatrix4x4(float[] q, float[] m)
    {
        float invLength = 1.0f / (float)Math.sqrt(dot(q, q));
        float qx = q[0] * invLength;
        float qy = q[1] * invLength;
        float qz = q[2] * invLength;
        float qw = q[3] * invLength;
        m[ 0] = 1.0f - 2.0f * qy * qy - 2.0f * qz * qz;
        m[ 1] = 2.0f * (qx * qy + qw * qz);
        m[ 2] = 2.0f * (qx * qz - qw * qy);
        m[ 3] = 0.0f;
        m[ 4] = 2.0f * (qx * qy - qw * qz);
        m[ 5] = 1.0f - 2.0f * qx * qx - 2.0f * qz * qz;
        m[ 6] = 2.0f * (qy * qz + qw * qx);
        m[ 7] = 0.0f;
        m[ 8] = 2.0f * (qx * qz + qw * qy);
        m[ 9] = 2.0f * (qy * qz - qw * qx);
        m[10] = 1.0f - 2.0f * qx * qx - 2.0f * qy * qy;
        m[11] = 0.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }

    @Override
    public List<Pair<String, AnimationListener>> supplyListeners() {
        List<Pair<String, AnimationListener>> listeners = new ArrayList<>();

        for(Map.Entry<String, ModelRendererWrapper> entry : modelMap.entrySet()){
            AnimationListener translationListener = new AnimationListener() {
                final ModelRendererWrapper rendererWrapper = entry.getValue();
                BonesItem bonesItem;
                {
                    //如果当前node是根node(也就是包含于shouldRender中)，则获取其bonesItem，以便后续计算相对位移 offset。
                    if(shouldRender.contains(rendererWrapper.getModelRenderer()))
                         bonesItem = indexBones.get(entry.getKey());
                }
                @Override
                public void update(float[] values) {
                    //因为模型是上下颠倒的，因此此处x轴和y轴的偏移也进行取反
                    if(bonesItem != null){
                        //因为要达成所有位移都是相对位移，所以如果当前node是根node，则减去根node的pivot坐标。
                        rendererWrapper.setOffsetX(-values[0] + bonesItem.getPivot().get(0) / 16f);
                        rendererWrapper.setOffsetY(-values[1] + bonesItem.getPivot().get(1) / 16f);
                        rendererWrapper.setOffsetZ(values[2] - bonesItem.getPivot().get(2) / 16f);
                    }else {
                        //虽然方法名称写的是getRotationPoint，但其实还是相对父级node的坐标移动量。因此此处与listener提供的local translation相减。
                        rendererWrapper.setOffsetX(-values[0] - rendererWrapper.getRotationPointX() / 16f);
                        rendererWrapper.setOffsetY(-values[1] - rendererWrapper.getRotationPointY() / 16f);
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
                    float[] m = new float[16];
                    quaternionToMatrix4x4(values, m);
                    // 计算 roll（绕 x 轴的旋转角）
                    float roll = (float)Math.atan2(m[6], m[10]);
                    // 计算 pitch（绕 y 轴的旋转角）
                    float pitch = (float)Math.atan2(m[2], Math.sqrt(m[6] * m[6] + m[10] * m[10]));
                    // 计算 roll（绕 z 轴的旋转角）
                    float yaw = (float)Math.atan2(m[1], m[0]);
                    //因为模型是上下颠倒的，因此此处roll轴的旋转需要进行取反
                    //此处不使用forge的Quaternion构造方法是因为这玩意儿竟然是用单位元四元数连乘三轴旋转四元数，这样就顺序相关了....
                    toQuaternion(-roll, pitch, yaw, rendererWrapper.getAdditionalQuaternion());
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

        //init and add camera animation object's animation listener to list
        ModelRendererWrapper cameraRendererWrapper = modelMap.get(CameraAnimationObject.CAMERA_NODE_NAME);
        if(cameraRendererWrapper != null) {
            if(shouldRender.contains(cameraRendererWrapper.getModelRenderer())){
                cameraAnimationObject.cameraBone = indexBones.get(CameraAnimationObject.CAMERA_NODE_NAME);
            }else {
                cameraAnimationObject.cameraRenderer = cameraRendererWrapper;
            }
            listeners.addAll(cameraAnimationObject.supplyListeners());
        }

        return listeners;
    }

    /**visible的优先级低于FunctionalBedrockPart，当visible为false的时候，仍然会执行functionalRenderers*/
    protected static class FunctionalBedrockPart extends BedrockPart{
        private final Map<String, Function<BedrockPart, IModelRenderer>> functionalRenderers;

        public FunctionalBedrockPart(@Nonnull Map<String, Function<BedrockPart, IModelRenderer>> functionalRenderers, @Nonnull String name){
            super(name);
            this.functionalRenderers = functionalRenderers;
        }

        @Override
        public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.translate(this.offsetX, this.offsetY, this.offsetZ);
            this.translateAndRotateAndScale(poseStack);

            Function<BedrockPart, IModelRenderer> function = name == null ? null : functionalRenderers.get(name);
            if(function != null){
                @Nullable IModelRenderer renderer = function.apply(this);
                if(renderer != null) renderer.render(poseStack, consumer, light, overlay);
            }else {
                super.compile(poseStack.last(), consumer, light, overlay, red, green, blue, alpha);
                for (BedrockPart part : this.children) {
                    part.render(poseStack, consumer, light, overlay, red, green, blue, alpha);
                }
            }

            poseStack.popPose();
        }
    }

    public static class CameraAnimationObject implements AnimationListenerSupplier{
        public static final String CAMERA_NODE_NAME = "cameraK";
        /**存在这个四元数中的旋转是世界箱体的旋转，而不是摄像头的旋转（二者互为相反数）*/
        public Quaternion rotationQuaternion = Quaternion.ONE.copy();
        public Vector3f translationVector = new Vector3f();

        /**
         * 当相机的节点为根时，cameraRenderer为空
         * */
        protected ModelRendererWrapper cameraRenderer;
        /**
         * 当相机的节点不为根时，cameraBone为空
         * */
        protected BonesItem cameraBone;

        @Override
        public List<Pair<String, AnimationListener>> supplyListeners() {
            AnimationListener translation = new AnimationListener() {
                @Override
                public void update(float[] values) {
                    if(cameraBone != null){
                        //因为要达成所有位移都是相对位移，所以如果当前node是根node，则减去根node的pivot坐标。
                        translationVector.setX(values[0] - cameraBone.getPivot().get(0) / 16f);
                        translationVector.setY(values[1] - cameraBone.getPivot().get(1) / 16f);
                        translationVector.setZ(values[2] - cameraBone.getPivot().get(2) / 16f);
                    }else {
                        //虽然方法名称写的是getRotationPoint，但其实还是相对父级node的坐标移动量。因此此处与listener提供的local translation相减。
                        translationVector.setX(values[0] + cameraRenderer.getRotationPointX() / 16f);
                        translationVector.setY(values[1] + cameraRenderer.getRotationPointY() / 16f);
                        translationVector.setZ(values[2] - cameraRenderer.getRotationPointZ() / 16f);
                    }
                }

                @Override
                public ObjectAnimationChannel.ChannelType getType() {
                    return ObjectAnimationChannel.ChannelType.TRANSLATION;
                }
            };
            AnimationListener rotation = new AnimationListener() {
                @Override
                public void update(float[] values) {
                    float[] m = new float[16];
                    quaternionToMatrix4x4(values, m);
                    // 计算 roll（绕 x 轴的旋转角）
                    float roll = (float)Math.atan2(m[6], m[10]);
                    // 计算 pitch（绕 y 轴的旋转角）
                    float pitch = (float)Math.atan2(m[2], Math.sqrt(m[6] * m[6] + m[10] * m[10]));
                    // 计算 roll（绕 z 轴的旋转角）
                    float yaw = (float)Math.atan2(m[1], m[0]);
                    /*对roll和yaw取反单纯是因为需要使用blockbench的camera插件，
                      它在关键帧中储存的旋转数值并不是摄像头的旋转数值，是世界箱体的旋转数值，
                      但唯独pitch是反的(也就是说唯独pitch是摄像机的旋转数值)。
                      最终需要存入rotationQuaternion的是世界箱体的旋转，因此roll yaw取反，pitch不需要*/
                    toQuaternion(-roll, pitch, -yaw, rotationQuaternion);
                }

                @Override
                public ObjectAnimationChannel.ChannelType getType() {
                    return ObjectAnimationChannel.ChannelType.ROTATION;
                }
            };

            return Arrays.asList(new Pair<>(CAMERA_NODE_NAME, translation), new Pair<>(CAMERA_NODE_NAME, rotation));
        }
    }
}
