package com.tac.guns.client.render.model.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.Reference;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.render.model.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.internal.MyCachedModels.MINI_DOT_BASE;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class SroDotSightModel implements IOverrideModel
{
    private static final ResourceLocation RED_DOT_RETICLE = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/dot_reticle.png");
    private static final ResourceLocation HIT_MARKER = new ResourceLocation(Reference.MOD_ID, "textures/items/timeless_scopes/hit_marker/dot_reticle.png");

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        /*if(!(parent.getItem() instanceof TimelessPistolGunItem))
            return;*/
        matrixStack.pushPose();
        /*if (Config.CLIENT.display.redDotSquishUpdate.get() && transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
            double transition = 1.0D - Math.pow(1.0D - AimingHandler.get().getNormalisedAdsProgress(), 2.0D);
            double zScale = 0.05D + 0.95D * (1.0D - transition);
            matrixStack.scale(1.0F, 1.0F, (float)zScale);
        }*/
        /*
        if (Config.CLIENT.display.redDotSquishUpdate.get() && transformType.firstPerson() && entity.equals(Minecraft.getInstance().player)) {
            double prog = 0;
            if(AimingHandler.get().getNormalisedAdsProgress() > 0.725) {
                prog = (AimingHandler.get().getNormalisedAdsProgress() - 0.725) * 3.63;
            }
            double transition = 1.0D - Math.pow(1.0D - prog, 2.0D);
            double zScale = 0.05D + 0.95D * (1.0D - transition);
            matrixStack.scale(1.0F, 1.0F, (float) zScale);
        }
        */
        if(!parent.isEmpty()) {
            matrixStack.translate(0, 0, 0.0095F);
            GunItem gunItem = ((GunItem) parent.getItem());
            if (gunItem.getGun().getModules().getAttachments().getPistolScope().getDoOnSlideMovement() && transformType.firstPerson()) {
                matrixStack.translate(0, 0, 0.0095F);
                matrixStack.translate(0, 0, GunRenderingHandler.get().opticMovement * 0.525);
            }


            matrixStack.translate(0, 0.055, 0);
            if (gunItem.getGun().getModules().getAttachments().getPistolScope().getDoRenderMount()) {
                RenderUtil.renderModel(MINI_DOT_BASE.getModel(), stack, matrixStack, renderTypeBuffer, light, overlay);
            }
        }

        RenderUtil.renderModel(stack, parent, matrixStack, renderTypeBuffer, light, overlay);
        matrixStack.translate(0, -0.049, 0);
        matrixStack.popPose();
        //matrixStack.translate(0, 0.006, 0);
        if(transformType.firstPerson() && entity.equals(Minecraft.getInstance().player))
        {
            matrixStack.pushPose();
            {
                matrixStack.translate(0, 0, GunRenderingHandler.get().opticMovement*0.6);
                Matrix4f matrix = matrixStack.last().pose();
                Matrix3f normal = matrixStack.last().normal();

                //ScopeData scopeData = ScopeEditor.get().getScopeData() == null || ScopeEditor.get().getScopeData().getTagName() != "sro_dot" ? new ScopeData("") : ScopeEditor.get().getScopeData();
                //matrixStack.translate(scopeData.getReticleXMod(), scopeData.getReticleYMod(), 0);

                matrixStack.translate(-0.0035, -0.00415+0.00825, 0); //tdev adjustment for reticle
                float size = 2.25F / 16.0F;
                matrixStack.translate(-size / 2, (0.30) * 0.0625, (-0.085) * 0.0625);

                VertexConsumer builder;

                double invertProgress = (1.0 - AimingHandler.get().getNormalisedAdsProgress());
                matrixStack.translate(-0.04 * invertProgress, 0.01 * invertProgress, 0);

                double scale = 4.875 -3.8699894;
                matrixStack.translate(size / 2, size / 2, 0);
                matrixStack.translate(-(size / scale) / 2, -(size / scale) / 2, 0);
                matrixStack.translate(0, 0, 0.0001);

                int reticleGlowColor = RenderUtil.getItemStackColor(stack, parent, IAttachment.Type.SCOPE_RETICLE_COLOR, 1);

                float red = ((reticleGlowColor >> 16) & 0xFF) / 255F;
                float green = ((reticleGlowColor >> 8) & 0xFF) / 255F;
                float blue = ((reticleGlowColor >> 0) & 0xFF) / 255F;
                float alpha = (float) AimingHandler.get().getNormalisedAdsProgress();

                alpha = (float) (1F * AimingHandler.get().getNormalisedAdsProgress());

                builder = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(RED_DOT_RETICLE));

                GunRenderingHandler.get().applyDelayedSwayTransforms(matrixStack, Minecraft.getInstance().player, partialTicks, -1f);
                GunRenderingHandler.get().applyBobbingTransforms(matrixStack,true, 1.8f);
                GunRenderingHandler.get().applyNoiseMovementTransform(matrixStack, -1.25f);
                GunRenderingHandler.get().applyJumpingTransforms(matrixStack, partialTicks,-0.65f);

                matrixStack.translate(0, 0, -0.35);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(GunRenderingHandler.get().newSwayYaw*0.85f));
                matrixStack.mulPose(Vector3f.ZN.rotationDegrees(GunRenderingHandler.get().newSwayPitch*0.85f));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees((GunRenderingHandler.get().recoilLift * GunRenderingHandler.get().recoilReduction) * 1.15F));
                matrixStack.translate(0, 0, 0.35);

                builder.vertex(matrix, 0, (float) (size / scale), 0).color(red, green, blue, alpha).uv(0.0F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (size / scale), 0, 0).color(red, green, blue, alpha).uv(0.9375F, 0.0F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                builder.vertex(matrix, (float) (size / scale), (float) (size / scale), 0).color(red, green, blue, alpha).uv(0.9375F, 0.9375F).overlayCoords(overlay).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

            }
            matrixStack.popPose();
        }
    }
}
