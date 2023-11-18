package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.HK_G3AnimationController;
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

public class hk_g3_animation extends SkinnedGunModel {

    public hk_g3_animation() {
        extraOffset.put(MUZZLE_SILENCER, new Vector3d(0, 0, -0.485));
        extraOffset.put(MUZZLE_COMPENSATOR, new Vector3d(0, 0, -0.11));
        extraOffset.put(MUZZLE_DEFAULT, new Vector3d(0, 0, -0.11));
        extraOffset.put(MUZZLE_BRAKE, new Vector3d(0, 0, -0.11));
    }

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)  {
        HK_G3AnimationController controller = HK_G3AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(getComponentModel(skin, RAIL_SCOPE), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC_DEVICE), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_EXTENDED), stack, matrices, renderBuffer, light, overlay);
            } else {
                if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem()) ||
                        Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                    RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_EXTENDED), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_DEFAULT), stack, matrices, renderBuffer, light, overlay);
                }
            }

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            renderStockWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            if (transformType.firstPerson()) {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_BOLT, transformType, matrices);
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
                if (Gun.hasAmmo(stack) || shouldOffset) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    matrices.translate(0, 0, 0.175f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                } else if (!Gun.hasAmmo(stack)) {
                    {
                        matrices.translate(0, 0, 0.175f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                }
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_PULL, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.PULL), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_HANDLE, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, HANDLE), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), HK_G3AnimationController.INDEX_BULLET, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}