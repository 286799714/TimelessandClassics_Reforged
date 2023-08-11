package com.tac.guns.client.handler;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.Reference;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.animation.*;
import com.tac.guns.client.render.animation.module.*;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.event.GunReloadEvent;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunEnchantmentHelper;

import com.tac.guns.util.GunModifierHelper;
import de.javagl.jgltf.model.animation.AnimationRunner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Mainly controls when the animation should play.
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public enum AnimationHandler {
    INSTANCE;

    public static void preloadAnimations() {
        //TODO: Make automatic or have some sort of check for this
        String[] animationControllers = {
                "AA12AnimationController",
                "Dp28AnimationController",
                "Glock17AnimationController",
                "HkMp5a5AnimationController",
                "HK416A5AnimationController",
                "M870AnimationController",
                "Mp7AnimationController",
                "Type81AnimationController",
                "Ak47AnimationController",
                "AWPAnimationController",
                "M60AnimationController",
                "M1014AnimationController",
                "TtiG34AnimationController",
                "MK18MOD1AnimationController",
                "M4AnimationController",
                "STI2011AnimationController",
                "Timeless50AnimationController",
                "M1911AnimationController",
                "MK47AnimationController",
                "MK14AnimationController",
                "SCAR_HAnimationController",
                "SCAR_MK20AnimationController",
                "SCAR_LAnimationController",
                "CZ75AnimationController",
                "CZ75AutoAnimationController",
                "DBShotgunAnimationController",
                "FNFALAnimationController",
                "M16A4AnimationController",
                "SPR15AnimationController",
                "Deagle50AnimationController",
                "Type95LAnimationController",
                "Type191AnimationController",
                "MAC10AnimationController",
                "Vector45AnimationController",
                "SKSTacticalAnimationController",
                "M24AnimationController",
                "M82A2AnimationController",
                "RPKAnimationController",
                "M249AnimationController",
                "M1A1AnimationController",
                "Glock18AnimationController",
                "SIGMCXAnimationController",
                "M92FSAnimationController",
                "MP9AnimationController",
                "MK23AnimationController",
                "RPG7AnimationController",
                "UDP9AnimationController",
                "UZIAnimationController",
                "MRADAnimationController",
                "HK_G3AnimationController"

        };


        for (String controllerName : animationControllers) {
            try {
                String fullClassName = "com.tac.guns.client.render.animation." + controllerName;
                Class<?> controllerClass = Class.forName(fullClassName);
                Method getInstanceMethod = controllerClass.getMethod("getInstance");
                getInstanceMethod.invoke(null);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void onGunReload(boolean reloading, ItemStack itemStack) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (itemStack.getItem() instanceof GunItem) {
            GunItem gunItem = (GunItem) itemStack.getItem();
            CompoundNBT tag = itemStack.getOrCreateTag();
            int reloadingAmount = GunModifierHelper.getAmmoCapacity(itemStack, gunItem.getGun()) - tag.getInt("AmmoCount");
            if (reloadingAmount <= 0) return;
        }
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if (controller == null) return;
        if (!reloading) return;

        AnimationMeta reloadEmptyMeta = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
        AnimationMeta reloadNormalMeta = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL);
        if (Gun.hasAmmo(itemStack)) {
            if (controller.getPreviousAnimation() != null && !controller.getPreviousAnimation().equals(reloadNormalMeta))
                controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL);
        } else {
            if (controller.getPreviousAnimation() != null && !controller.getPreviousAnimation().equals(reloadEmptyMeta))
                controller.stopAnimation();

            if (GunAnimationController.fromItem(itemStack.getItem()) instanceof PumpShotgunAnimationController)
                ((PumpShotgunAnimationController) GunAnimationController.fromItem(itemStack.getItem())).setEmpty(true);

            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
        }
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Pre event) {
        if (!event.isClient()) return;
        if (Minecraft.getInstance().player == null) return;
        if (!event.getPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller == null) return;
        if (controller.isAnimationRunning()) {
            AnimationMeta meta = controller.getPreviousAnimation();
            if (meta == null) return;
            if (meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT)) || meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY)))
                controller.stopAnimation();
            else {
                AnimationRunner runner = Animations.getAnimationRunner(meta.getResourceLocation());
                if (runner == null) return;
                float current = runner.getAnimationManager().getCurrentTimeS();
                float max = runner.getAnimationManager().getMaxEndTimeS();
                if (!(meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PUMP)) ||
                        meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PULL_BOLT))))
                    if (max - current <= 0.25f) return;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPumpShotgunFire(GunFireEvent.Post event) {
        if (!event.isClient()) return;
        if (Minecraft.getInstance().player == null) return;
        if (!event.getPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PUMP);
        }
    }

    @SubscribeEvent
    public void onBoltActionRifleFire(GunFireEvent.Post event) {
        if (!event.isClient()) return;
        if (Minecraft.getInstance().player == null) return;
        if (!event.getPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof BoltActionAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PULL_BOLT);
        }
    }

    @SubscribeEvent
    public void onMachineGunFire(GunFireEvent.Post event) {
        if (!event.isClient()) return;
        if (Minecraft.getInstance().player == null) return;
        if (!event.getPlayer().getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof MachineGunAnimationController) {
            if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.BULLET_CHAIN) != null)
                controller.runAnimation(GunAnimationController.AnimationLabel.BULLET_CHAIN);
        }
    }

    static {
        Keys.INSPECT.addPressCallback( () -> {
            final PlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return;
            
            final ItemStack stack = player.inventory.getCurrentItem();
            final GunAnimationController controller
                = GunAnimationController.fromItem(stack.getItem());
            if (controller != null && !controller.isAnimationRunning()) {
                controller.stopAnimation();
                if (Gun.hasAmmo(stack)) {
                    controller.runAnimation(GunAnimationController.AnimationLabel.INSPECT);
                } else {
                    controller.runAnimation(GunAnimationController.AnimationLabel.INSPECT_EMPTY);
                }
            }
        } );
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
    }

    @SubscribeEvent
    public void onClientPlayerReload(GunReloadEvent.Pre event) {
        if (event.isClient()) {
            GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
            if (controller != null) {
                if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack itemStack = player.inventory.getCurrentItem();
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if (controller == null) return;
        if (controller.isAnimationRunning()) {

        }
    }

    public boolean isReloadingIntro(Item item) {
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null) return false;
        return controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO);
    }

    public void onReloadLoop(Item item) {
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null) return;
        controller.stopAnimation();
        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP);
    }

    public void onReloadEnd(Item item) {
        /*
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null) return;
        if (controller instanceof PumpShotgunAnimationController) {
            if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) != null) {
                if (SyncedPlayerData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.STOP_ANIMA))
                    controller.stopAnimation();
            }
        } else {
            if (SyncedPlayerData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.STOP_ANIMA)) {
                controller.stopAnimation();
                controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
                controller.stopAnimation();
            }
        }
        */
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null) return;
        ItemStack stack = Minecraft.getInstance().player.getHeldItemMainhand();
        GunAnimationController controller = GunAnimationController.fromItem(stack.getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            if (controller.getPreviousAnimation() != null && controller.getPreviousAnimation().equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_LOOP)) && !ReloadHandler.get().isReloading()) {
                if (!controller.isAnimationRunning()) {
                    if (((PumpShotgunAnimationController) controller).isEmpty()) {
                        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_EMPTY_END);
                        ((PumpShotgunAnimationController) controller).setEmpty(false);
                    } else
                        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
                }
            }
        }

        final PlayerEntity player = Minecraft.getInstance().player;
        final ItemStack itemStack = player.inventory.getCurrentItem();
        if (itemStack.getItem() instanceof GunItem) {
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.INSPECT))
                if (AimingHandler.get().isAiming())
                    controller.stopAnimation();

            if (SyncedPlayerData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
                AimingHandler.get().cancelAim();
            } else if (AimingHandler.get().getCanceling())
                AimingHandler.get().setCanceling();
        }
    }

    /*@SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
    }

    @SubscribeEvent
    public void onClientPlayerReload(GunReloadEvent.Pre event){
        if(event.isClient()){
            GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
            if(controller != null){
                if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack itemStack = player.inventory.getCurrentItem();
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if(controller == null) return;
        if(controller.isAnimationRunning()){

        }
    }

    public boolean isReloadingIntro(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return false;
        return controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO);
    }

    public void onReloadLoop(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        controller.stopAnimation();
        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP);
    }

    public void onReloadEnd(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        if(controller instanceof PumpShotgunAnimationController ) {
            if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) != null) {
                controller.stopAnimation();
                controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
            }
        }else{
            controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
            controller.stopAnimation();
        }
    }*/
}
