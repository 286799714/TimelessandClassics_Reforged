package com.tac.guns.client.handler;

import com.tac.guns.Config;
import com.tac.guns.common.Gun;
import com.tac.guns.common.ShootTracker;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class RecoilHandler
{
    private static RecoilHandler instance;

    public static RecoilHandler get()
    {
        if(instance == null)
        {
            instance = new RecoilHandler();
        }
        return instance;
    }

    private Map<Player, RecoilTracker> trackerMap = new WeakHashMap<>();

    private Random random = new Random();
    private int recoilRand;
    private double gunRecoilNormal;
    private double gunRecoilAngle;
    private double gunHorizontalRecoilAngle;
    public float cameraRecoil; // READONLY

    private float progressCameraRecoil;

    public float horizontalCameraRecoil; // READONLY

    private float horizontalProgressCameraRecoil;

    private int timer;

    private long prevTime = System.currentTimeMillis();

    private final int recoilDuration = 200; //0.20s

    private RecoilHandler() {}

    @SubscribeEvent
    public void preShoot(GunFireEvent.Pre event) {
        if(!(event.getStack().getItem() instanceof GunItem))
            return;
        this.recoilRand = this.random.nextInt(2);
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Post event)
    {
        if(!event.isClient())
            return;

        if(!Config.SERVER.enableCameraRecoil.get())
            return;

        ItemStack heldItem = event.getStack();
        GunItem gunItem = (GunItem) heldItem.getItem();
        Gun modifiedGun = gunItem.getModifiedGun(heldItem);

        float verticalRandomAmount = this.random.nextFloat()*(1.22f - 0.75f) + 0.75f;

        float recoilModifier = 1.0F - GunModifierHelper.getRecoilModifier(heldItem);
        recoilModifier *= this.getAdsRecoilReduction(modifiedGun);
        recoilModifier *= GunEnchantmentHelper.getBufferedRecoil(heldItem);
        recoilModifier *= verticalRandomAmount;
        this.cameraRecoil = modifiedGun.getGeneral().getRecoilAngle() * recoilModifier;
        this.progressCameraRecoil = 0F;

        // Horizontal Recoil
        this.lastRandPitch = random.nextFloat();
        this.lastRandYaw = random.nextFloat();

        float horizontalRandomAmount = this.random.nextFloat()*(1.22f - 0.75f) + 0.75f;

        float horizontalRecoilModifier = 1.0F - GunModifierHelper.getHorizontalRecoilModifier(heldItem);
        horizontalRecoilModifier *= this.getAdsRecoilReduction(modifiedGun);
        horizontalRecoilModifier *= GunEnchantmentHelper.getBufferedRecoil(heldItem);
        horizontalRecoilModifier *= horizontalRandomAmount;
        horizontalCameraRecoil = (modifiedGun.getGeneral().getHorizontalRecoilAngle() * horizontalRecoilModifier * 0.75F);
        horizontalProgressCameraRecoil = 0F;

        timer = recoilDuration;

        RecoilTracker tracker = getRecoilTracker(event.getPlayer());
        tracker.needRecoil = true;
        tracker.tick = 1;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(timer > 0) timer -= System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();
        if(timer < 0) timer = 0;

        if(!Config.SERVER.enableCameraRecoil.get())
            return;

        if(event.phase != TickEvent.Phase.END || this.cameraRecoil <= 0)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        float cameraRecoilModifer = mc.player.getMainHandItem().getItem() instanceof GunItem ? ((GunItem) mc.player.getMainHandItem().getItem()).getGun().getGeneral().getCameraRecoilModifier() : 1.0F;

        float recoilAmount = this.cameraRecoil * mc.getDeltaFrameTime() * 0.2F;//0.25F;//0.1F;
        float HorizontalRecoilAmount = this.horizontalCameraRecoil * mc.getDeltaFrameTime() * 0.1F;//0.25F;//* 0.1F;
        float startProgress = (this.progressCameraRecoil / this.cameraRecoil);
        float endProgress = ((this.progressCameraRecoil + recoilAmount) / this.cameraRecoil);

        float progressForward = mc.player.getMainHandItem().getItem() instanceof GunItem ? ((GunItem) mc.player.getMainHandItem().getItem()).getGun().getGeneral().getRecoilDuration() *
                GunModifierHelper.getRecoilSmootheningTime(mc.player.getMainHandItem()) : 0.25F;

        float delay = 0.10F;
        float slow = 1.75F;
        if(startProgress < progressForward - delay) // && startProgress > 0.125F
        {
            mc.player.setXRot(mc.player.getXRot() - ((endProgress - startProgress) / progressForward) * this.cameraRecoil / cameraRecoilModifer);
            if(recoilRand == 1)
                mc.player.setYRot(mc.player.getYRot() - ((endProgress - startProgress) / progressForward) * this.horizontalCameraRecoil / cameraRecoilModifer);
            else
                mc.player.setYRot(mc.player.getYRot() -  ((endProgress - startProgress) / progressForward) * -this.horizontalCameraRecoil / cameraRecoilModifer);
        }
        else if(startProgress > progressForward) {
            mc.player.setXRot((float) (mc.player.getXRot() + ((endProgress - startProgress) / (1 - progressForward)) * this.cameraRecoil / (cameraRecoilModifer * slow))); // 0.75F
            if (recoilRand == 1)
                mc.player.setYRot((float) (mc.player.getYRot() - ((endProgress - startProgress) / (1 - progressForward)) * -this.horizontalCameraRecoil / (cameraRecoilModifer * slow)));
            else
                mc.player.setYRot((float) (mc.player.getYRot() - ((endProgress - startProgress) / (1 - progressForward)) * this.horizontalCameraRecoil / (cameraRecoilModifer * slow)));
        }

            this.progressCameraRecoil += recoilAmount;

        if(this.progressCameraRecoil >= this.cameraRecoil)
        {
            this.cameraRecoil = 0;
            this.progressCameraRecoil = 0;
        }

        this.horizontalProgressCameraRecoil += HorizontalRecoilAmount;

        if(this.horizontalProgressCameraRecoil >= this.horizontalCameraRecoil)
        {
            this.horizontalCameraRecoil = 0;
            this.horizontalProgressCameraRecoil = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderOverlay(RenderHandEvent event)
    {
        if(event.getHand() != InteractionHand.MAIN_HAND)
            return;

        ItemStack heldItem = event.getItemStack();
        if(!(heldItem.getItem() instanceof GunItem))
            return;

        GunItem gunItem = (GunItem) heldItem.getItem();
        Gun modifiedGun = gunItem.getModifiedGun(heldItem);
        float cooldown = (float) timer / recoilDuration;

        float recoilTimeOffset = modifiedGun.getGeneral().getWeaponRecoilOffset();
        /*float cooldown ;
        if((tracker.getCooldown(gunItem, Minecraft.getInstance().getRenderPartialTicks()))<0.5f)
            cooldown = 0;/*(tracker.getCooldown(gunItem,
                    Minecraft.getInstance().getRenderPartialTicks()));
        else
        cooldown = (tracker.getCooldown(gunItem,
                Minecraft.getInstance().getRenderPartialTicks())-0.5f)*2f;*/
        if(cooldown >= recoilTimeOffset)// || tooFast) // Actually have any visual recoil at Rate 1???
        {
            float amount = 1F * ((1.0F - cooldown) / (1-modifiedGun.getGeneral().getWeaponRecoilOffset()));
            this.gunRecoilNormal = 1 - (--amount);
        }
        else {
            float amount = ( (cooldown) / modifiedGun.getGeneral().getWeaponRecoilOffset() );
            this.gunRecoilNormal = amount < 0.5 ? 2 * amount * amount : -1 + (4 - 2 * amount) * amount;
        }

        this.gunRecoilAngle = modifiedGun.getGeneral().getRecoilAngle();
        this.gunHorizontalRecoilAngle = modifiedGun.getGeneral().getHorizontalRecoilAngle();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        trackerMap.values().forEach(tracker -> {
            if(tracker.tick > 4) {
                tracker.needRecoil = false;
                tracker.tick = 0;
            }
            else if(tracker.tick > 0){
                tracker.needRecoil = true;
                tracker.tick++;
            }
        });
    }

    public double getAdsRecoilReduction(Gun gun)
    {
        return 1.0 - gun.getGeneral().getRecoilAdsReduction() * AimingHandler.get().getNormalisedAdsProgress();
    }

    public double getGunRecoilNormal()
    {
        return this.gunRecoilNormal;
    }

    public double getGunRecoilAngle()
    {
        return this.gunRecoilAngle;
    }

    public double getGunHorizontalRecoilAngle()
    {
        return this.gunHorizontalRecoilAngle;
    }

    public double getRecoilProgress() {return timer / (double)recoilDuration;}

    public RecoilTracker getRecoilTracker(Player player)
    {
        return trackerMap.computeIfAbsent(player, player1 -> new RecoilTracker());
    }

    private static Vec3 getVectorFromRotation(float pitch, float yaw)
    {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    private static Random rand = new Random();
    public float lastRandPitch = 0f;
    public float lastRandYaw = 0f;

    public static class RecoilTracker{
        private boolean needRecoil = false;
        private int tick = 0;

        public boolean isNeedRecoil() {
            return needRecoil;
        }

        public int getTick() {
            return tick;
        }
    }
}
