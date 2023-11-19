package com.tac.guns.client.render.model.scope;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.client.GunRenderType;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.command.ScopeEditor;
import com.tac.guns.client.handler.command.data.ScopeData;
import com.tac.guns.client.render.model.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.internal.MyCachedModels.Sx8_BODY;
import static com.tac.guns.client.render.model.internal.MyCachedModels.Sx8_FRONT;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class Standard6_10xScopeModel implements IOverrideModel
{
    private static final ResourceLocation RED_DOT_RETICLE = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/standard_8x_scope_reticle.png");
    private static final ResourceLocation HIT_MARKER = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/hit_marker/standard_8x_scope_reticle.png");

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        matrixStack.pushPose();
        matrixStack.translate(0, -0.15, -0.38);
        matrixStack.translate(0, 0, 0.0015);
        if(AimingHandler.get().getNormalisedAdsProgress() < 0.525 || Config.CLIENT.display.scopeDoubleRender.get())
            RenderUtil.renderModel(Sx8_FRONT.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);
        RenderUtil.renderModel(Sx8_BODY.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);

        matrixStack.translate(0, 0.15, 0.42);

        matrixStack.popPose();
        matrixStack.translate(0, 0, 0.04);
        if(transformType.firstPerson() && entity.equals(Minecraft.getInstance().player))
        {
            if(entity.getMainArm() == HumanoidArm.LEFT)
            {
                matrixStack.scale(-1, 1, 1);
            }

            ScopeData scopeData = ScopeEditor.get().getScopeData() == null || ScopeEditor.get().getScopeData().getTagName() != "gener8x" ? new ScopeData("") : ScopeEditor.get().getScopeData();
            ScopeItem scopeItem = (ScopeItem)stack.getItem();
            float scopeSize = 1.085F + 0.24375f + 0.03f + 0.3945f + scopeData.getDrZoomSizeMod();
            float scopePrevSize = 1.20F + scopeData.getReticleSizeMod();
            float size = scopeSize / 16.0F;
            float reticleSize = scopePrevSize / 16.0F;

            float crop = Config.CLIENT.quality.worldRerenderPiPAlpha.get() ? 0.1f : scopeItem.getProperties().getAdditionalZoom().getDrCropZoom() + scopeData.getDrZoomCropMod();//scopeItem.getProperties().getAdditionalZoom().getDrCropZoom() +
            // scopeData.getDrZoomCropMod();
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();


            float texU = ((window.getScreenWidth() - window.getScreenHeight() + window.getScreenHeight() * crop * 2.0F) / 2.0F) / window.getScreenWidth();

            matrixStack.pushPose();
            {
                Matrix4f matrix = matrixStack.last().pose();
                Matrix3f normal = matrixStack.last().normal();

                matrixStack.translate((-size / 2) + scopeData.getDrXZoomMod(), 0.08725 + 0.014 -0.005 + 0.00175 + scopeData.getDrYZoomMod(), Config.CLIENT.display.scopeDoubleRender.get() ? (4.70 -0.54725 + 0.0239 + scopeData.getDrZZoomMod()) * 0.0625 :
                        (2.37 + 0.54725 + 0.0239 + scopeData.getDrZZoomMod()) * 0.0625); //4.70
                float color = (float) AimingHandler.get().getNormalisedAdsProgress() * 0.8F + 0.2F;

                VertexConsumer builder;

                if(Config.CLIENT.display.scopeDoubleRender.get())
                {
                    builder = renderTypeBuffer.getBuffer(GunRenderType.getScreen());
                    builder.vertex(matrix, 0, size, 0).color(color, color, color, 1.0F).uv(texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, 0, 0, 0).color(color, color, color, 1.0F).uv(texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, size, 0, 0).color(color, color, color, 1.0F).uv(1.0F - texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, size, size, 0).color(color, color, color, 1.0F).uv(1.0F - texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
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
                GunRenderingHandler.get().applyBobbingTransforms(matrixStack,true);
                matrixStack.scale(10.0f,10.0f,10.0f);
                matrixStack.translate(-0.00455715, -0.00439, 0.001);
                matrixStack.translate(0.00025875f, 0.0000525f, scopeData.getReticleZMod());
                builder = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(RED_DOT_RETICLE));
                // Walking bobbing
                boolean aimed = false;
                /* The new controlled bobbing */
                if(AimingHandler.get().isAiming())
                    aimed = true;

                GunRenderingHandler.get().applyDelayedSwayTransforms(matrixStack, Minecraft.getInstance().player, partialTicks, -0.0225f);
                GunRenderingHandler.get().applyBobbingTransforms(matrixStack,true, 0.035f);
                GunRenderingHandler.get().applyNoiseMovementTransform(matrixStack, -0.06f);
                GunRenderingHandler.get().applyJumpingTransforms(matrixStack, partialTicks,-0.06f);

                float recoilReversedMod = 0.15f;
                matrixStack.translate(0, 0, -0.35);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(GunRenderingHandler.get().newSwayYaw*recoilReversedMod*0.2f));
                matrixStack.mulPose(Vector3f.ZN.rotationDegrees(GunRenderingHandler.get().newSwayPitch*recoilReversedMod*0.2f));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift*0.02f * GunRenderingHandler.get().recoilReduction) * 0.25F));
                matrixStack.translate(0, 0, 0.35);

                builder.vertex(matrix, 0, (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.0F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (reticleSize / scale), 0, 0).color(red, green, blue, alpha).uv(0.9375F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (reticleSize / scale), (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.9375F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

            }
            matrixStack.popPose();
        }//float scopeSize = 1.095F;matrixStack.translate(-size / 2, 0.0645 , 3.45 * 0.0625);
    }
}
