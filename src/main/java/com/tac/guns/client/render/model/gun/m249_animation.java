package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.animation.M249AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
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
public class m249_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        M249AnimationController controller = M249AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_BODY, transformType, matrices);

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_MAGAZINE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, MAG), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_CAPS, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, CAP), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_CHAIN, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY)
                    || Gun.hasAmmo(stack)) {
                RenderUtil.renderModel(getComponentModel(skin, BULLET_CHAIN), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_ROTATE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, HANDLE), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M249AnimationController.INDEX_IRON, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BULLET_CHAIN_COVER), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}