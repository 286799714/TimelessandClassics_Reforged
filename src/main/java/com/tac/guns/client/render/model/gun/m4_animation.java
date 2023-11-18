package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.Config;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.M4AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
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
public class m4_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        M4AnimationController controller = M4AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M4AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(getComponentModel(skin, CARRY), stack, matrices, renderBuffer, light, overlay);
            }

            renderStock(stack, matrices, renderBuffer, light, overlay, skin);

            renderLaserDevice(stack, matrices, renderBuffer, light, overlay, skin);

            if (transformType.firstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                renderLaser(stack, matrices, renderBuffer, light, overlay, skin);

            // If niether trips, render the cover for the side or top, since only one is accessible at once currently, TODO: Have a more streamlined system to handle multi-accesible attachments
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.get()) {
                RenderUtil.renderModel(getComponentModel(skin, RAIL_EXTENDED_SIDE), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, TOP_RAIL_COVER), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack) != ItemStack.EMPTY && Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.get()) {
                RenderUtil.renderModel(getComponentModel(skin, RAIL_EXTENDED_TOP), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(getComponentModel(skin, SIDE_RAIL_COVER), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack) == ItemStack.EMPTY && Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack) == ItemStack.EMPTY && Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack) == ItemStack.EMPTY) {
                RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_DEFAULT), stack, matrices, renderBuffer, light, overlay);
            } else {
                if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack) == ItemStack.EMPTY && Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack) == ItemStack.EMPTY) {
                    RenderUtil.renderModel(getComponentModel(skin, TOP_RAIL_COVER), stack, matrices, renderBuffer, light, overlay);
                    RenderUtil.renderModel(getComponentModel(skin, SIDE_RAIL_COVER), stack, matrices, renderBuffer, light, overlay);
                }
                RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_EXTENDED), stack, matrices, renderBuffer, light, overlay);
            }

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);

            matrices.pushPose();
            {
                if (transformType.firstPerson()) {
                    Gun gun = ((GunItem) stack.getItem()).getGun();
                    float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                    if (Gun.hasAmmo(stack)) {
                        // Math provided by Bomb787 on GitHub and Curseforge!!!
                        matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    } else if (!Gun.hasAmmo(stack)) {
                        {
                            matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                        }
                    }
                }
                RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M4AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();
        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}