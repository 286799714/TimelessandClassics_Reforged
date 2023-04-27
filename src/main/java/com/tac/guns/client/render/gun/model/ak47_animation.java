package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Ak47AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class ak47_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        
        Ak47AnimationController controller = Ak47AnimationController.getInstance();
        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());
        
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.AK47.getModel(),Ak47AnimationController.INDEX_BODY,transformType,matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(SpecialModels.AK47_OPTIC_MOUNT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.AK47_BUTT_LIGHTWEIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.AK47_BUTT_TACTICAL.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.AK47_BUTT_HEAVY.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                //int overlayTmp = Gun.getAttachment(IAttachment.Type.BARREL, stack).getStack().serializeNBT().getInt("Color");
                //int overlayTmp = Minecraft.getInstance().getItemColors().getColor(Gun.getAttachment(IAttachment.Type.BARREL, stack).getStack(), 0);
                //if(overlayTmp == -1)
                //{
                //    overlayTmp = overlay;
                //}
            /*
                Hm, it seems like the getAttachment().stack() method chain does not actually grab the color of the specific attachment
                I will be making a bug report as I don't think this behavior is correct and something wrong is on either side as this should be clearly possible
            */
                matrices.translate(0, 0, -0.295f);

                RenderUtil.renderModel(SpecialModels.AK47_SILENCER.getModel(), stack, matrices, renderBuffer, light, overlay);

                matrices.translate(0, 0, 0.295f);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.AK47_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.AK47_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(SpecialModels.AK47.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        //Always push
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.AK47.getModel(), Ak47AnimationController.INDEX_BOLT, transformType, matrices);

            /*//We're getting the cooldown tracker for the item - items like the sword, ender pearl, and chorus fruit all have this too.
            Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());
        */
            if(transformType.firstPerson()) {
                // Math provided by Bomb787 on GitHub and Curseforge!!!
                matrices.translate(0, 0, 0.190f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1));
            }
            RenderUtil.renderModel(SpecialModels.AK47_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        //Always pop
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.AK47.getModel(), Ak47AnimationController.INDEX_MAGAZINE, transformType, matrices);
            if(GunModifierHelper.getAmmoCapacity(stack) > -1)
            {
                RenderUtil.renderModel(SpecialModels.AK47_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            else
            {
                RenderUtil.renderModel(SpecialModels.AK47_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation()) ){
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(SpecialModels.AK47.getModel(), Ak47AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                if(GunModifierHelper.getAmmoCapacity(stack) > -1)
                {
                    RenderUtil.renderModel(SpecialModels.AK47_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
                else
                {
                    RenderUtil.renderModel(SpecialModels.AK47_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }
            matrices.popPose();
        }

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }

     

    //TODO comments
}
