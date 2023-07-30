package com.tac.guns.client.handler;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.Reference;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.tac.guns.item.GunItem.isSingleHanded;

/**
 * @author Arcomit
 * @updateDate 2023/7/25
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class OffHandHandler {

    @SubscribeEvent
    public static void renderOffHandEvent(RenderHandEvent event) {
        if (event.getHand() == Hand.OFF_HAND) {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = event.getItemStack();

            if (mainHand.getItem() instanceof GunItem) {
                GunAnimationController controller = GunAnimationController.fromItem(mainHand.getItem());
                if (!isSingleHanded(mainHand) || controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_LOOP) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY_END) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY))
                    if (!(offHand.getItem() instanceof GunItem) && !offHand.isEmpty())
                        event.setCanceled(true);//Turn off rendering.
            }
        }
    }


    @SubscribeEvent
    public static void useOffHandEvent(InputEvent.ClickInputEvent event) {
        if (event.getHand() == Hand.OFF_HAND) {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();

            if (mainHand.getItem() instanceof GunItem) {
                GunAnimationController controller = GunAnimationController.fromItem(mainHand.getItem());
                if (!isSingleHanded(mainHand) || controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_LOOP) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY_END) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY))
                    if (!(offHand.getItem() instanceof GunItem) && !offHand.isEmpty()) {
                        event.setSwingHand(false);//Close arm swing
                        event.setCanceled(true);//Disable deputies
                    }

            }
        }
    }
}
