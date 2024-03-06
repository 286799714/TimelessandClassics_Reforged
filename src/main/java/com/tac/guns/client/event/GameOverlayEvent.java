package com.tac.guns.client.event;

import com.tac.guns.GunMod;
import com.tac.guns.api.client.player.IClientPlayerGunOperator;
import com.tac.guns.api.entity.IGunOperator;
import com.tac.guns.api.gun.ReloadState;
import com.tac.guns.api.item.IGun;
import com.tac.guns.client.animation.internal.GunAnimationStateMachine;
import com.tac.guns.client.resource.ClientGunPackLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GunMod.MOD_ID)
public class GameOverlayEvent {
    /**
     * 当玩家手上拿着枪时，播放特定动画、或瞄准时需要隐藏准心
     */
    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderOverlay(RenderGameOverlayEvent.PreLayer event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (event.getOverlay() == ForgeIngameGui.CROSSHAIR_ELEMENT) {
            LocalPlayer player = Minecraft.getInstance().player;
            // 瞄准快要完成时，取消准心渲染
            if (IClientPlayerGunOperator.fromLocalPlayer(player).getClientAimingProgress() > 0.9) {
                event.setCanceled(true);
                return;
            }
            // 换弹进行时取消准心渲染
            ReloadState reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
            if (reloadState.getStateType().isReloading()) {
                event.setCanceled(true);
                return;
            }
            // 播放的动画需要隐藏准心时，取消准心渲染
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof IGun iGun)) {
                return;
            }
            ResourceLocation gunId = iGun.getGunId(stack);
            ClientGunPackLoader.getGunIndex(gunId).ifPresent(gunIndex -> {
                GunAnimationStateMachine animationStateMachine = gunIndex.getAnimationStateMachine();
                if (animationStateMachine.shouldHideCrossHair()) {
                    event.setCanceled(true);
                }
            });
        }
    }
}
