package com.tac.guns.client.render.model.scope;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.internal.MyCachedModels.LPVO_1_6;
import static com.tac.guns.client.render.model.internal.MyCachedModels.LPVO_1_6_FRONT;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class VortexLPVO_3_6xScopeModel implements IOverrideModel
{
    private static final ResourceLocation RED_DOT_RETICLE = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/razor_lpvo_reticle.png");
    private static final ResourceLocation HIT_MARKER = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/hit_marker/razor_lpvo_reticle.png");
    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        matrixStack.pushPose();

        matrixStack.translate(0, 0.074, 0);

        if(AimingHandler.get().getNormalisedAdsProgress() < 0.525 || Config.CLIENT.display.scopeDoubleRender.get())
            RenderUtil.renderModel(LPVO_1_6_FRONT.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);
        RenderUtil.renderModel(LPVO_1_6.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));

        matrixStack.translate(0, -0.057, 0);
        matrixStack.popPose();
        matrixStack.translate(0, 0.017, 0);
        if(transformType.firstPerson() && entity.equals(Minecraft.getInstance().player))
        {
            ScopeData scopeData = ScopeEditor.get().getScopeData() == null || ScopeEditor.get().getScopeData().getTagName() != "vlpvo6" ? new ScopeData("") : ScopeEditor.get().getScopeData();
            if(entity.getMainArm() == HumanoidArm.LEFT)
            {
                matrixStack.scale(-1, 1, 1);
            }

            ScopeItem scopeItem = (ScopeItem)stack.getItem();
            float scopePrevSize = 0.965F + 1.5975033F + scopeData.getReticleSizeMod();
            float scopeSize = 1.13750F + 0.36750045F + 0.3F + scopeData.getDrZoomSizeMod();
            float size = scopeSize / 16.0F;
            float reticleSize = scopePrevSize / 16.0F;
            //float crop = scopeItem.getProperties().getAdditionalZoom().getDrCropZoom() + scopeData.getDrZoomCropMod();
            float crop = Config.CLIENT.quality.worldRerenderPiPAlpha.get() ? 0.1f : scopeItem.getProperties().getAdditionalZoom().getDrCropZoom() + scopeData.getDrZoomCropMod();
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            float texU = ((window.getScreenWidth() - window.getScreenHeight() + window.getScreenHeight() * crop * 2.0F) / 2.0F) / window.getScreenWidth();

            matrixStack.pushPose();
            {
                Matrix4f matrix = matrixStack.last().pose();
                Matrix3f normal = matrixStack.last().normal();

                matrixStack.translate((-size / 2) + scopeData.getDrXZoomMod(), (0.0685075+0.01175-0.01225) + scopeData.getDrYZoomMod(), (Config.CLIENT.display.scopeDoubleRender.get() ? (4.65+0.52975-0.618+ scopeData.getDrZZoomMod()) * 0.0625 : (2.15+0.52975-0.618 + scopeData.getDrZZoomMod()) * 0.0625));

                float color = (float) AimingHandler.get().getNormalisedAdsProgress() * 0.8F + 0.2F;

                VertexConsumer builder;

                if(Config.CLIENT.display.scopeDoubleRender.get())
                {
                    /*RenderSystem.enableTexture();
                    RenderSystem.bindTexture(ScreenTextureState.instance().getTextureId());
                      if(OptifineHelper.isRenderingDfb()) {
                        FlipTextures flipTextures = new FlipTextures(Shaders.activeProgram.getName(), Shaders.activeProgram.getDrawBuffers().capacity());

                        GL43.glCopyImageSubData(3553, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0,
                                ScreenTextureState.instance().lastWidth,
                                ScreenTextureState.instance().lastHeight,
                                1);
                    } else {
                        GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,0,0,ScreenTextureState.instance().lastWidth, ScreenTextureState.instance().lastHeight,0);
                    }*/

                    //RenderSystem.bindTexture(ScreenTextureState.instance().textureId);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                    builder = renderTypeBuffer.getBuffer(GunRenderType.getScreen());
                    builder.vertex(matrix, 0, size, 0).color(color, color, color, 1.0F).uv(texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, 0, 0, 0).color(color, color, color, 1.0F).uv(texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, size, 0, 0).color(color, color, color, 1.0F).uv(1.0F - texU, crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.vertex(matrix, size, size, 0).color(color, color, color, 1.0F).uv(1.0F - texU, 1.0F - crop).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

                    //RenderSystem.disableTexture();

                    /*GlStateManager.bindTexture(ClientProxy.scopeUtils.INSIDE_GUN_TEX);
                    ClientProxy.scopeUtils.drawScaledCustomSizeModalRectFlipY(0, 0, 0, 0, 1, 1, resolution.getScaledWidth(), resolution.getScaledHeight(), 1, 1);*/
                    /*builder = renderTypeBuffer.getBuffer(GunRenderType.getScreen());
                    builder.pos(matrix, 0, size, 0).color(color, color, color, 1.0F).tex(texU, 1.0F - crop).overlay(overlay).lightmap(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.pos(matrix, 0, 0, 0).color(color, color, color, 1.0F).tex(texU, crop).overlay(overlay).lightmap(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.pos(matrix, size, 0, 0).color(color, color, color, 1.0F).tex(1.0F - texU, crop).overlay(overlay).lightmap(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                    builder.pos(matrix, size, size, 0).color(color, color, color, 1.0F).tex(1.0F - texU, 1.0F - crop).overlay(overlay).lightmap(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();*/
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
                matrixStack.scale(6f,6f,6f);
                matrixStack.translate((-0.00327715-0.0063525+0.000215)+ scopeData.getReticleXMod(), (-0.003385-0.00847125+0.0008-0.000575) + scopeData.getReticleYMod(), (0.001) + scopeData.getReticleZMod());
                builder = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(RED_DOT_RETICLE));
                // Walking bobbing
                boolean aimed = false;
                /* The new controlled bobbing */
                if(AimingHandler.get().isAiming())
                    aimed = true;

                double invertZoomProgress = aimed ? 0.0575 : 0.468;//double invertZoomProgress = aimed ? 0.135 : 0.94;//aimed ? 1.0 - AimingHandler.get().getNormalisedRepairProgress() : ;
                GunRenderingHandler.get().applyDelayedSwayTransforms(matrixStack, Minecraft.getInstance().player, partialTicks, -0.045f);
                GunRenderingHandler.get().applyBobbingTransforms(matrixStack,true, 0.085f);
                GunRenderingHandler.get().applyNoiseMovementTransform(matrixStack, -0.11f);
                GunRenderingHandler.get().applyJumpingTransforms(matrixStack, partialTicks,-0.11f);

                float recoilReversedMod = 0.2f;
                matrixStack.translate(0, 0, -0.35);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(GunRenderingHandler.get().newSwayYaw*recoilReversedMod*0.4f));
                matrixStack.mulPose(Vector3f.ZN.rotationDegrees(GunRenderingHandler.get().newSwayPitch*recoilReversedMod*0.5f));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift*0.02f * GunRenderingHandler.get().recoilReduction) * 0.65F));
                matrixStack.translate(0, 0, 0.35);

                builder.vertex(matrix, 0, (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.0F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (reticleSize / scale), 0, 0).color(red, green, blue, alpha).uv(0.9375F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (reticleSize / scale), (float) (reticleSize / scale), 0).color(red, green, blue, alpha).uv(0.9375F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

            }
            matrixStack.popPose();
        }
    }
}
