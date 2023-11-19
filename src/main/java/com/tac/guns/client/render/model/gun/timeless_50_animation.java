package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Timeless50AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class timeless_50_animation extends SkinnedGunModel {

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        Timeless50AnimationController controller = Timeless50AnimationController.getInstance();


        boolean renderClumsy = Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.get() ||
                Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.get();
        double yAdjust = 0; //Neko accidently made the .50 slightly too high in model space compared to the STI

        matrices.pushPose();
        {
            matrices.translate(0, yAdjust, 0);
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_BODY, transformType, matrices);
            if (renderClumsy) {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL_EXTENDED), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.CLUMSYYY), stack, matrices, renderBuffer, 15728880, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.get()) {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL_STANDARD), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.NEKOOO), stack, matrices, renderBuffer, 15728880, overlay);
                matrices.translate(0, 0, -0.3125);
                RenderUtil.renderModel(getComponentModel(skin, MUZZLE_SILENCER), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.3125);
            } else {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL_STANDARD), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.NEKOOO), stack, matrices, renderBuffer, 15728880, overlay);
            }
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            matrices.translate(0, yAdjust, 0);
            if (transformType.firstPerson()) {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_SLIDE, transformType, matrices);
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 :
                        ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());
                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();


                //matrices.translate(0.00, 0.0, 0.035); // Issues with the slide starting out further forward, seems to be ~ a 0.035 movement
                if (Gun.hasAmmo(stack) || shouldOffset) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    double v = -4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0;
                    matrices.translate(0, 0, 0.1925f * v);
                    GunRenderingHandler.get().opticMovement = 0.1925f * v;
                } else if (!Gun.hasAmmo(stack)) {
                    matrices.translate(0, 0, 0.1925f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }
                matrices.translate(0, 0, 0.025F);
            }
            if (renderClumsy) {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.SLIDE_EXTENDED_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.SLIDE_EXTENDED), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(getComponentModel(skin, SLIDE_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
                RenderUtil.renderModel(getComponentModel(skin, SLIDE), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_HAMMER, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.HAMMER), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_MAG, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY) || controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation()))
                matrices.translate(-0.00175, 0, 0); //-0.02, 0.05);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) && transformType.firstPerson()) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                matrices.translate(0.0, -0.1, 2.2);
                renderMag(stack, matrices, renderBuffer, light, overlay, skin);
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.popPose();
        }

        if (!controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY).equals(controller.getPreviousAnimation())) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_BULLET1, transformType, matrices);
                if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY) || controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation()))
                    matrices.translate(-0.00175, 0, 0);
                RenderUtil.renderModel(getComponentModel(skin, BULLET1), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.popPose();
        }

        if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) && !controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY).equals(controller.getPreviousAnimation()) && transformType.firstPerson()) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), Timeless50AnimationController.INDEX_BULLET2, transformType, matrices);
                matrices.translate(0, -0.1, 2.2);
                RenderUtil.renderModel(getComponentModel(skin, BULLET2), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.popPose();
        }

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
