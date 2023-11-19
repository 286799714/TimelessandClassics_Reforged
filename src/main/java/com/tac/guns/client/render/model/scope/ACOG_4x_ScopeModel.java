package com.tac.guns.client.render.model.scope;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.client.GunRenderType;
import com.tac.guns.client.render.model.internal.MyCachedModels;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.command.ScopeEditor;
import com.tac.guns.client.handler.command.data.ScopeData;
import com.tac.guns.client.render.model.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ACOG_4x_ScopeModel implements IOverrideModel
{
    private static final ResourceLocation RED_DOT_RETICLE = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/acog_tinangle_optic.png");
    private static final ResourceLocation HIT_MARKER = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/hit_marker/acog_tinangle_optic.png");

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay)
    {
        matrixStack.pushPose();

        /*if ((OptifineHelper.isShadersEnabled()) || !Config.CLIENT.display.scopeDoubleRender.get() && transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
            double transition = 1.0D - Math.pow(1.0D - AimingHandler.get().getNormalisedRepairProgress(), 2.0D);
            double zScale = 0.05D + 0.75D * (1.0D - transition);
            //if(AimingHandler.get().getNormalisedRepairProgress() > 0.525)
                matrixStack.translate(0,0,transition*0.18);
            matrixStack.scale(1.0F, 1.0F, (float)zScale);
        }*/
        /*
        if (!Config.CLIENT.display.scopeDoubleRender.get() && transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
            double prog = 0;
            if(AimingHandler.get().getNormalisedAdsProgress() > 0.725) {
                prog = (AimingHandler.get().getNormalisedAdsProgress() - 0.725) * 3.63;
            }
            double transition = 1.0D - Math.pow(1.0D - prog, 2.0D);
            double zScale = 0.05D + 0.95D * (1.0D - transition);
            matrixStack.translate(0,0,transition*0.18);
            matrixStack.scale(1.0F, 1.0F, (float) zScale);

        }
         */
        matrixStack.translate(0, 0.074, 0);

        RenderUtil.renderModel(stack, parent, matrixStack, renderTypeBuffer, light, overlay);
        double progress = AimingHandler.get().getLerpAdsProgress(partialTicks);
        if(progress < 0.8){
            RenderUtil.renderModel(MyCachedModels.ACOG_SCOPE_MIRROR.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);
        }

        matrixStack.translate(0, -0.057, 0);
        matrixStack.popPose();

        if(progress >= 0.8) {
            matrixStack.translate(0, 0.017, 0);
            if (transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
                ScopeData scopeData = ScopeEditor.get().getScopeData() == null || ScopeEditor.get().getScopeData().getTagName() != "acog4x" ? new ScopeData("") : ScopeEditor.get().getScopeData();
                if (entity.getMainArm() == HumanoidArm.LEFT) {
                    matrixStack.scale(-1, 1, 1);
                }

                float scopePrevSize = 0.965F + 0.27F + scopeData.getReticleSizeMod();
                float scopeSize = 1.005F + 0.245F + scopeData.getDrZoomSizeMod();
                float size = scopeSize / 16.0F;
                float reticleSize = scopePrevSize / 16.0F;
                ScopeItem scopeItem = (ScopeItem) stack.getItem();
                float crop = Config.CLIENT.quality.worldRerenderPiPAlpha.get() ? 0.1f : scopeItem.getProperties().getAdditionalZoom().getDrCropZoom() + scopeData.getDrZoomCropMod();//0.43F 0.1f;
                Minecraft mc = Minecraft.getInstance();
                Window window = mc.getWindow();

                float texU = ((window.getScreenWidth() - window.getScreenHeight() + window.getScreenHeight() * crop * 2.0F) / 2.0F) / window.getScreenWidth();

                //matrixStack.rotate(Vector3f.ZP.rotationDegrees(-GunRenderingHandler.get().immersiveWeaponRoll));
                matrixStack.pushPose();
                {
                    Matrix4f matrix = matrixStack.last().pose();
                    Matrix3f normal = matrixStack.last().normal();

                    matrixStack.translate((-size / 2) + scopeData.getDrXZoomMod(), 0.0936175 - 0.006 + scopeData.getDrYZoomMod(), Config.CLIENT.display.scopeDoubleRender.get() ? (3.915 + 0.3 + scopeData.getDrZZoomMod()) * 0.0625 : (3.075 + 0.3 + scopeData.getDrZZoomMod()) * 0.0625); //3.275

                    float color = (float) AimingHandler.get().getNormalisedAdsProgress() * 0.8F + 0.2F;

                    VertexConsumer builder;

                    if (Config.CLIENT.display.scopeDoubleRender.get()) {
                        //RenderSystem.setShaderTexture(0, GunRenderType.getScreen());


                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

                        builder = renderTypeBuffer.getBuffer(GunRenderType.getScreen());
                        //RenderSystem.setShaderTexture(0, );
                        builder.vertex(matrix, 0, size, 0).color(color, color, color, 1.0F).uv(texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, 0, 0, 0).color(color, color, color, 1.0F).uv(texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, 0, 0).color(color, color, color, 1.0F).uv(1.0F - texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, size, 0).color(color, color, color, 1.0F).uv(1.0F - texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

                    /*if(color <= 0.21)
                    {
                        builder = renderTypeBuffer.getBuffer(GunRenderType.getScreenBlack());
                        builder.vertex(matrix, 0, size, 0).color(color, color, color, 1.0F).uv(texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, 0, 0, 0).color(color, color, color, 1.0F).uv(texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, 0, 0).color(color, color, color, 1.0F).uv(1.0F - texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, size, 0).color(color, color, color, 1.0F).uv(1.0F - texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

                    }
                    else {*/


                        /*builder = renderTypeBuffer.getBuffer(GunRenderType.getScreen());
                        builder.vertex(matrix, 0, size, 0).color(color, color, color, 1.0F).uv(texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, 0, 0, 0).color(color, color, color, 1.0F).uv(texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, 0, 0).color(color, color, color, 1.0F).uv(1.0F - texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(matrix, size, size, 0).color(color, color, color, 1.0F).uv(1.0F - texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();*/


                        //}
                    }

                    matrixStack.translate(0, 0, 0.0001);

                    double invertProgress = (1.0 - AimingHandler.get().getNormalisedAdsProgress());
                    matrixStack.translate(-0.04 * invertProgress, 0.01 * invertProgress, 0);

                    double scale = 8.0;
                    matrixStack.translate(size / 2, size / 2, 0);
                    matrixStack.translate(-(size / scale) / 2, -(size / scale) / 2, 0);
                    matrixStack.translate(0, 0, 0.0001);

                    int reticleGlowColor = RenderUtil.getItemStackColor(stack, parent, IAttachment.Type.SCOPE_RETICLE_COLOR, 1);

                    float red = ((reticleGlowColor >> 16) & 0xFF) / 255F;
                    float green = ((reticleGlowColor >> 8) & 0xFF) / 255F;
                    float blue = ((reticleGlowColor >> 0) & 0xFF) / 255F;
                    float alpha = (float) AimingHandler.get().getNormalisedAdsProgress();

                    alpha = (float) (1F * AimingHandler.get().getNormalisedAdsProgress());

                    //matrixStack.rotate(Vector3f.ZP.rotationDegrees(-GunRenderingHandler.get().immersiveWeaponRoll));

                    GunRenderingHandler.get().applyBobbingTransforms(matrixStack, true);
                    matrixStack.scale(7.5f, 7.5f, 7.5f);
                    matrixStack.translate(0, 0, 0.001);
                    matrixStack.translate(-0.00335715 - 0.00022 - 0.0008825 + scopeData.getReticleXMod(), -0.0035055 - 0.0006325 + scopeData.getReticleYMod(), 0.0007 + scopeData.getReticleZMod());


                    builder = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(RED_DOT_RETICLE));
                    // Walking bobbing
                    boolean aimed = false;
                    /* The new controlled bobbing */
                    if (AimingHandler.get().isAiming())
                        aimed = true;

                    GunRenderingHandler.get().applyDelayedSwayTransforms(matrixStack, Minecraft.getInstance().player, partialTicks, -0.045f);
                    GunRenderingHandler.get().applyBobbingTransforms(matrixStack, true, 0.085f);
                    GunRenderingHandler.get().applyNoiseMovementTransform(matrixStack, -0.11f);
                    GunRenderingHandler.get().applyJumpingTransforms(matrixStack, partialTicks, -0.11f);

                    float recoilReversedMod = 0.15f;
                    matrixStack.translate(0, 0, -0.35);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(GunRenderingHandler.get().newSwayYaw * recoilReversedMod * 0.4f));
                    matrixStack.mulPose(Vector3f.ZN.rotationDegrees(GunRenderingHandler.get().newSwayPitch * recoilReversedMod * 0.5f));
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift * 0.02f * GunRenderingHandler.get().recoilReduction) * 0.65F));
                    matrixStack.translate(0, 0, 0.35);

                    double invertZoomProgress = aimed ? 0.0575 : 0.468;//double invertZoomProgress = aimed ? 0.135 : 0.94;//aimed ? 1.0 - AimingHandler.get().getNormalisedRepairProgress() : ;
                    //matrixStack.translate(-0.115*Math.asin(((double) (MathHelper.sin(GunRenderingHandler.get().walkingDistance*GunRenderingHandler.get().walkingCrouch * (float) Math.PI)) * GunRenderingHandler.get().walkingCameraYaw * 0.5F) * invertZoomProgress), 0.085*(Math.asin((double) (Math.abs(-MathHelper.cos(GunRenderingHandler.get().walkingDistance*GunRenderingHandler.get().walkingCrouch * (float) Math.PI) * GunRenderingHandler.get().walkingCameraYaw))) * invertZoomProgress * 1.140),0);//(Math.asin((double) (Math.abs(-MathHelper.cos(GunRenderingHandler.get().walkingDistance*GunRenderingHandler.get().walkingCrouch * (float) Math.PI) * GunRenderingHandler.get().walkingCameraYaw))) * invertZoomProgress * 1.140), 0.0D);// * 1.140, 0.0D);
                    //matrixStack.rotate(Vector3f.ZN.rotationDegrees((float)(MathHelper.sin(GunRenderingHandler.get().walkingDistance*GunRenderingHandler.get().walkingCrouch * (float) Math.PI) * GunRenderingHandler.get().walkingCameraYaw * 3.0F) * (float) invertZoomProgress));
                    //matrixStack.rotate(Vector3f.XN.rotationDegrees((float)(Math.abs(MathHelper.cos(GunRenderingHandler.get().walkingDistance*GunRenderingHandler.get().walkingCrouch * (float) Math.PI - 0.2F) * GunRenderingHandler.get().walkingCameraYaw) * 5.0F) * (float) invertZoomProgress));

                /*matrixStack.translate(0, 0, -0.35);
                matrixStack.rotate(Vector3f.YN.rotationDegrees((GunRenderingHandler.get().newSwayYaw * GunRenderingHandler.get().recoilReduction)*0.0955f));//0.192f
                //matrixStack.rotate(Vector3f.ZN.rotationDegrees((GunRenderingHandler.get().newSwayYaw * GunRenderingHandler.get().weaponsHorizontalAngle * 0.65f * GunRenderingHandler.get().recoilReduction)*0.0955f)); // seems to be interesting to increase the force of
                matrixStack.rotate(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift * GunRenderingHandler.get().recoilReduction) * 0.04775F));
                matrixStack.translate(0, 0, 0.35);*/

                    int lightmapValue = 15728880;
                    //alpha *= 0.6;
                    builder.vertex(matrix, 0, (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.0F, 0.9375F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, (float) (reticleSize / scale), 0, 0).color(red, green, blue, alpha).uv(0.9375F, 0.0F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, (float) (reticleSize / scale), (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.9375F, 0.9375F).overlayCoords(overlay).uv2(lightmapValue).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();


                }
                matrixStack.popPose();
            }
        }
    }
}
