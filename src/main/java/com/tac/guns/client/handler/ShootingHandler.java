package com.tac.guns.client.handler;

import com.tac.guns.Config;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.transition.TimelessGunItem;
import com.tac.guns.duck.CurrentFpsGetter;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageEmptyMag;
import com.tac.guns.network.message.MessageShoot;
import com.tac.guns.network.message.MessageShooting;
import com.tac.guns.network.message.MessageUpdateMoveInacc;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import com.tac.guns.util.GunModifierHelper;

import static net.minecraftforge.event.TickEvent.Type.RENDER;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ShootingHandler {

    // TODO: Cleanup and document ShootingHandler fire code, what is important or able to be simplified.
    private static ShootingHandler instance;

    public static ShootingHandler get() {
        if (instance == null) {
            instance = new ShootingHandler();
        }
        return instance;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public void setShootingError(boolean shootErr) {
        this.shootErr = shootErr;
    }

    private boolean shooting;

    private boolean shootErr;
    private boolean clickUp = false;
    public int burstTracker = 0;
    private int burstCooldown = 0;
    private boolean isPressed = false;

    private final int emptyCheckCoolDown = 40;

    private int emptyCheckCountDown = 40;

    private ShootingHandler() {
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!isInGame())
            return;
        this.burstTracker = 0;
        this.burstCooldown = 0;
        this.clickUp = false;
        this.shootErr = false;
    }

    private boolean isInGame() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null)
            return false;
        if (mc.getOverlay() != null)
            return false;
        if (mc.screen != null)
            return false;
        if (!mc.mouseHandler.isMouseGrabbed())
            return false;
        return mc.isWindowActive();
    }

    // CHECK HERE: Indicates the ticks left for next shot
    private static float shootTickGapLeft = 0F;

    public float getShootTickGapLeft() {
        return shootTickGapLeft;
    }

    public static float shootMsGap = 0F;

    public float getshootMsGap() {
        return shootMsGap;
    }

    public static float calcShootTickGap(int rpm) {
        float shootTickGap = 60F / rpm * 20F;
        return shootTickGap;
    }


    private static float hitmarkerCooldownMultiplier() {
        int fps = ((CurrentFpsGetter) Minecraft.getInstance()).getCurrentFps();
        if (fps < 11)
            return 16f;
        else if (fps < 21)
            return 14.5f;
        else if (fps < 31)
            return 4f;
        else if (fps < 61)
            return 2f;
        else if (fps < 121)
            return 1f;
        else if (fps < 181)
            return 0.7f;
        else if (fps < 201)
            return 0.5f;
        else
            return 0.375f;
    }

    private static float visualCooldownMultiplier() {
        int fps = ((CurrentFpsGetter) Minecraft.getInstance()).getCurrentFps();
        if (fps < 11)
            return 8f;
        else if (fps < 21)
            return 6.25f;
        else if (fps < 31)
            return 1.25f;
        else if (fps < 61)
            return 0.95f;
        else if (fps < 121)
            return 0.625f;
        else if (fps < 181)
            return 0.425f;
        else if (fps < 201)
            return 0.35f;
        else
            return 0.25f;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderTickLow(TickEvent.RenderTickEvent evt) {
        if (!evt.type.equals(RENDER) || evt.phase.equals(TickEvent.Phase.START))
            return;

        //TODO: Gurantee this solution is good, run a performance profile soon and reduce renderTick listeners
        if (HUDRenderingHandler.get().hitMarkerTracker > 0F)
            HUDRenderingHandler.get().hitMarkerTracker -= evt.renderTickTime * hitmarkerCooldownMultiplier();
        else
            HUDRenderingHandler.get().hitMarkerTracker = 0;
        if (shootMsGap > 0F) {
            shootMsGap -= evt.renderTickTime * visualCooldownMultiplier();
        } else if (shootMsGap < -0.05F)
            shootMsGap = 0F;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderTick(TickEvent.RenderTickEvent evt) {
        // Upper is to handle rendering, bellow is handling animation calls and burst tracking

        if (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.isAlive() || Minecraft.getInstance().player.getMainHandItem().getItem() instanceof GunItem)
            return;
        GunAnimationController controller = GunAnimationController.fromItem(Minecraft.getInstance().player.getMainHandItem().getItem());
        if (controller == null)
            return;
        else if (controller.isAnimationRunning() && (shootMsGap < 0F && this.burstTracker != 0)) {
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP) || controller.isAnimationRunning(GunAnimationController.AnimationLabel.PULL_BOLT))
                return;
            if (Config.CLIENT.controls.burstPress.get())
                this.burstTracker = 0;
            this.clickUp = true;
        }
    }

    @SubscribeEvent
    public void onHandleShooting(TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.START)
            return;

        if (!this.isInGame())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            // CHECK HERE: Reduce by 1F in each tick until it is less than 0F
            shootTickGapLeft -= shootTickGapLeft > 0F ? 1F : 0F;

            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof GunItem && (Gun.hasAmmo(heldItem) || player.isCreative())) {
                final float dist = Math.abs(player.zza) / 2.5F
                        + Math.abs(player.xxa) / 1.25F
                        + (player.getDeltaMovement().y > 0D ? 0.5F : 0F);
                PacketHandler.getPlayChannel().sendToServer(new MessageUpdateMoveInacc(dist));

                // Update #shooting state if it has changed
                final boolean shooting = Keys.PULL_TRIGGER.isDown() && GunRenderingHandler.get().sprintTransition == 0;
                // TODO: check if this is needed
//              if(GunMod.controllableLoaded)
//              {
//                  shooting |= ControllerHandler.isShooting();
//              }
                if (shooting ^ this.shooting) {
                    this.shooting = shooting;
                    PacketHandler.getPlayChannel().sendToServer(new MessageShooting(shooting));
                }
            } else if (this.shooting) {
                this.shooting = false;
                PacketHandler.getPlayChannel().sendToServer(new MessageShooting(false));
            }
        } else {
            this.shooting = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isInGame())
            return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null)
            if (this.burstCooldown > 0)
                this.burstCooldown -= 1;
        if(emptyCheckCountDown <= emptyCheckCoolDown)
            emptyCheckCountDown++;
    }

    @SubscribeEvent
    public void onClickInput(InputEvent.ClickInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if(heldItem.getItem() instanceof TimelessGunItem) {
            if (Keys.PULL_TRIGGER.isDown()) {
                if (event.isAttack()) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }

                if (emptyCheckCountDown > emptyCheckCoolDown) {
                    if (magError(player, heldItem)) {
                        emptyCheckCountDown = 0;
                        player.displayClientMessage(new TranslatableComponent("info.tac.mag_error").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED), true);
                        PacketHandler.getPlayChannel().sendToServer(new MessageEmptyMag());
                        return;
                    }
                }

                if (heldItem.getTag().getInt("CurrentFireMode") == 3 && this.burstCooldown == 0 && !this.isPressed) {
                    this.isPressed = true;
                    this.burstTracker = ((TimelessGunItem) heldItem.getItem()).getGun().getGeneral().getBurstCount();
                    fire(player, heldItem);
                    this.burstCooldown = ((TimelessGunItem) heldItem.getItem()).getGun().getGeneral().getBurstRate();
                } else if (this.burstCooldown == 0 && !this.isPressed) {
                    this.isPressed = true;
                    fire(player, heldItem);
                }

                if (emptyCheckCountDown > emptyCheckCoolDown) {
                    if (!(heldItem.getTag().getInt("AmmoCount") > 0)) {
                        emptyCheckCountDown = 0;
                        player.displayClientMessage(new TranslatableComponent("info.tac.out_of_ammo").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED), true);
                        PacketHandler.getPlayChannel().sendToServer(new MessageEmptyMag());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (!isInGame())
            return;

        if (!Keys.PULL_TRIGGER.isDown())
            this.isPressed = false;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof TimelessGunItem) {
                if (heldItem.getTag() == null) {
                    heldItem.getOrCreateTag();
                    return;
                }
                TimelessGunItem gunItem = (TimelessGunItem) heldItem.getItem();
                if (heldItem.getTag().getInt("CurrentFireMode") == 3 && Config.CLIENT.controls.burstPress.get()) {
                    if (this.burstTracker > 0)
                        fire(player, heldItem);
                    return;
                } else if (Keys.PULL_TRIGGER.isDown()) {
                    Gun gun = ((TimelessGunItem) heldItem.getItem()).getModifiedGun(heldItem);
                    if (gun.getGeneral().isAuto() && heldItem.getTag().getInt("CurrentFireMode") == 2) {
                        fire(player, heldItem);
                        return;
                    }
                    if (heldItem.getTag().getInt("CurrentFireMode") == 3 && !Config.CLIENT.controls.burstPress.get() && !this.clickUp && this.burstCooldown == 0) {
                        if (this.burstTracker < gun.getGeneral().getBurstCount()) {
                            if (ShootingHandler.get().getshootMsGap() <= 0) {
                                fire(player, heldItem);
                                if (!this.shootErr)
                                    this.burstTracker++;
                            }
                        } else if (heldItem.getTag().getInt("AmmoCount") > 0 && this.burstTracker > 0) {
                            this.burstTracker = 0;
                            this.clickUp = true;
                            this.burstCooldown = gun.getGeneral().getBurstRate();
                        }
                        return;
                    }
                } else if (this.clickUp /*|| InputHandler.PULL_TRIGGER.down*/) {
                    if (heldItem.getTag().getInt("CurrentFireMode") == 3 && this.burstTracker > 0) {
                        this.burstCooldown = gunItem.getGun().getGeneral().getBurstRate();
                    }
                    this.burstTracker = 0;
                    this.clickUp = false;
                }
                // CONTINUER FOR BURST. USING BURST TRACKER AS A REVERSE ITOR WHEN BURST IS ON PRESS MODE.
            }
        }
    }

    public void fire(Player player, ItemStack heldItem) {
        if (magError(player, heldItem)) return;

        if (!(heldItem.getItem() instanceof GunItem))
            return;

        if (!Gun.hasAmmo(heldItem) && !player.isCreative())
            return;

        if (player.isSpectator())
            return;

        player.setSprinting(false);
        if (GunRenderingHandler.get().sprintTransition != 0) {
            this.shooting = false;
            return;
        }

        // CHECK HERE: Restrict the fire rate
//      if(!tracker.hasCooldown(heldItem.getItem()))
        if (shootTickGapLeft <= 0F) {
            GunItem gunItem = (GunItem) heldItem.getItem();
            Gun modifiedGun = gunItem.getModifiedGun(heldItem);

            if (MinecraftForge.EVENT_BUS.post(new GunFireEvent.Pre(player, heldItem)))
                return;
            ArmorInteractionHandler.get().resetRepairProgress(false);

            // CHECK HERE: Change this to test different rpm settings.
            // TODO: Test serverside, possible issues 0.3.4-alpha
            final float rpm = modifiedGun.getGeneral().getRate(); // Rounds per sec. Should come from gun properties in the end.
            shootTickGapLeft += calcShootTickGap((int) rpm);
            shootMsGap = calcShootTickGap((int) rpm);
            RecoilHandler.get().lastRandPitch = RecoilHandler.get().lastRandPitch;
            RecoilHandler.get().lastRandYaw = RecoilHandler.get().lastRandYaw;
            PacketHandler.getPlayChannel().sendToServer(new MessageShoot(player.getViewYRot(1), player.getViewXRot(1), RecoilHandler.get().lastRandPitch, RecoilHandler.get().lastRandYaw));

            if (Config.CLIENT.controls.burstPress.get()) this.burstTracker--;
            else this.burstTracker++;
            MinecraftForge.EVENT_BUS.post(new GunFireEvent.Post(player, heldItem));
        }
    }

    private boolean magError(Player player, ItemStack heldItem) {
        int[] extraAmmo = ((TimelessGunItem) heldItem.getItem()).getGun().getReloads().getMaxAdditionalAmmoPerOC();
        int magMode = GunModifierHelper.getAmmoCapacity(heldItem);
        if (magMode < 0) {
            if (heldItem.getItem() instanceof TimelessGunItem && heldItem.getTag().getInt("AmmoCount") - 1 > ((TimelessGunItem) heldItem.getItem()).getGun().getReloads().getMaxAmmo()) {
                return true;
            }
        } else {
            if (heldItem.getItem() instanceof TimelessGunItem && heldItem.getTag().getInt("AmmoCount") - 1 > ((TimelessGunItem) heldItem.getItem()).getGun().getReloads().getMaxAmmo() + extraAmmo[magMode]) {
                return true;
            }
        }
        return false;
    }
}