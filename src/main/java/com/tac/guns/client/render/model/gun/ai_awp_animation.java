package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.animation.AWPAnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
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
 * Author: ClumsyAlien, codebase and design based off Mr.Pineapple's original addon
 */
public class ai_awp_animation extends SkinnedGunModel {

    public ai_awp_animation() {
        extraOffset.put(MUZZLE_SILENCER, new Vector3d(0, 0, -0.4));
    }

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay){
        AWPAnimationController controller = AWPAnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), AWPAnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(getComponentModel(skin, SIGHT), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
            }

            renderBarrel(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), AWPAnimationController.INDEX_BOLT, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), AWPAnimationController.INDEX_BOLT_EXTRA, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT_TAIL), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), AWPAnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            if (controller.isAnimationRunning() && controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PULL_BOLT).equals(controller.getPreviousAnimation())) {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), AWPAnimationController.INDEX_BULLET, transformType, matrices);
                RenderUtil.renderModel(getComponentModel(skin, BULLET_SHELL), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}