package com.tac.guns.client.render.gun;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.client.gunskin.GunSkin;
import com.tac.guns.client.gunskin.ModelComponent;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tac.guns.client.gunskin.ModelComponent.*;


public abstract class SkinAnimationModel implements IOverrideModel {
    protected Map<ModelComponent, Vector3d> extraOffset = new HashMap<>();
    //    protected Map<ModelComponent,IBakedModel> defaultModels;
    private static List<SkinAnimationModel> models = new ArrayList<>();

    public SkinAnimationModel() {
        models.add(this);
    }

    public BakedModel getModelComponent(GunSkin skin, ModelComponent key) {
        return (skin == null || skin.getModel(key) == null ?
                Minecraft.getInstance().getModelManager().getMissingModel() :
                skin.getModel(key).getModel());
    }

//    public void cleanCache(){
//        defaultModels = null;
//    }

//    public static void cleanAllCache(){
//        for (SkinAnimationModel model : models) {
//            model.cleanCache();
//        }
//    }

    private void renderComponent(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin, ModelComponent modelComponent) {
        if (extraOffset.containsKey(modelComponent)) {
            Vector3d x = extraOffset.get(modelComponent);
            matrices.pushPose();
            matrices.translate(x.x, x.y, x.z);
            RenderUtil.renderModel(getModelComponent(skin, modelComponent), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(-x.x, -x.y, -x.z);
            matrices.popPose();
        } else
            RenderUtil.renderModel(getModelComponent(skin, modelComponent), stack, matrices, renderBuffer, light, overlay);
    }

    protected void renderStockWithDefault(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_LIGHT), stack, matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_TACTICAL), stack, matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_HEAVY), stack, matrices, renderBuffer, light, overlay);
        } else {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_DEFAULT), stack, matrices, renderBuffer, light, overlay);
        }
    }

    protected void renderStock(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_LIGHT), stack, matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_TACTICAL), stack, matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, STOCK_HEAVY), stack, matrices, renderBuffer, light, overlay);
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
            RenderUtil.renderModel(getModelComponent(skin, GRIP_TACTICAL), stack, matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, GRIP_LIGHT), stack, matrices, renderBuffer, light, overlay);
        }
    }

    protected void renderSight(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getScope(stack) != null) {
            RenderUtil.renderModel(getModelComponent(skin, SIGHT_FOLDED), stack, matrices, renderBuffer, light, overlay);
            RenderUtil.renderModel(getModelComponent(skin, SIGHT_FOLDED_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
        } else {
            RenderUtil.renderModel(getModelComponent(skin, SIGHT), stack, matrices, renderBuffer, light, overlay);
            RenderUtil.renderModel(getModelComponent(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
        }
    }

    protected void renderMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (GunModifierHelper.getAmmoCapacityWeight(stack) > -1) {
            RenderUtil.renderModel(getModelComponent(skin, MAG_EXTENDED), stack, matrices, renderBuffer, light, overlay);
        } else {
            RenderUtil.renderModel(getModelComponent(skin, MAG_STANDARD), stack, matrices, renderBuffer, light, overlay);
        }
    }

    protected void renderDrumMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (GunModifierHelper.getAmmoCapacityWeight(stack) > -1) {
            RenderUtil.renderModel(getModelComponent(skin, MAG_DRUM), stack, matrices, renderBuffer, light, overlay);
        } else {
            RenderUtil.renderModel(getModelComponent(skin, MAG_STANDARD), stack, matrices, renderBuffer, light, overlay);
        }
    }

    protected void renderLaserDevice(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, LASER_BASIC_DEVICE), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) || Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderModel(getModelComponent(skin, LASER_IR_DEVICE), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, light, overlay);
        }
    }

    protected void renderLaser(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderLaserModuleModel(getModelComponent(skin, LASER_BASIC), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay);
        } else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) || Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            RenderUtil.renderLaserModuleModel(getModelComponent(skin, LASER_IR), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, 15728880, overlay);
        }
    }
}
