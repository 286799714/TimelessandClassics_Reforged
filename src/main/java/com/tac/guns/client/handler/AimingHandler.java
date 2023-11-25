package com.tac.guns.client.handler;

import com.mojang.logging.LogUtils;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Config;
import com.tac.guns.Config.RightClickUse;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.crosshair.Crosshair;
import com.tac.guns.common.AimingManager;
import com.tac.guns.common.Gun;
import com.tac.guns.duck.MouseSensitivityModifier;
import com.tac.guns.init.ModBlocks;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.transition.TimelessGunItem;
import com.tac.guns.item.attachment.impl.Scope;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageAim;
import com.tac.guns.util.math.MathUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AimingHandler {
    private static AimingHandler instance;

    public static AimingHandler get() {
        if (instance == null) {
            instance = new AimingHandler();
        }
        return instance;
    }

    private final AimingManager.AimTracker localTracker = new AimingManager.AimTracker();
    private double normalisedAdsProgress;
    private double oldProgress;
    private double newProgress;
    private boolean aiming = false;
    private boolean toggledAim = false;

    public boolean isRenderingHand = false;

    public int getCurrentScopeZoomIndex() {
        return this.currentScopeZoomIndex;
    }

    public void resetCurrentScopeZoomIndex() {
        this.currentScopeZoomIndex = 0;
    }

    private int currentScopeZoomIndex = 0;
    private boolean isPressed = false;

    private AimingHandler() {
        Keys.SIGHT_SWITCH.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.SIGHT_SWITCH))
                return;

            final Minecraft mc = Minecraft.getInstance();
            if (
                    mc.player != null
                            && (
                            mc.player.getMainHandItem().getItem() instanceof GunItem
                                    || Gun.getScope(mc.player.getMainHandItem()) != null
                    )
            ) this.currentScopeZoomIndex++;
        });
    }

    @SubscribeEvent
    public void onLocalPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        AimingManager.get().getAimingMap().clear();
    }

    @SubscribeEvent
    public void onClickInput(InputEvent.ClickInputEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        assert player != null;
        final ItemStack heldItem = player.getMainHandItem();
        final boolean isGunInHand = heldItem.getItem() instanceof TimelessGunItem;
        if (!isGunInHand) {
            return;
        }

        if (!event.isUseItem()) {
            return;
        }

        final boolean hasMouseOverBlock = mc.hitResult instanceof BlockHitResult;
        if (!hasMouseOverBlock) {
            return;
        }

        assert mc.level != null;
        final BlockHitResult result = (BlockHitResult) mc.hitResult;
        final BlockState state = mc.level.getBlockState(result.getBlockPos());
        final Block block = state.getBlock();
        final RightClickUse config = Config.CLIENT.rightClickUse;
        if (block instanceof EntityBlock) {
            if (config.allowChests.get()) {
                return;
            }
        } else if (block == Blocks.CRAFTING_TABLE || block == ModBlocks.WORKBENCH.get()) {
            if (config.allowCraftingTable.get()) {
                return;
            }
        } else if (state.is(BlockTags.DOORS)) {
            if (config.allowDoors.get()) {
                return;
            }
        } else if (state.is(BlockTags.TRAPDOORS)) {
            if (config.allowTrapDoors.get()) {
                return;
            }
        } else if (state.is(Tags.Blocks.CHESTS)) {
            if (config.allowChests.get()) {
                return;
            }
        } else if (state.is(Tags.Blocks.FENCE_GATES)) {
            if (config.allowFenceGates.get()) {
                return;
            }
        } else if (state.is(BlockTags.BUTTONS)) {
            if (config.allowButton.get()) {
                return;
            }
        } else if (block == Blocks.LEVER) {
            if (config.allowLever.get()) {
                return;
            }
        } else if (config.allowRestUse.get()) {
            return;
        }

        event.setCanceled(true);
        event.setSwingHand(false);
    }

    public float getAimProgress(Player player, float partialTicks) {
        if (player.isLocalPlayer()) {
            return (float) this.localTracker.getNormalProgress(partialTicks);
        }

        AimingManager.AimTracker tracker = AimingManager.get().getAimTracker(player);
        if (tracker != null) {
            return (float) tracker.getNormalProgress(partialTicks);
        }
        return 0F;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        tickLerpProgress();

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        if (!Config.CLIENT.controls.holdToAim.get()) {
            if (!Keys.AIM_TOGGLE.isDown())
                this.isPressed = false;
        }

        if (this.isAiming()) {
            if (!this.aiming) {
                ModSyncedDataKeys.AIMING.setValue(player, true);
                PacketHandler.getPlayChannel().sendToServer(new MessageAim(true));
                this.aiming = true;

                final ItemStack stack = player.getInventory().getSelected();
                final GunAnimationController controller
                        = GunAnimationController.fromItem( stack.getItem() );
                if(controller != null && controller.isAnimationRunning(GunAnimationController.AnimationLabel.INSPECT))
                    controller.stopAnimation();
            }
            this.localTracker.handleAiming(player.getItemInHand(InteractionHand.MAIN_HAND), true);
        } else {
            if (this.aiming) {
                ModSyncedDataKeys.AIMING.setValue(player, false);
                PacketHandler.getPlayChannel().sendToServer(new MessageAim(false));
                this.aiming = false;
            }
            this.localTracker.handleAiming(player.getItemInHand(InteractionHand.MAIN_HAND), false);
        }
        if(this.aiming) {
            ArmorInteractionHandler.get().resetRepairProgress(false);
            player.setSprinting(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFovUpdate(FOVModifierEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && !mc.player.getMainHandItem().isEmpty() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            ItemStack heldItem = mc.player.getMainHandItem();
            if (heldItem.getItem() instanceof TimelessGunItem) {
                TimelessGunItem gunItem = (TimelessGunItem) heldItem.getItem();
                if(AimingHandler.get().normalisedAdsProgress != 0 && !SyncedEntityData.instance().get(mc.player, ModSyncedDataKeys.RELOADING))
                {
                    Gun modifiedGun = gunItem.getModifiedGun(heldItem);
                    if (modifiedGun.getModules().getZoom() != null) {
                        float newFov = modifiedGun.getModules().getZoom().getFovModifier();
                        Scope scope = Gun.getScope(heldItem);
                        if (scope != null) {
                            if (!Config.COMMON.gameplay.realisticLowPowerFovHandling.get() || (scope.getAdditionalZoom().getZoomMultiple() > 1 && Config.COMMON.gameplay.realisticLowPowerFovHandling.get()) || gunItem.isIntegratedOptic()) {
                                newFov = (float) MathUtil.magnificationToFovMultiplier(scope.getAdditionalZoom().getZoomMultiple(), Minecraft.getInstance().options.fov);
                                if(newFov >= 1) newFov = modifiedGun.getModules().getZoom().getFovModifier();
                                event.setNewfov(newFov + (1.0F - newFov) * (1.0F - (float) this.normalisedAdsProgress));
                            }
                        } else if (!Config.COMMON.gameplay.realisticIronSightFovHandling.get() || gunItem.isIntegratedOptic())
                            event.setNewfov(newFov + (1.0F - newFov) * (1.0F - (float) this.normalisedAdsProgress));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void captureFovAndModifyMouseSensitivity(EntityViewRenderEvent.FieldOfView event){
        if(!isRenderingHand) {
            Minecraft mc = Minecraft.getInstance();
            double modifier = MathUtil.fovToMagnification(event.getFOV(), mc.options.fov);
            ((MouseSensitivityModifier) mc.mouseHandler).setSensitivity(mc.options.sensitivity / modifier);
        }
    }

    private void tickLerpProgress(){
        oldProgress = newProgress;
        newProgress += (normalisedAdsProgress - newProgress) * 0.5;
    }

    /**
     * Prevents the crosshair from rendering when aiming down sight
     */
    @SubscribeEvent(receiveCanceled = true)
    public void onRenderOverlay(RenderGameOverlayEvent.PreLayer event) {
        this.normalisedAdsProgress = this.localTracker.getNormalProgress(event.getPartialTicks());
        Crosshair crosshair = CrosshairHandler.get().getCurrentCrosshair();
        if (this.normalisedAdsProgress > 0 && (crosshair == null || crosshair.isDefault())) {
            if (event.getOverlay() == ForgeIngameGui.CROSSHAIR_ELEMENT)
                event.setCanceled(true);
        }
    }

    public boolean isAiming() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return false;

        if (mc.player.isSpectator())
            return false;

        if (mc.screen != null)
            return false;

        ItemStack heldItem = mc.player.getMainHandItem();
        if (!(heldItem.getItem() instanceof GunItem))
            return false;

        Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem);
        if (gun.getModules().getZoom() == null) {
            return false;
        }

        ItemCooldowns tracker = Minecraft.getInstance().player.getCooldowns();
        float cooldown = tracker.getCooldownPercent(heldItem.getItem(), Minecraft.getInstance().getFrameTime());

        if (gun.getGeneral().isBoltAction() && (cooldown < 0.8 && cooldown > 0) && Gun.getScope(heldItem) != null) {
            return false;
        }

//        if(!this.localTracker.isAiming() && this.isLookingAtInteractableBlock())
//            return false;

        if (SyncedEntityData.instance().get(mc.player, ModSyncedDataKeys.RELOADING))
            return false;

        boolean zooming;

        if (Config.CLIENT.controls.holdToAim.get()) {
            zooming = Keys.AIM_HOLD.isDown();

        } else {
            if (Keys.AIM_TOGGLE.isDown())
                if (!this.isPressed) {
                    this.isPressed = true;
                    this.forceToggleAim();
                }
            zooming = this.toggledAim;
        }
        return zooming;
    }

    public boolean isToggledAim() {
        return this.toggledAim;
    }

    public void forceToggleAim() {
        if (this.toggledAim)
            this.toggledAim = false;
        else
            this.toggledAim = true;
    }

    public double getNormalisedAdsProgress() {
        return this.normalisedAdsProgress;
    }

    public double getLerpAdsProgress(float partialTicks){
        return Mth.lerp(partialTicks, oldProgress, newProgress);
    }
}