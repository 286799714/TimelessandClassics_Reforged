package com.tac.guns.client.render.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static com.tac.guns.client.render.model.CommonComponents.*;


public abstract class ProgrammableGunModel implements IOverrideModel {
    @Deprecated
    protected Map<GunComponent, Vector3d> extraOffset = new HashMap<>();

    public ProgrammableGunModel() {
    }

    protected BakedModel getModelComponent(GunSkin skin, GunComponent key) {
        if(skin == null) return null;
        CachedModel cachedModel = skin.getModel(key);
        if(cachedModel == null) return null;
        return cachedModel.getModel();
    }

//    public void cleanCache(){
//        defaultModels = null;
//    }

//    public static void cleanAllCache(){
//        for (SkinAnimationModel model : models) {
//            model.cleanCache();
//        }
//    }

    private void renderComponent(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin, GunComponent modelComponent) {
        BakedModel bakedModel = getModelComponent(skin, modelComponent);
        if(bakedModel == null) return;
        if (extraOffset.containsKey(modelComponent)) {
            Vector3d x = extraOffset.get(modelComponent);
            matrices.pushPose();
            matrices.translate(x.x, x.y, x.z);
            RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
            matrices.translate(-x.x, -x.y, -x.z);
            matrices.popPose();
        } else
            RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
    }

    private void renderLaserModuleComponent(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin, GunComponent modelComponent) {
        if (extraOffset.containsKey(modelComponent)) {
            Vector3d x = extraOffset.get(modelComponent);
            matrices.pushPose();
            matrices.translate(x.x, x.y, x.z);
            RenderUtil.renderLaserModuleModel(getModelComponent(skin, modelComponent), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(-x.x, -x.y, -x.z);
            matrices.popPose();
        } else
            RenderUtil.renderLaserModuleModel(getModelComponent(skin, modelComponent), stack, matrices, renderBuffer, light, overlay);
    }

    protected void renderStockWithDefault(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_LIGHT);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_TACTICAL);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_HEAVY);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_DEFAULT);
        }
    }

    protected void renderStock(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_LIGHT);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_TACTICAL);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_HEAVY);
        }
    }

    protected void renderBarrelWithDefault(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_SILENCER);
        } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_COMPENSATOR);
        } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_BRAKE);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_DEFAULT);
        }
    }

    protected void renderBarrel(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_SILENCER);
        } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_COMPENSATOR);
        } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_BRAKE);
        }
    }

    protected void renderGrip(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, GRIP_TACTICAL);
        } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, GRIP_LIGHT);
        }
    }

    protected void renderSight(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getScope(stack) != null) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, SIGHT_FOLDED);
            renderComponent(stack, matrices, renderBuffer, 15728880, overlay, skin, SIGHT_FOLDED_LIGHT);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, SIGHT);
            renderComponent(stack, matrices, renderBuffer, 15728880, overlay, skin, SIGHT_LIGHT);
        }
    }

    protected void renderMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (GunModifierHelper.getAmmoCapacityWeight(stack) > -1) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_EXTENDED);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_STANDARD);
        }
    }

    protected void renderDrumMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (GunModifierHelper.getAmmoCapacityWeight(stack) > -1) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_DRUM);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_STANDARD);
        }
    }

    protected void renderLaserDevice(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay, skin, LASER_BASIC_DEVICE);
        } else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) ||
                Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, light, overlay, skin, LASER_IR_DEVICE);
        }
    }

    protected void renderLaser(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay, skin, LASER_BASIC);
        } else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) ||
                Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, 15728880, overlay, skin, LASER_IR);
        }
    }
}
