package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.command.ScopeEditor;
import com.tac.guns.client.handler.command.data.ScopeData;
import com.tac.guns.client.render.animation.P90AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

public class p90_animation extends SkinnedGunModel {

    private static final ResourceLocation RED_DOT_RETICLE = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/dot_reticle.png");

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        P90AnimationController controller = P90AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), P90AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.SCOPE_DEFAULT), stack, matrices, renderBuffer, light, overlay);

                //scope dot render
                matrices.translate(0, 0.017, 0);
                if (transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
                    ScopeData scopeData = ScopeEditor.get().getScopeData() == null || ScopeEditor.get().getScopeData().getTagName() == "item.tac.p90" ? new ScopeData("") : ScopeEditor.get().getScopeData();
                    if (entity.getMainArm() == HumanoidArm.LEFT) {
                        matrices.scale(-1, 1, 1);
                    }
                    float scopePrevSize = (0.965F + 0.99F + 0.975f) + scopeData.getReticleSizeMod();
                    float scopeSize = 1.815F + scopeData.getDrZoomSizeMod();
                    float size = scopeSize / 16.0F;
                    float reticleSize = scopePrevSize / 16.0F;
                    float crop = 0.429F + scopeData.getDrZoomCropMod();//0.43F
                    Minecraft mc = Minecraft.getInstance();
                    Window window = mc.getWindow();

                    float texU = ((window.getWidth() - window.getHeight() + window.getHeight() * crop * 2.0F) / 2.0F) / window.getWidth();

                    //matrixStack.rotate(Vector3f.ZP.rotationDegrees(-GunRenderingHandler.get().immersiveWeaponRoll));
                    matrices.pushPose();
                    {
                        Matrix4f matrix = matrices.last().pose();
                        Matrix3f normal = matrices.last().normal();

                        matrices.translate((-size / 2) + scopeData.getDrXZoomMod(), (0.0936175 + 0.3275) + scopeData.getDrYZoomMod(), Config.CLIENT.display.scopeDoubleRender.get() ? (3.915 - 3.605 + scopeData.getDrZZoomMod()) * 0.0625 : (3.075 - 3.605 + scopeData.getDrZZoomMod()) * 0.0625); //3.275

                        float color = (float) AimingHandler.get().getNormalisedAdsProgress() * 0.8F + 0.2F;

                        VertexConsumer builder;

                        matrices.translate(0.002, -0.21, -0.54);

                        double invertProgress = (1.0 - AimingHandler.get().getNormalisedAdsProgress());
                        matrices.translate(-0.04 * invertProgress, 0.01 * invertProgress, 0);

                        double scale = 8.0;
                        matrices.translate(size / 2, size / 2, 0);
                        matrices.translate(-(size / scale) / 2, -(size / scale) / 2, 0);

                        int reticleGlowColor = RenderUtil.getItemStackColor(stack, ItemStack.EMPTY, IAttachment.Type.SCOPE_RETICLE_COLOR, 1);

                        float red = ((reticleGlowColor >> 16) & 0xFF) / 255F;
                        float green = ((reticleGlowColor >> 8) & 0xFF) / 255F;
                        float blue = ((reticleGlowColor >> 0) & 0xFF) / 255F;
                        float alpha = (float) AimingHandler.get().getNormalisedAdsProgress();

                        alpha = (float) (1F * AimingHandler.get().getNormalisedAdsProgress());

                        //matrixStack.rotate(Vector3f.ZP.rotationDegrees(-GunRenderingHandler.get().immersiveWeaponRoll));
                        GunRenderingHandler.get().applyBobbingTransforms(matrices, true);
                        matrices.scale(6f, 6f, 6f);
                        //matrixStack.translate(-0.00335715, -0.0039355, 0.0000);
                        matrices.translate((-0.00335715 - 0.00375 - 0.00428) + scopeData.getReticleXMod(), (-0.0035055 - 0.00315) + scopeData.getReticleYMod(), 0.0000 + scopeData.getReticleZMod());


                        builder = renderBuffer.getBuffer(RenderType.entityTranslucent(RED_DOT_RETICLE));
                        // Walking bobbing
                        boolean aimed = false;
                        /* The new controlled bobbing */
                        if (AimingHandler.get().isAiming())
                            aimed = true;

                        GunRenderingHandler.get().applyDelayedSwayTransforms(matrices, Minecraft.getInstance().player, partialTicks, -0.075f);
                        GunRenderingHandler.get().applyBobbingTransforms(matrices,true, 0.1f);
                        GunRenderingHandler.get().applyNoiseMovementTransform(matrices, -0.1f);
                        GunRenderingHandler.get().applyJumpingTransforms(matrices, partialTicks,-0.05f);

                        matrices.translate(0, 0, -0.35);
                        matrices.mulPose(Vector3f.YP.rotationDegrees(GunRenderingHandler.get().newSwayYaw * 0.15f));
                        matrices.mulPose(Vector3f.ZN.rotationDegrees(GunRenderingHandler.get().newSwayPitch * 0.15f));
                        matrices.mulPose(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift * GunRenderingHandler.get().recoilReduction) * 0.25F));
                        matrices.translate(0, 0, 0.35);

                        int lightmapValue = 15728880;
                        //alpha *= 0.6;
                        builder.vertex(matrix, 0, (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.0F, 0.9375F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, (float) (reticleSize / scale), 0, 0).color(red, green, blue, alpha).uv(0.9375F, 0.0F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, (float) (reticleSize / scale), (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.9375F, 0.9375F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    }
                    matrices.popPose();
                }
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), P90AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC_DEVICE), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                if (transformType.firstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                    RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
            }

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), P90AnimationController.INDEX_MAG, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, MAG), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), P90AnimationController.INDEX_PULL, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.PULL), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}