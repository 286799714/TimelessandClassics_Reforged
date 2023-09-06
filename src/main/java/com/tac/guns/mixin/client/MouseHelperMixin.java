package com.tac.guns.mixin.client;

import com.tac.guns.Config;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.impl.Scope;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mixin(MouseHandler.class)
public class MouseHelperMixin
{
    @ModifyVariable(method = "turnPlayer()V", at = @At(value = "STORE", opcode = Opcodes.DSTORE), ordinal = 2)
    private double sensitivity(double original) {
        float additionalAdsSensitivity = 1.0F;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && !mc.player.getMainHandItem().isEmpty() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            ItemStack heldItem = mc.player.getMainHandItem();
            if (heldItem.getItem() instanceof GunItem) {
                GunItem gunItem = (GunItem) heldItem.getItem();
                if (AimingHandler.get().isAiming() && !ModSyncedDataKeys.RELOADING.getValue(mc.player)) {
                    Gun modifiedGun = gunItem.getModifiedGun(heldItem);
                    if (modifiedGun.getModules().getZoom() != null) {
                        float newFov = modifiedGun.getModules().getZoom().getFovModifier();

                        Scope scope = Gun.getScope(heldItem);
                        if (scope != null) {
                            if (scope.getTagName() == "gener8x" || scope.getTagName() == "vlpvo6" ||
                                    scope.getTagName() == "acog4x" || scope.getTagName() == "elcan14x" ||
                                    scope.getTagName() == "qmk152") {
                                newFov = (0.8F - scope.getAdditionalZoom().getFovZoom() * (Config.CLIENT.display.scopeDoubleRender.get() ? 0.833F : 1F));
                            } else {
                                newFov -= scope.getAdditionalZoom().getFovZoom() * (Config.CLIENT.display.scopeDoubleRender.get() ? 0.833F : 1F);
                            }
                        }
                        else
                            additionalAdsSensitivity = Mth.clamp(1.0F - (1.0F / newFov) / 10F, 0.0F, 1.0F);
                    }
                }
            }
        }
        double adsSensitivity = Config.CLIENT.controls.aimDownSightSensitivity.get();
        return original * (1.0 - (1.0 - adsSensitivity) * AimingHandler.get().getNormalisedAdsProgress()) * additionalAdsSensitivity;
    }
}