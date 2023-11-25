package com.tac.guns.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.mojang.logging.LogUtils;
import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.annotation.Ignored;
import com.tac.guns.annotation.Optional;
import com.tac.guns.client.handler.command.GunEditor;
import com.tac.guns.interfaces.TGExclude;
import com.tac.guns.inventory.gear.InventoryListener;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.item.attachment.IScope;
import com.tac.guns.item.attachment.impl.Scope;
import com.tac.guns.util.WearableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class Gun implements INBTSerializable<CompoundTag>
{
    private General general = new General();
    private Reloads reloads = new Reloads();
    private Projectile projectile = new Projectile();
    private Sounds sounds = new Sounds();
    private Display display = new Display();
    private Modules modules = new Modules();

    public General getGeneral()
    {
        return this.general;
    }

    public Reloads getReloads()
    {
        return this.reloads;
    }

    public Projectile getProjectile()
    {
        return this.projectile;
    }

    public Sounds getSounds()
    {
        return this.sounds;
    }

    public Display getDisplay()
    {
        return this.display;
    }

    public Modules getModules()
    {
        return this.modules;
    }

    public static class General implements INBTSerializable<CompoundTag>
    {
        @Optional
        private boolean auto = false;
        @Optional
        private boolean boltAction = false;
        @Optional
        private int rate;
        @Optional
        private int burstRate = 8;
        @Optional
        private int burstCount = 3;
        @Optional
        private int[] rateSelector = new int[]{0,1};
        @Optional
        private float recoilAngle = 1.0F;
        @Optional
        private float recoilKick;
        @Optional
        private float horizontalRecoilAngle = 2.0F;
        @Optional
        private float cameraRecoilModifier = 1.75F; // How much to divide out of camera recoil, use for either softening camera shake while keeping high recoil feeling weapons
        @Optional
        private float recoilDuration = 0.25F;
        @Optional
        private float weaponRecoilOffset = 0.5F; // Recoil up until the weapon cooldown is under this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
        @Optional
        private float cameraRecoilDuration = 1F; // Percentage

        // this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
        @Optional
        private float visualRecoilPercent = 0F; // Percentage
        // this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
        @Optional
        private float recoilAdsReduction = 0.2F;
        @Optional
        private int projectileAmount = 1;
        @Optional
        private int projToMinAccuracy = 5;
        @Optional
        private int msToAccuracyReset = 425;
        @Optional
        private boolean alwaysSpread = false;
        @Optional
        private float spread = 1.0f;
        @Optional
        private float firstShotSpread = 0.0f;
        @Optional
        private float weightKilo = 0.0F;
        @Ignored
        @TGExclude
        private GripType gripType = GripType.ONE_HANDED;
        @Optional
        private float movementInaccuracy = 1F;
        @Optional
        private float hipFireInaccuracy = 3.25F;
        @Optional
        private float levelReq = 300.0F;
        @Optional
        private int upgradeBenchMaxUses = 3;
        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Auto", this.auto);
            tag.putBoolean("BoltAction", this.boltAction);
            tag.putInt("Rate", this.rate);
            tag.putInt("BurstRate", this.burstRate);
            tag.putInt("BurstCount", this.burstCount);
            tag.putIntArray("RateSelector", this.rateSelector);
            tag.putString("GripType", this.gripType.getId().toString());
            tag.putFloat("RecoilAngle", this.recoilAngle); // x2 for quick camera recoil reduction balancing
            tag.putFloat("RecoilKick", this.recoilKick);
            tag.putFloat("HorizontalRecoilAngle", this.horizontalRecoilAngle); // x2 for quick camera recoil reduction balancing
            tag.putFloat("CameraRecoilModifier", this.cameraRecoilModifier);
            tag.putFloat("RecoilDurationOffset", this.recoilDuration);
            tag.putFloat("weaponRecoilOffset", this.weaponRecoilOffset);
            tag.putFloat("CameraRecoilDuration", this.cameraRecoilDuration);
            tag.putFloat("VisualRecoilDuration", this.visualRecoilPercent);
            tag.putFloat("RecoilAdsReduction", this.recoilAdsReduction);
            tag.putInt("ProjectileAmount", this.projectileAmount);
            tag.putInt("ProjToMinAccuracy", this.projToMinAccuracy);
            tag.putInt("MsToAccuracyRest", this.msToAccuracyReset);
            tag.putBoolean("AlwaysSpread", this.alwaysSpread);
            tag.putFloat("Spread", this.spread);
            tag.putFloat("FirstShotSpread", this.firstShotSpread);
            tag.putFloat("WeightKilo", this.weightKilo);
            tag.putFloat("LevelReq", this.levelReq);
            tag.putInt("UpgradeBenchMaxUses", this.upgradeBenchMaxUses);
            tag.putFloat("MovementInaccuracy", this.movementInaccuracy); // Movement inaccuracy modifier
            tag.putFloat("HipFireInaccuracy", this.hipFireInaccuracy); // Movement inaccuracy modifier
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Auto", Tag.TAG_ANY_NUMERIC))
            {
                this.auto = tag.getBoolean("Auto");
            }
            if(tag.contains("BoltAction", Tag.TAG_ANY_NUMERIC))
            {
                this.boltAction = tag.getBoolean("BoltAction");
            }
            if(tag.contains("Rate", Tag.TAG_ANY_NUMERIC))
            {
                this.rate = tag.getInt("Rate");
            }
            if(tag.contains("BurstRate", Tag.TAG_ANY_NUMERIC))
            {
                this.burstRate = tag.getInt("BurstRate");
            }
            if(tag.contains("BurstCount", Tag.TAG_ANY_NUMERIC))
            {
                this.burstCount = tag.getInt("BurstCount");
            }
            if(tag.contains("RateSelector", Tag.TAG_INT_ARRAY))
            {
                this.rateSelector = tag.getIntArray("RateSelector");
            }
            if(tag.contains("GripType", Tag.TAG_STRING))
            {
                this.gripType = GripType.getType(ResourceLocation.tryParse(tag.getString("GripType")));
            }
            if(tag.contains("RecoilAngle", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilAngle = tag.getFloat("RecoilAngle");
            }
            if(tag.contains("RecoilKick", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilKick = tag.getFloat("RecoilKick");
            }
            if(tag.contains("HorizontalRecoilAngle", Tag.TAG_ANY_NUMERIC))
            {
                this.horizontalRecoilAngle = tag.getFloat("HorizontalRecoilAngle");
            }
            if(tag.contains("CameraRecoilModifier", Tag.TAG_ANY_NUMERIC))
            {
                this.cameraRecoilModifier = tag.getFloat("CameraRecoilModifier");
            }
            if(tag.contains("RecoilDurationOffset", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilDuration = tag.getFloat("RecoilDurationOffset");
            }
            if(tag.contains("weaponRecoilOffset", Tag.TAG_ANY_NUMERIC))
            {
                this.weaponRecoilOffset = tag.getFloat("weaponRecoilOffset");
            }
            if(tag.contains("CameraRecoilDuration", Tag.TAG_ANY_NUMERIC))
            {
                this.cameraRecoilDuration = tag.getFloat("CameraRecoilDuration");
            }
            if(tag.contains("VisualRecoilDuration", Tag.TAG_ANY_NUMERIC))
            {
                this.visualRecoilPercent = tag.getFloat("VisualRecoilDuration");
            }
            if(tag.contains("RecoilAdsReduction", Tag.TAG_ANY_NUMERIC))
            {
                this.recoilAdsReduction = tag.getFloat("RecoilAdsReduction");
            }
            if(tag.contains("ProjectileAmount", Tag.TAG_ANY_NUMERIC))
            {
                this.projectileAmount = tag.getInt("ProjectileAmount");
            }
            if(tag.contains("ProjToMinAccuracy", Tag.TAG_ANY_NUMERIC))
            {
                this.projToMinAccuracy = tag.getInt("ProjToMinAccuracy");
            }
            if(tag.contains("MsToAccuracyRest", Tag.TAG_ANY_NUMERIC))
            {
                this.msToAccuracyReset = tag.getInt("MsToAccuracyRest");
            }
            if(tag.contains("UpgradeBenchMaxUses", Tag.TAG_ANY_NUMERIC))
            {
                this.upgradeBenchMaxUses = tag.getInt("UpgradeBenchMaxUses");
            }
            if(tag.contains("AlwaysSpread", Tag.TAG_ANY_NUMERIC))
            {
                this.alwaysSpread = tag.getBoolean("AlwaysSpread");
            }
            if(tag.contains("Spread", Tag.TAG_ANY_NUMERIC))
            {
                this.spread = tag.getFloat("Spread");
            }
            if(tag.contains("FirstShotSpread", Tag.TAG_ANY_NUMERIC))
            {
                this.firstShotSpread = tag.getFloat("FirstShotSpread");
            }
            if(tag.contains("WeightKilo", Tag.TAG_ANY_NUMERIC))
            {
                this.weightKilo = tag.getFloat("WeightKilo");
            }
            if(tag.contains("LevelReq", Tag.TAG_ANY_NUMERIC))
            {
                this.levelReq = tag.getFloat("LevelReq");
            }
            if(tag.contains("MovementInaccuracy", Tag.TAG_ANY_NUMERIC))
            {
                this.movementInaccuracy = tag.getFloat("MovementInaccuracy");
            }
            if(tag.contains("HipFireInaccuracy", Tag.TAG_ANY_NUMERIC))
            {
                this.hipFireInaccuracy = tag.getFloat("HipFireInaccuracy");
            }
        }

        /**
         * @return A copy of the general get
         */
        public General copy()
        {
            General general = new General();
            general.auto = this.auto;
            general.boltAction = this.boltAction;
            general.rate = this.rate;
            general.burstRate = this.burstRate;
            general.burstCount = this.burstCount;
            general.rateSelector = this.rateSelector;
            general.gripType = this.gripType;
            general.recoilAngle = this.recoilAngle;
            general.recoilKick = this.recoilKick;
            general.horizontalRecoilAngle = this.horizontalRecoilAngle;
            general.cameraRecoilModifier = this.cameraRecoilModifier;
            general.recoilDuration = this.recoilDuration;
            general.weaponRecoilOffset = this.weaponRecoilOffset;
            general.cameraRecoilDuration = this.cameraRecoilDuration;
            general.visualRecoilPercent = this.visualRecoilPercent;
            general.recoilAdsReduction = this.recoilAdsReduction;
            general.projectileAmount = this.projectileAmount;
            general.projToMinAccuracy = this.projToMinAccuracy;
            general.msToAccuracyReset = this.msToAccuracyReset;
            general.alwaysSpread = this.alwaysSpread;
            general.spread = this.spread;
            general.firstShotSpread = this.firstShotSpread;
            general.weightKilo = this.weightKilo;
            general.levelReq = this.levelReq;
            general.upgradeBenchMaxUses = this.upgradeBenchMaxUses;
            general.movementInaccuracy = this.movementInaccuracy;
            general.hipFireInaccuracy = this.hipFireInaccuracy;
            return general;
        }
        /**
         * @return If this gun is automatic or not
         */
        public boolean isAuto()
        {
            return this.auto;
        }
        /**
         * @return If the gun exits aim during Cooldown
         */
        public boolean isBoltAction()
        {
            return this.boltAction;
        }
        /**
         * @return The fire rate of this weapon in ticks
         */
        public int getRate()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? (int) (this.rate + GunEditor.get().getRateMod()) : (int)this.rate;
        }
        /**
         * @return The fire rate of this weapon in ticks
         */
        public int getUpgradeBenchMaxUses()
        {
            return this.upgradeBenchMaxUses;
        }
        /**
         * @return The fire rate of this weapon in ticks
         */
        public int getBurstRate()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? (int) (this.burstRate + GunEditor.get().getBurstRateMod()) : (int)this.burstRate;
        }
        public int getBurstCount()
        {
            return this.burstCount;
        }
        /**
         * @return The fire modes supported by the weapon, [0,1,2,3,4,5] [Safety, Single, Auto, Three round burst, Special 1, Special 2]
         */
        public int[] getRateSelector()
        {
            return this.rateSelector;
        }
        /**
         * @return The type of grip this weapon uses
         */
        public GripType getGripType()
        {
            return this.gripType;
        }
        /**
         * @return The amount of recoil this gun produces upon firing in degrees
         */
        public float getRecoilAngle()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.recoilAngle + GunEditor.get().getRecoilAngleMod() : (this.recoilAngle/1.5f);
        }
        /**
         * @return The amount of kick this gun produces upon firing
         */
        public float getRecoilKick()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.recoilKick + GunEditor.get().getRecoilKickMod() : (this.recoilKick/1.5f);
        }
        /**
         * @return The amount of horizontal kick this gun produces upon firing
         */
        public float getHorizontalRecoilAngle() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.horizontalRecoilAngle + GunEditor.get().getHorizontalRecoilAngleMod() : (this.horizontalRecoilAngle/1.5f);}
        /**
         * @return How much to divide out of camera recoil, use for either softening camera shake while keeping high recoil feeling weapons
         */
        public float getCameraRecoilModifier() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.cameraRecoilModifier + GunEditor.get().getCameraRecoilModifierMod() : this.cameraRecoilModifier;}
        /**
         * @return The duration offset for recoil. This reduces the duration of recoil animation
         */
        public float getRecoilDuration() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.recoilDuration + GunEditor.get().getRecoilDurationMod() : this.recoilDuration;}
        /**
         * @return Recoil (the weapon) up until the weapon cooldown is under this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
         */
        public float getWeaponRecoilOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.weaponRecoilOffset + GunEditor.get().getWeaponRecoilDurationMod() : this.weaponRecoilOffset;}
        /**
         * @return Recoil (the weapon) up until the weapon cooldown is under this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
         */
        public float getCameraRecoilDuration() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.cameraRecoilDuration + GunEditor.get().getWeaponRecoilDurationMod() : this.cameraRecoilDuration;}

        /**
         * @return Recoil (the weapon) up until the weapon cooldown is under this value (0.1 == 10% recoil time left, use to help scale with high firerate weapons and their weapon recoil feel)
         */
        public float getVisualRecoilPercent() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.visualRecoilPercent : this.visualRecoilPercent;}

        /**
         * @return The amount of reduction applied when aiming down this weapon's sight
         */
        public float getRecoilAdsReduction() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? (this.recoilAdsReduction + GunEditor.get().getRecoilAdsReductionMod())*2 : (this.recoilAdsReduction)*2;}

        /*public float getRecoilAdsReduction() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? (this.recoilAdsReduction + GunEditor.get().getRecoilAdsReductionMod())*2 : (this.recoilAdsReduction)*2;}*/
        /**
         * @return The amount of projectiles this weapon fires
         */
        public int getProjectileAmount()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? (int) (this.projectileAmount + GunEditor.get().getProjectileAmountMod()) : this.projectileAmount;
        }
        /**
         * @return If this weapon should always spread it's projectiles according to {@link #getSpread()}
         */
        public boolean isAlwaysSpread()
        {
            return this.alwaysSpread;
        }
        /**
         * @return The maximum amount of degrees applied to the initial pitch and yaw direction of
         * the fired projectile.
         */
        public float getSpread()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ?
                    (this.spread*0.5f + GunEditor.get().getSpreadMod()) : this.spread*0.5f;
        }
        /**
         * @return The default Kilogram weight of the weapon
         */
        public float getWeightKilo()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.weightKilo + GunEditor.get().getWeightKiloMod() : this.weightKilo;//*1.25f;
        }
        /**
         * @return The maximum amount of degrees applied to the initial pitch and yaw direction of
         * the fired projectile.
         */
        public float getLevelReq()
        {
            return this.levelReq;//*1.25f;
        }

        /**
         * @return Percentage of movement inaccuracy modification ((spread * (movement)) * movementInaccuracy)
         */
        public float getMovementInaccuracy()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.movementInaccuracy : this.movementInaccuracy;//*1.25f;
        }
        /**
         * @return Percentage of movement inaccuracy modification ((spread * (movement)) * movementInaccuracy)
         */
        public float getHipFireInaccuracy()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.general) ? this.hipFireInaccuracy*1.75f :
                    this.hipFireInaccuracy*1.75f;//*1.25f;
        }
        /**
         * @return The amount of projectiles the weapon fires before hitting minimum accuracy
         */
        public int getProjCountAccuracy()
        {
            return this.projToMinAccuracy;
        }
        /**
         * @return The initial amount of degrees applied to the initial pitch and yaw direction of
         * the fired projectile.
         */
        public float getFirstShotSpread()
        {
            return this.firstShotSpread;
        }
        /**
         * @return Miliseconds to wait per last fired shot before attempting to reset projectile count for accuracy calculation
         */
        public int getMsToAccuracyReset()
        {
            return this.msToAccuracyReset;
        }
    }

    public static class Reloads implements INBTSerializable<CompoundTag>
    {
        private int maxAmmo = 20;
        @Optional
        private boolean magFed = false;
        @Optional
        private int reloadMagTimer = 20;
        @Optional
        private int additionalReloadEmptyMagTimer = 0;
        @Optional
        private int reloadAmount = 1;
        @Optional
        private int[] maxAdditionalAmmoPerOC = new int[]{};
        @Optional
        private int preReloadPauseTicks = 0;
        @Optional
        private int interReloadPauseTicks = 1;
        @Optional
        private boolean openBolt = false;

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("MaxAmmo", this.maxAmmo);
            tag.putBoolean("MagFed", this.magFed);
            tag.putInt("ReloadSpeed", this.reloadAmount);
            tag.putInt("ReloadMagTimer", this.reloadMagTimer);
            tag.putInt("AdditionalReloadEmptyMagTimer", this.additionalReloadEmptyMagTimer);
            tag.putIntArray("MaxAmmunitionPerOverCap", this.maxAdditionalAmmoPerOC);
            tag.putInt("ReloadPauseTicks", this.preReloadPauseTicks);
            tag.putInt("InterReloadPauseTicks", this.interReloadPauseTicks);
            tag.putBoolean("OpenBolt", this.openBolt);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("MaxAmmo", Tag.TAG_ANY_NUMERIC))
            {
                this.maxAmmo = tag.getInt("MaxAmmo");
            }
            if(tag.contains("MagFed", Tag.TAG_ANY_NUMERIC))
            {
                this.magFed = tag.getBoolean("MagFed");
            }
            if(tag.contains("ReloadSpeed", Tag.TAG_ANY_NUMERIC))
            {
                this.reloadAmount = tag.getInt("ReloadSpeed");
            }
            if(tag.contains("ReloadMagTimer", Tag.TAG_ANY_NUMERIC))
            {
                this.reloadMagTimer = tag.getInt("ReloadMagTimer");
            }
            if(tag.contains("AdditionalReloadEmptyMagTimer", Tag.TAG_ANY_NUMERIC))
            {
                this.additionalReloadEmptyMagTimer = tag.getInt("AdditionalReloadEmptyMagTimer");
            }
            if(tag.contains("MaxAmmunitionPerOverCap", Tag.TAG_INT_ARRAY))
            {
                this.maxAdditionalAmmoPerOC = tag.getIntArray("MaxAmmunitionPerOverCap");
            }
            if(tag.contains("ReloadPauseTicks", Tag.TAG_ANY_NUMERIC))
            {
                this.preReloadPauseTicks = tag.getInt("ReloadPauseTicks");
            }
            if(tag.contains("InterReloadPauseTicks", Tag.TAG_ANY_NUMERIC))
            {
                this.interReloadPauseTicks = tag.getInt("InterReloadPauseTicks");
            }
            if(tag.contains("OpenBolt", Tag.TAG_ANY_NUMERIC))
            {
                this.openBolt = tag.getBoolean("OpenBolt");
            }
        }

        /**
         * @return A copy of the general get
         */
        public Reloads copy()
        {
            Reloads reloads = new Reloads();
            reloads.magFed = this.magFed;
            reloads.maxAmmo = this.maxAmmo;
            reloads.reloadAmount = this.reloadAmount;
            reloads.reloadMagTimer = this.reloadMagTimer;
            reloads.additionalReloadEmptyMagTimer = this.additionalReloadEmptyMagTimer;
            reloads.maxAdditionalAmmoPerOC = this.maxAdditionalAmmoPerOC;
            reloads.preReloadPauseTicks = this.preReloadPauseTicks;
            reloads.interReloadPauseTicks = this.interReloadPauseTicks;
            reloads.openBolt = this.openBolt;
            return reloads;
        }

        /**
         * @return Does this gun reload all ammunition following a single timer and replenish
         */
        public boolean isMagFed() {return this.magFed;}
        /**
         * @return The maximum amount of ammo this weapon can hold
         */
        public int getMaxAmmo()
        {
            return this.maxAmmo;
        }
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int getReloadAmount() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.reloadAmount + GunEditor.get().getReloadAmountMod()) : this.reloadAmount;}
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int getReloadMagTimer()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.reloadMagTimer + GunEditor.get().getReloadMagTimerMod()) : this.reloadMagTimer;
        }
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int getAdditionalReloadEmptyMagTimer()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() &&  GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.additionalReloadEmptyMagTimer + GunEditor.get().getAdditionalReloadEmptyMagTimerMod()) : this.additionalReloadEmptyMagTimer;
        }
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int[] getMaxAdditionalAmmoPerOC() {return this.maxAdditionalAmmoPerOC;}
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int getPreReloadPauseTicks()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.preReloadPauseTicks + GunEditor.get().getPreReloadPauseTicksMod()) : this.preReloadPauseTicks;
        }
        /**
         * @return The amount of ammo to add to the weapon each reload cycle
         */
        public int getinterReloadPauseTicks() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.reloads) ? (int) (this.interReloadPauseTicks + GunEditor.get().getInterReloadPauseTicksMod()) : this.interReloadPauseTicks;}
        /**
         * @return Does this gun reload all ammunition following a single timer and replenish
         */
        public boolean isOpenBolt()
        {
            return this.openBolt;
        }
    }

    public static class Projectile implements INBTSerializable<CompoundTag>
    {
        // ONLY ON INGEST...
        @Optional
        private boolean visible = true;
        @Optional
        private float damage;
        @Optional
        private float armorIgnore = 1f;
        @Optional
        private float critical = 0f;
        @Optional
        private float criticalDamage = 1f;
        @Optional
        private float headDamage = 1f;
        @Optional
        private float closeDamage = 1f;
        @Optional
        private float decayStart = 0f;
        @Optional
        private float minDecayMultiplier = 0.1f;
        @Optional
        private float decayEnd = 1f;
        @Ignored
        private float size = 0.1f;
        @Optional
        private double speed;
        @Optional
        public int life;
        @Optional
        private boolean gravity = true;
        @Optional
        private boolean damageReduceOverLife = true;
        @Optional
        public int trailColor = 0xFFD289;
        @Optional
        public double trailLengthMultiplier = 4.35;

        //TODO: Actually use per gun now, currently not in use (UNUSED)
        @Optional
        private float trailRotationMultiplier = 0.0175f;
        @Optional
        private boolean ricochet = true;
        @TGExclude
        private ResourceLocation item = new ResourceLocation(Reference.MOD_ID, "basic_ammo");
        @Optional
        private int bulletClass = 1;
        @Optional
        private float bluntDamagePercentage = 0.5f;
        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putString("Item", this.item.toString());
            tag.putBoolean("Visible", this.visible);
            tag.putFloat("Damage", this.damage);
            tag.putFloat("ArmorIgnore", this.armorIgnore);
            tag.putFloat("Critical", this.critical);
            tag.putFloat("CriticalDamage", this.criticalDamage);
            tag.putFloat("HeadDamage", this.headDamage);
            tag.putFloat("CloseDamage", this.closeDamage);
            tag.putFloat("DecayStart", this.decayStart);
            tag.putFloat("MinDecayMultiplier", this.minDecayMultiplier);
            tag.putFloat("DecayEnd", this.decayEnd);
            tag.putFloat("Size", this.size);
            tag.putDouble("Speed", this.speed);
            tag.putInt("Life", this.life);
            tag.putBoolean("Gravity", this.gravity);
            tag.putBoolean("DamageReduceOverLife", this.damageReduceOverLife);
            tag.putInt("TrailColor", this.trailColor);
            tag.putDouble("TrailLengthMultiplier", this.trailLengthMultiplier);
            tag.putFloat("TrailRotationMultiplier", this.trailRotationMultiplier);
            tag.putBoolean("Ricochet", this.ricochet);
            tag.putInt("BulletClass", this.bulletClass);
            tag.putFloat("BluntDamagePercentage", this.bluntDamagePercentage);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Item", Tag.TAG_STRING))
            {
                this.item = new ResourceLocation(tag.getString("Item"));
            }
            if(tag.contains("Visible", Tag.TAG_ANY_NUMERIC))
            {
                this.visible = tag.getBoolean("Visible");
            }
            if(tag.contains("Damage", Tag.TAG_ANY_NUMERIC))
            {
                this.damage = tag.getFloat("Damage");
            }
            if(tag.contains("ArmorIgnore",Tag.TAG_ANY_NUMERIC))
            {
                this.armorIgnore = tag.getFloat("ArmorIgnore");
            }
            if(tag.contains("Critical", Tag.TAG_ANY_NUMERIC))
            {
                this.critical = tag.getFloat("Critical");
            }
            if(tag.contains("CriticalDamage", Tag.TAG_ANY_NUMERIC))
            {
                this.criticalDamage = tag.getFloat("CriticalDamage");
            }
            if(tag.contains("HeadDamage", Tag.TAG_ANY_NUMERIC))
            {
                this.headDamage = tag.getFloat("HeadDamage");
            }
            if (tag.contains("CloseDamage", Tag.TAG_ANY_NUMERIC)) {
                this.closeDamage = tag.getFloat("CloseDamage");
            }
            if (tag.contains("DecayStart", Tag.TAG_ANY_NUMERIC)) {
                this.decayStart = tag.getFloat("DecayStart");
            }
            if (tag.contains("MinDecayMultiplier", Tag.TAG_ANY_NUMERIC)) {
                this.minDecayMultiplier = tag.getFloat("MinDecayMultiplier");
            }
            if (tag.contains("DecayEnd", Tag.TAG_ANY_NUMERIC)) {
                this.decayEnd = tag.getFloat("DecayEnd");
            }
            if(tag.contains("Size", Tag.TAG_ANY_NUMERIC))
            {
                this.size = tag.getFloat("Size");
            }
            if(tag.contains("Speed", Tag.TAG_ANY_NUMERIC))
            {
                this.speed = tag.getDouble("Speed");
            }
            if(tag.contains("Life", Tag.TAG_ANY_NUMERIC))
            {
                this.life = tag.getInt("Life");
            }
            if(tag.contains("Gravity", Tag.TAG_ANY_NUMERIC))
            {
                this.gravity = tag.getBoolean("Gravity");
            }
            if(tag.contains("DamageReduceOverLife", Tag.TAG_ANY_NUMERIC))
            {
                this.damageReduceOverLife = tag.getBoolean("DamageReduceOverLife");
            }
            if(tag.contains("TrailColor", Tag.TAG_ANY_NUMERIC))
            {
                this.trailColor = tag.getInt("TrailColor");
            }
            if(tag.contains("TrailLengthMultiplier", Tag.TAG_ANY_NUMERIC))
            {
                this.trailLengthMultiplier = tag.getDouble("TrailLengthMultiplier");
            }
            if(tag.contains("TrailRotationMultiplier", Tag.TAG_ANY_NUMERIC))
            {
                this.trailRotationMultiplier = tag.getFloat("TrailRotationMultiplier");
            }
            if(tag.contains("Ricochet", Tag.TAG_ANY_NUMERIC))
            {
                this.ricochet = tag.getBoolean("Ricochet");
            }
            if(tag.contains("BulletClass", Tag.TAG_ANY_NUMERIC))
            {
                this.bulletClass = tag.getInt("BulletClass");
            }
            if(tag.contains("BluntDamagePercentage", Tag.TAG_ANY_NUMERIC))
            {
                this.bluntDamagePercentage = tag.getFloat("bluntDamagePercentage");
            }
        }

        public Projectile copy()
        {
            Projectile projectile = new Projectile();
            projectile.item = this.item;
            projectile.visible = this.visible;
            projectile.damage = this.damage;
            projectile.armorIgnore = this.armorIgnore;
            projectile.critical = this.critical;
            projectile.criticalDamage = this.criticalDamage;
            projectile.headDamage = this.headDamage;
            projectile.closeDamage = this.closeDamage;
            projectile.decayStart = this.decayStart;
            projectile.minDecayMultiplier = this.minDecayMultiplier;
            projectile.decayEnd = this.decayEnd;
            projectile.size = this.size;
            projectile.speed = this.speed;
            projectile.life = this.life;
            projectile.gravity = this.gravity;
            projectile.damageReduceOverLife = this.damageReduceOverLife;
            projectile.trailColor = this.trailColor;
            projectile.trailLengthMultiplier = this.trailLengthMultiplier;
            projectile.trailRotationMultiplier = this.trailRotationMultiplier;
            projectile.ricochet = this.ricochet;
            projectile.bulletClass = this.bulletClass;
            projectile.bluntDamagePercentage = this.bluntDamagePercentage;
            return projectile;
        }

        /**
         * @return The registry id of the ammo item
         */
        public ResourceLocation getItem()
        {
            return this.item;
        }

        /**
         * @return If this projectile should be visible when rendering
         */
        public boolean isVisible()
        {
            return this.visible;
        }

        /**
         * @return The Damage caused by this projectile
         */
        public float getDamage()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.damage + GunEditor.get().getDamageMod()) : this.damage;
        }

        /**
         * @return The ArmorIgnore caused by this projectile
         */
        public float getGunArmorIgnore()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.armorIgnore + GunEditor.get().getArmorIgnoreMod()) : this.armorIgnore;
        }

        /**
         * @return The Critical caused by this projectile
         */
        public float getGunCritical()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.critical + GunEditor.get().getCriticalMod()) : this.critical;
        }

        /**
         * @return The CriticalDamage caused by this projectile
         */
        public float getGunCriticalDamage()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.criticalDamage + GunEditor.get().getCriticalDamageMod()) : this.criticalDamage;
        }

        /**
         * @return The HeadDamage caused by this projectile
         */
        public float getGunHeadDamage()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.headDamage + GunEditor.get().getHeadDamageMod()) : this.headDamage;
        }

        /**
         * @return The CloseDamage caused by this projectile
         */
        public float getGunCloseDamage() {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.closeDamage + GunEditor.get().getCloseDamageMod()) : this.closeDamage;
        }

        /**
         * @return The decay start position of this projectile
         */
        public float getGunDecayStart() {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.decayStart + GunEditor.get().getDecayStartMod()) : this.decayStart;
        }

        /**
         * @return The min decay percentage of this projectile
         */
        public float getGunMinDecayMultiplier() {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.minDecayMultiplier + GunEditor.get().getMinDecayMultiplierMod()) : this.minDecayMultiplier;
        }

        /**
         * @return The decay end position of this projectile
         */
        public float getGunDecayEnd() {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.decayEnd + GunEditor.get().getDecayEndMod()) : this.decayEnd;
        }

        /**
         * @return The size of the projectile entity bounding box
         */
        public float getSize()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ? (this.size + GunEditor.get().getDamageMod()) : this.size;
        }

        /**
         * @return The speed the projectile moves every tick
         */
        public double getSpeed()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ?
                    (this.speed + GunEditor.get().getSpeedMod()) : this.speed;
        }

        /**
         * @return The amount of ticks before tsis projectile is removed
         */
        public int getLife()
        {
            return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.projectile) ?
                    (int) (this.life + GunEditor.get().getLifeMod()) : this.life;
        }

        /**
         * @return If gravity should be applied to the projectile
         */
        public boolean isGravity()
        {
            return this.gravity;
        }

        /**
         * @return If the damage should reduce the further the projectile travels
         */
        public boolean isDamageReduceOverLife()
        {
            return this.damageReduceOverLife;
        }

        /**
         * @return The color of the projectile trail in rgba integer format
         */
        public int getTrailColor()
        {
            return this.trailColor;
        }

        /**
         * @return The multiplier to change the length of the projectile trail
         */
        public double getTrailLengthMultiplier()
        {
            return this.trailLengthMultiplier;
        }
        /**
         * @return The multiplier to change the rotation from muzzle to bullet, this helps close bullet trails render properly without purely relying on eye / view position
         */
        public float getTrailRotationMultiplier()
        {
            return this.trailRotationMultiplier;
        }
        /**
         * @return If the bullet will bounce off of hard blocks
         */
        public boolean isRicochet()
        {
            return this.ricochet;
        }
        /**
         * @return The class of the bullet, meaning what tiers of armor can stop it, a class 1 bullet hitting class 1 armor means only blunt damage is applied
         */
        public int getBulletClass()
        {
            return this.bulletClass;
        }
        /**
         * @return The percentage of damage applied as blunt when an armor class is met, 0 = armor stops all bullet damage.
         * 100 means all damage passes no matter, unless blunt damage is disabled within TaC config.
         */
        public float getBluntDamagePercentage()
        {
            return this.bluntDamagePercentage;
        }
    }

    public static class Sounds implements INBTSerializable<CompoundTag>
    {
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation fire;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reload;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadEmpty;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadNormal;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation pump;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation pullBolt;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadIntro;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadLoop;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadEnd;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation reloadEndEmpty;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation draw;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation inspect;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation inspectEmpty;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation cock;
        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation silencedFire;

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            if(this.fire != null)
            {
                tag.putString("Fire", this.fire.toString());
            }
            if(this.reload != null)
            {
                tag.putString("Reload", this.reload.toString());
            }
            if(this.cock != null)
            {
                tag.putString("Cock", this.cock.toString());
            }
            if(this.silencedFire != null)
            {
                tag.putString("SilencedFire", this.silencedFire.toString());
            }
            if(reloadEmpty != null)
            {
                tag.putString("ReloadEmpty", this.reloadEmpty.toString());
            }
            if(draw != null)
            {
                tag.putString("Draw", this.draw.toString());
            }
            if(inspectEmpty != null)
            {
                tag.putString("InspectEmpty", this.inspectEmpty.toString());
            }
            if(inspect != null)
            {
                tag.putString("Inspect", this.inspect.toString());
            }
            if(reloadNormal != null)
            {
                tag.putString("ReloadNormal", this.reloadNormal.toString());
            }
            if(pump != null)
            {
                tag.putString("Pump", this.pump.toString());
            }
            if(reloadIntro != null)
            {
                tag.putString("ReloadIntro", this.reloadIntro.toString());
            }
            if(reloadLoop != null)
            {
                tag.putString("ReloadLoop", this.reloadLoop.toString());
            }
            if(reloadEnd != null)
            {
                tag.putString("ReloadEnd", this.reloadEnd.toString());
            }
            if(pullBolt != null)
            {
                tag.putString("PullBolt", this.pullBolt.toString());
            }
            if(reloadEndEmpty != null)
            {
                tag.putString("ReloadEndEmpty", this.reloadEndEmpty.toString());
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Fire", Tag.TAG_STRING))
            {
                this.fire = this.createSound(tag, "Fire");
            }
            if(tag.contains("Reload", Tag.TAG_STRING))
            {
                this.reload = this.createSound(tag, "Reload");
            }
            if(tag.contains("Cock", Tag.TAG_STRING))
            {
                this.cock = this.createSound(tag, "Cock");
            }
            if(tag.contains("SilencedFire", Tag.TAG_STRING))
            {
                this.silencedFire = this.createSound(tag, "SilencedFire");
            }
            if(tag.contains("ReloadEmpty", Tag.TAG_STRING))
            {
                this.reloadEmpty = this.createSound(tag, "ReloadEmpty");
            }
            if(tag.contains("Draw", Tag.TAG_STRING)){
                this.draw = this.createSound(tag, "Draw");
            }
            if(tag.contains("InspectEmpty", Tag.TAG_STRING)){
                this.inspectEmpty = this.createSound(tag, "InspectEmpty");
            }
            if(tag.contains("Inspect", Tag.TAG_STRING)){
                this.inspect = this.createSound(tag, "Inspect");
            }
            if(tag.contains("ReloadNormal", Tag.TAG_STRING)){
                this.reloadNormal = this.createSound(tag, "ReloadNormal");
            }
            if(tag.contains("Pump", Tag.TAG_STRING)){
                this.pump = this.createSound(tag, "Pump");
            }
            if(tag.contains("ReloadIntro", Tag.TAG_STRING)){
                this.reloadIntro = this.createSound(tag, "ReloadIntro");
            }
            if(tag.contains("ReloadLoop", Tag.TAG_STRING)){
                this.reloadLoop = this.createSound(tag, "ReloadLoop");
            }
            if(tag.contains("ReloadEnd", Tag.TAG_STRING)){
                this.reloadEnd = this.createSound(tag, "ReloadEnd");
            }
            if(tag.contains("PullBolt", Tag.TAG_STRING)){
                this.pullBolt = this.createSound(tag, "PullBolt");
            }
            if(tag.contains("ReloadEndEmpty", Tag.TAG_STRING)){
                this.reloadEndEmpty = this.createSound(tag, "ReloadEndEmpty");
            }
        }

        public Sounds copy()
        {
            Sounds sounds = new Sounds();
            sounds.fire = this.fire;
            sounds.reload = this.reload;
            sounds.cock = this.cock;
            sounds.silencedFire = this.silencedFire;
            sounds.reloadEmpty = this.reloadEmpty;
            sounds.draw = this.draw;
            sounds.inspectEmpty = this.inspectEmpty;
            sounds.inspect = this.inspect;
            sounds.reloadNormal = this.reloadNormal;
            sounds.pump = this.pump;
            sounds.reloadIntro = this.reloadIntro;
            sounds.reloadLoop = this.reloadLoop;
            sounds.reloadEnd = this.reloadEnd;
            sounds.pullBolt = this.pullBolt;
            sounds.reloadEndEmpty = this.reloadEndEmpty;
            return sounds;
        }

        @Nullable
        private ResourceLocation createSound(CompoundTag tag, String key)
        {
            String sound = tag.getString(key);
            return sound.isEmpty() ? null : new ResourceLocation(sound);
        }

        /**
         * @return The registry id of the sound event when firing this weapon
         */
        @Nullable
        public ResourceLocation getFire()
        {
            return this.fire;
        }

        /**
         * @return The registry id of the sound event when reloading this weapon
         */
        @Nullable
        public ResourceLocation getReload()
        {
            return this.reload;
        }

        /**
         * @return The registry id of the sound event when cocking this weapon
         */
        @Nullable
        public ResourceLocation getCock()
        {
            return this.cock;
        }

        /**
         * @return The registry id of the sound event when silenced firing this weapon
         */
        @Nullable
        public ResourceLocation getSilencedFire()
        {
            return this.silencedFire;
        }

        /**
         * @return The registry id of the sound event when reloading firearms with no bullets in the gun
         */
        @Nullable
        public ResourceLocation getReloadEmpty() { return this.reloadEmpty; }

        /**
         * @return The registry id of the sound event when drawing.
         */
        @Nullable
        public ResourceLocation getDraw() { return this.draw; }

        @Nullable
        public ResourceLocation getInspectEmpty() { return this.inspectEmpty != null ? this.inspectEmpty : this.inspect; }
        /**
         * @return The registry id of the sound event when inspecting.
         */
        @Nullable
        public ResourceLocation getInspect() { return this.inspect; }

        /**
         * @return The registry id of the sound event when inspecting.
         */
        @Nullable
        public ResourceLocation getReloadNormal() { return this.reloadNormal; }

        @Nullable
        public ResourceLocation getPump() { return this.pump; }

        @Nullable
        public ResourceLocation getReloadIntro() { return this.reloadIntro; }

        @Nullable
        public ResourceLocation getReloadLoop() { return this.reloadLoop; }

        @Nullable
        public ResourceLocation getReloadEnd() { return this.reloadEnd; }

        @Nullable
        public ResourceLocation getPullBolt() { return this.pullBolt; }

        @Nullable
        public ResourceLocation getReloadEndEmpty() { return this.reloadEndEmpty; }
    }

    public static class Display implements INBTSerializable<CompoundTag>
    {
        @Optional
        @Nullable
        private Flash flash;

        @Optional
        @Nullable
        private ShellCasing shellCasing;

        @Optional
        private int weaponType = 0;

        @Optional
        private float hipfireScale = 0.75F;

        @Optional
        private float hipfireMoveScale = 0.5F;

        @Optional
        private float hipfireRecoilScale = 1.0F;

        @Optional
        private boolean showDynamicHipfire = true;

        public float getHipfireScale()
        {
            return this.hipfireScale;
        }

        public float getHipfireMoveScale()
        {
            return this.hipfireMoveScale;
        }

        public float getHipfireRecoilScale()
        {
            return this.hipfireRecoilScale;
        }

        public boolean isDynamicHipfire()
        {
            return this.showDynamicHipfire;
        }

        public WeaponType getWeaponType()
        {
            return WeaponType.values()[weaponType];
        }

        @Nullable
        public Flash getFlash()
        {
            return this.flash;
        }

        @Nullable
        public ShellCasing getShellCasing() {
            return this.shellCasing;
        }

        public static class Flash extends Positioned
        {
            private double size = 0.5;
            private double smokeSize = 2.0;
            private double trailAdjust = 1.15;

            @Override
            public CompoundTag serializeNBT()
            {
                CompoundTag tag = super.serializeNBT();
                tag.putDouble("Scale", this.size);
                tag.putDouble("SmokeSize", this.smokeSize);
                tag.putDouble("TrailAdjust", this.trailAdjust);
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag tag)
            {
                super.deserializeNBT(tag);
                if(tag.contains("Scale", Tag.TAG_ANY_NUMERIC))
                {
                    this.size = tag.getDouble("Scale");
                }
                if(tag.contains("TrailAdjust", Tag.TAG_ANY_NUMERIC))
                {
                    this.trailAdjust = tag.getDouble("TrailAdjust");
                }
                if(tag.contains("SmokeSize", Tag.TAG_ANY_NUMERIC))
                {
                    this.smokeSize = tag.getDouble("SmokeSize");
                }
            }

            public Flash copy()
            {
                Flash flash = new Flash();
                flash.size = this.size;
                flash.smokeSize = this.smokeSize;
                flash.trailAdjust = this.trailAdjust;
                flash.xOffset = this.xOffset;
                flash.yOffset = this.yOffset;
                flash.zOffset = this.zOffset;
                return flash;
            }

            /**
             * @return The size/scale of the muzzle flash render
             */
            public double getSize() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) ? this.size /*+ GunEditor.get().getSizeMod()*/ : this.size;}
            @Override
            public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) ? this.xOffset + GunEditor.get().getxMod() : this.xOffset;}
            @Override
            public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) ? this.yOffset + GunEditor.get().getyMod() : this.yOffset;}
            @Override
            public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) ? this.zOffset + GunEditor.get().getzMod() : this.zOffset;}

            public double getTrailAdjust() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) ? this.trailAdjust + GunEditor.get().getSizeMod() : this.trailAdjust;}

            /**
             * @return The size/scale of the muzzle smoke render
             */
            public double getSmokeSize() { return this.smokeSize;}
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            if(this.flash != null)
            {
                tag.put("Flash", this.flash.serializeNBT());
            }
            if (this.shellCasing != null) {
                tag.put("ShellCasing", this.shellCasing.serializeNBT());
            }
            tag.putFloat("HipFireScale", this.hipfireScale);
            tag.putFloat("HipFireMoveScale", this.hipfireMoveScale);
            tag.putFloat("HipFireRecoilScale", this.hipfireRecoilScale/2); // Compensate for camera changes
            tag.putBoolean("ShowDynamicHipfire", this.showDynamicHipfire);
            tag.putInt("WeaponType", this.weaponType);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Flash", Tag.TAG_COMPOUND))
            {
                CompoundTag flashTag = tag.getCompound("Flash");
                if(!flashTag.isEmpty())
                {
                    Flash flash = new Flash();
                    flash.deserializeNBT(tag.getCompound("Flash"));
                    this.flash = flash;
                }
                else
                {
                    this.flash = null;
                }
            }
            if (tag.contains("ShellCasing", Tag.TAG_COMPOUND)) {
                CompoundTag casingTag = tag.getCompound("ShellCasing");
                if (!casingTag.isEmpty()) {
                    ShellCasing shellCasing = new ShellCasing();
                    shellCasing.deserializeNBT(casingTag);
                    this.shellCasing = shellCasing;
                } else {
                    this.shellCasing = null;
                }
            }
            if(tag.contains("HipFireScale", Tag.TAG_ANY_NUMERIC))
            {
                this.hipfireScale = tag.getFloat("HipFireScale");
            }
            if(tag.contains("HipFireMoveScale", Tag.TAG_ANY_NUMERIC))
            {
                this.hipfireMoveScale = tag.getFloat("HipFireMoveScale");
            }
            if(tag.contains("HipFireRecoilScale", Tag.TAG_ANY_NUMERIC))
            {
                this.hipfireRecoilScale = tag.getFloat("HipFireRecoilScale");
            }
            if(tag.contains("ShowDynamicHipfire", Tag.TAG_ANY_NUMERIC))
            {
                this.showDynamicHipfire = tag.getBoolean("ShowDynamicHipfire");
            }
            if(tag.contains("WeaponType", Tag.TAG_ANY_NUMERIC))
            {
                this.weaponType = tag.getInt("WeaponType");
            }
        }

        public Display copy()
        {
            Display display = new Display();
            if(flash != null)
            {
                display.flash = flash.copy();
            }
            if (shellCasing != null) {
                display.shellCasing = shellCasing.copy();
            }
            if(hipfireScale != 0)
            {
                display.hipfireScale = this.hipfireScale;
            }
            if(hipfireMoveScale != 0)
            {
                display.hipfireMoveScale = this.hipfireMoveScale;
            }
            if(hipfireRecoilScale != 0)
            {
                display.hipfireRecoilScale = this.hipfireRecoilScale;
            }
            if(weaponType != 0)
            {
                display.weaponType = this.weaponType;
            }

            // Should always contain a value, true or false, does not check for empty
            display.hipfireRecoilScale = this.hipfireRecoilScale;

            return display;
        }
    }

    public static class Modules implements INBTSerializable<CompoundTag>
    {
        @Optional
        @Nullable
        private Zoom zoom = new Zoom();
        @Optional
        private Attachments attachments = new Attachments();

        @Nullable
        public Zoom getZoom()
        {
            return this.zoom.copy();
        }

        public Attachments getAttachments()
        {
            return this.attachments;
        }

        public static class Zoom extends Positioned
        {
            @Optional
            private float fovModifier = 0.8F;

            @Optional
            private double stabilityOffset = 0.225;

            @Override
            public CompoundTag serializeNBT()
            {
                CompoundTag tag = super.serializeNBT();
                tag.putFloat("FovModifier", this.fovModifier);
                tag.putDouble("StabilityOffset", this.stabilityOffset);
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag tag)
            {
                super.deserializeNBT(tag);
                if(tag.contains("FovModifier", Tag.TAG_ANY_NUMERIC))
                {
                    this.fovModifier = tag.getFloat("FovModifier");
                }
                if(tag.contains("StabilityOffset", Tag.TAG_ANY_NUMERIC))
                {
                    this.stabilityOffset = tag.getDouble("StabilityOffset");
                }
            }

            public Zoom copy()
            {
                Zoom zoom = new Zoom();
                zoom.fovModifier = this.fovModifier;
                zoom.stabilityOffset = this.stabilityOffset;
                zoom.xOffset = this.xOffset;
                zoom.yOffset = this.yOffset;
                zoom.zOffset = this.zOffset;
                return zoom;
            }

            public float getFovModifier()
            {
                return this.fovModifier;
            }
            public double getStabilityOffset()
            {
                return this.stabilityOffset;
            }

            //TODO: CLEAN DISGUSTING OVERRIDES, THIS IS FOR DEVELOPMENT TOOLING ONLY, ONLY FOR POSITIONED ENFORCED SYSTEMS!!!
            @Override
            public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.zoom) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
            @Override
            public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.zoom) ?
                    super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
            @Override
            public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.zoom) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
        }



        public static class Attachments implements INBTSerializable<CompoundTag>
        {
            @Optional
            @Nullable
            private ScaledPositioned scope;
            @Optional
            @Nullable
            private ScaledPositioned barrel;
            @Optional
            @Nullable
            private ScaledPositioned stock;
            @Optional
            @Nullable
            private ScaledPositioned underBarrel;
            @Optional
            @Nullable
            private ScaledPositioned gunSkin;
            @Optional
            @Nullable
            private ScaledPositioned sideRail;
            @Optional
            @Nullable
            private ScaledPositioned irDevice;
            @Optional
            @Nullable
            private ScaledPositioned extendedMag;
            @Optional
            @Nullable
            private ScaledPositioned oldScope;
            @Optional
            @Nullable
            private PistolScope pistolScope;
            @Optional
            @Nullable
            private ScaledPositioned pistolBarrel;

            @Nullable
            public ScaledPositioned getScope()
            {
                return this.scope;
            }

            @Nullable
            public ScaledPositioned getBarrel()
            {
                return this.barrel;
            }

            @Nullable
            public ScaledPositioned getStock()
            {
                return this.stock;
            }

            @Nullable
            public ScaledPositioned getUnderBarrel()
            {
                return this.underBarrel;
            }

            @Nullable
            public ScaledPositioned getGunSkin()
            {
                return this.gunSkin;
            }

            @Nullable
            public ScaledPositioned getSideRail()
            {
                return this.sideRail;
            }
            @Nullable
            public ScaledPositioned getIrDevice()
            {
                return this.irDevice;
            }
            @Nullable
            public ScaledPositioned getExtendedMag()
            {
                return this.extendedMag;
            }
            @Nullable
            public ScaledPositioned getOldScope()
            {
                return this.oldScope;
            }
            @Nullable
            public PistolScope getPistolScope()
            {
                return this.pistolScope;
            }
            @Nullable
            public ScaledPositioned getPistolBarrel()
            {
                return this.pistolBarrel;
            }


            @Override
            public CompoundTag serializeNBT()
            {
                CompoundTag tag = new CompoundTag();
                if(this.scope != null)
                {
                    tag.put("Scope", this.scope.serializeNBT());
                }
                if(this.barrel != null)
                {
                    tag.put("Barrel", this.barrel.serializeNBT());
                }
                if(this.stock != null)
                {
                    tag.put("Stock", this.stock.serializeNBT());
                }
                if(this.underBarrel != null)
                {
                    tag.put("UnderBarrel", this.underBarrel.serializeNBT());
                }
                if(this.gunSkin != null)
                {
                    tag.put("GunSkin", this.gunSkin.serializeNBT());
                }
                if(this.oldScope != null)
                {
                    tag.put("OldScope", this.oldScope.serializeNBT());
                }
                if(this.sideRail != null)
                {
                    tag.put("SideRail", this.sideRail.serializeNBT());
                }
                if(this.irDevice != null)
                {
                    tag.put("IrDevice", this.irDevice.serializeNBT());
                }
                if(this.extendedMag != null)
                {
                    tag.put("ExtendedMag", this.extendedMag.serializeNBT());
                }
                if(this.pistolScope != null)
                {
                    tag.put("PistolScope", this.pistolScope.serializeNBT());
                }
                if(this.pistolBarrel != null)
                {
                    tag.put("PistolBarrel", this.pistolBarrel.serializeNBT());
                }
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag tag)
            {
                if(tag.contains("Scope", Tag.TAG_COMPOUND))
                {
                    this.scope = this.createScope(tag, "Scope");
                }
                if(tag.contains("Barrel", Tag.TAG_COMPOUND))
                {
                    this.barrel = this.createBarrel(tag, "Barrel");
                }
                if(tag.contains("Stock", Tag.TAG_COMPOUND))
                {
                    this.stock = this.createScaledPositioned(tag, "Stock");
                }
                if(tag.contains("UnderBarrel", Tag.TAG_COMPOUND))
                {
                    this.underBarrel = this.createScaledPositioned(tag, "UnderBarrel");
                }
                if(tag.contains("GunSkin", Tag.TAG_COMPOUND))
                {
                    this.gunSkin = this.createScaledPositioned(tag, "GunSkin");
                }
                if(tag.contains("OldScope", Tag.TAG_COMPOUND))
                {
                    this.oldScope = this.createOldScope(tag, "OldScope");
                }
                if(tag.contains("SideRail", Tag.TAG_COMPOUND))
                {
                    this.sideRail = this.createScaledPositioned(tag, "SideRail");
                }
                if(tag.contains("IrDevice", Tag.TAG_COMPOUND))
                {
                    this.irDevice = this.createScaledPositioned(tag, "IrDevice");
                }
                if(tag.contains("ExtendedMag", Tag.TAG_COMPOUND))
                {
                    this.extendedMag = this.createScaledPositioned(tag, "ExtendedMag");
                }
                if(tag.contains("PistolScope", Tag.TAG_COMPOUND))
                {
                    this.pistolScope = this.createPistolScope(tag, "PistolScope");
                }
                if(tag.contains("PistolBarrel", Tag.TAG_COMPOUND))
                {
                    this.pistolBarrel = this.createPistolBarrel(tag, "PistolBarrel");
                }
            }

            public Attachments copy()
            {
                Attachments attachments = new Attachments();
                if(this.scope != null)
                {
                    attachments.scope = this.scope.copy();
                }
                if(this.barrel != null)
                {
                    attachments.barrel = this.barrel.copy();
                }
                if(this.stock != null)
                {
                    attachments.stock = this.stock.copy();
                }
                if(this.underBarrel != null)
                {
                    attachments.underBarrel = this.underBarrel.copy();
                }
                if(this.gunSkin != null)
                {
                    attachments.gunSkin = this.gunSkin.copy();
                }
                if(this.oldScope != null)
                {
                    attachments.oldScope = this.oldScope.copy();
                }
                if(this.sideRail != null)
                {
                    attachments.sideRail = this.sideRail.copy();
                }
                if(this.irDevice != null)
                {
                    attachments.irDevice = this.irDevice.copy();
                }
                if(this.extendedMag != null)
                {
                    attachments.extendedMag = this.extendedMag.copy();
                }
                if(this.pistolScope != null)
                {
                    attachments.pistolScope = this.pistolScope.copy();
                }
                if(this.pistolBarrel != null)
                {
                    attachments.pistolBarrel = this.pistolBarrel.copy();
                }
                return attachments;
            }

            @Nullable
            private ScaledPositioned createScaledPositioned(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new ScaledPositioned(attachment);
            }
            @Nullable
            private PistolScope createPistolScope(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new PistolScope(attachment) {
                    @Override
                    public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
                    @Override
                    public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
                    @Override
                    public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
                    @Override
                    public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.scale + GunEditor.get().getSizeMod() :     super.scale;}
                };
            }
            @Nullable
            private ScaledPositioned createScope(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new ScaledPositioned(attachment) {
                    @Override
                    public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.scope) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
                    @Override
                    public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.scope) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
                    @Override
                    public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.scope) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
                    @Override
                    public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.scope) ? this.scale + GunEditor.get().getSizeMod() :      this.scale;}
                };
            }
            @Nullable
            private ScaledPositioned createBarrel(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new ScaledPositioned(attachment) {
                    @Override
                    public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.barrel) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
                    @Override
                    public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.barrel) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
                    @Override
                    public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.barrel) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
                    @Override
                    public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.barrel) ? this.scale + GunEditor.get().getSizeMod() :      this.scale;}
                };
            }
            @Nullable
            private ScaledPositioned createPistolBarrel(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new ScaledPositioned(attachment) {
                    @Override
                    public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolBarrel) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
                    @Override
                    public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolBarrel) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
                    @Override
                    public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolBarrel) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
                    @Override
                    public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolBarrel) ? this.scale + GunEditor.get().getSizeMod() :       this.scale;}
                };
            }
            @Nullable
            private ScaledPositioned createOldScope(CompoundTag tag, String key) {
                CompoundTag attachment = tag.getCompound(key);
                return attachment.isEmpty() ? null : new ScaledPositioned(attachment) {
                    @Override
                    public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.oldScope) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
                    @Override
                    public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.oldScope) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
                    @Override
                    public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.oldScope) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
                    @Override
                    public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.oldScope) ? this.scale + GunEditor.get().getSizeMod() :      this.scale;}
                };
            }
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.put("Zoom", this.zoom.serializeNBT());
            tag.put("Attachments", this.attachments.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Zoom", Tag.TAG_COMPOUND))
            {
                this.zoom.deserializeNBT(tag.getCompound("Zoom"));
            }
            if(tag.contains("Attachments", Tag.TAG_COMPOUND))
            {
                this.attachments.deserializeNBT(tag.getCompound("Attachments"));
            }
        }
        public Modules copy()
        {
            Modules modules = new Modules();
            modules.zoom = this.zoom.copy();
            modules.attachments = this.attachments.copy();
            return modules;
        }
    }
    public static class Positioned implements INBTSerializable<CompoundTag>
    {
/*        public Positioned(CompoundNBT tag) {this.deserializeNBT(tag);}*/
        @Optional
        protected double xOffset = 0;
        @Optional
        protected double yOffset = 0;
        @Optional
        protected double zOffset = 0;

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("XOffset", this.xOffset);
            tag.putDouble("YOffset", this.yOffset);
            tag.putDouble("ZOffset", this.zOffset);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("XOffset", Tag.TAG_ANY_NUMERIC))
            {
                this.xOffset = tag.getDouble("XOffset");
            }
            if(tag.contains("YOffset", Tag.TAG_ANY_NUMERIC))
            {
                this.yOffset = tag.getDouble("YOffset");
            }
            if(tag.contains("ZOffset", Tag.TAG_ANY_NUMERIC))
            {
                this.zOffset = tag.getDouble("ZOffset");
            }
        }

        public double getXOffset() {return this.xOffset;}
        public double getYOffset() {return this.yOffset;}
        public double getZOffset() {return this.zOffset;}

        public Positioned copy()
        {
            Positioned positioned = new Positioned();
            positioned.xOffset = this.xOffset;
            positioned.yOffset = this.yOffset;
            positioned.zOffset = this.zOffset;
            return positioned;
        }
    }

    public static class ScaledPositioned extends Positioned
    {
        @Optional
        protected double scale = 1.0;

        public ScaledPositioned() {}

        public ScaledPositioned(CompoundTag tag)
        {
            this.deserializeNBT(tag);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = super.serializeNBT();
            tag.putDouble("Scale", this.scale);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            super.deserializeNBT(tag);
            if(tag.contains("Scale", Tag.TAG_ANY_NUMERIC))
            {
                this.scale = tag.getDouble("Scale");
            }
        }

        @Override
        public ScaledPositioned copy() {
            ScaledPositioned positioned = new ScaledPositioned();
            positioned.xOffset = this.xOffset;
            positioned.yOffset = this.yOffset;
            positioned.zOffset = this.zOffset;
            positioned.scale = this.scale;
            return positioned;
        }
        public double getScale() {return this.scale;}
    }

    public static class PistolScope extends ScaledPositioned
    {
        @Required
        private boolean doRenderMount;
        @Required
        private boolean doOnSlideMovement;
        //private HashSet

        public PistolScope() {}

        public PistolScope(CompoundTag tag)
        {
            this.deserializeNBT(tag);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = super.serializeNBT();
            tag.putBoolean("RenderMount", this.doRenderMount);
            tag.putBoolean("DoOnSlideMovement", this.doOnSlideMovement);
            //tag.put
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            super.deserializeNBT(tag);
            if(tag.contains("RenderMount", Tag.TAG_ANY_NUMERIC))
            {
                this.doRenderMount = tag.getBoolean("RenderMount");
            }
            if(tag.contains("DoOnSlideMovement", Tag.TAG_ANY_NUMERIC))
            {
                this.doOnSlideMovement = tag.getBoolean("DoOnSlideMovement");
            }
        }
        @Override
        public double getXOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getXOffset() + GunEditor.get().getxMod() : super.getXOffset();}
        @Override
        public double getYOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getYOffset() + GunEditor.get().getyMod() : super.getYOffset();}
        @Override
        public double getZOffset() {return (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? super.getZOffset() + GunEditor.get().getzMod() : super.getZOffset();}
        @Override
        public double getScale() {return   (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && Config.COMMON.development.enableTDev.get() && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.pistolScope) ? this.scale + GunEditor.get().getSizeMod() :      this.scale;}

        public boolean getDoRenderMount()
        {
            return this.doRenderMount;
        }
        public boolean getDoOnSlideMovement()
        {
            return this.doOnSlideMovement;
        }

        @Override
        public PistolScope copy()
        {
            PistolScope positioned = new PistolScope();
            positioned.xOffset = this.xOffset;
            positioned.yOffset = this.yOffset;
            positioned.zOffset = this.zOffset;
            positioned.scale = this.scale;
            positioned.doRenderMount = this.doRenderMount;
            positioned.doOnSlideMovement = this.doOnSlideMovement;
            return positioned;
        }
    }

    public static class ShellCasing extends ScaledPositioned {
        @Optional
        protected float velocityX = 0f;

        @Optional
        protected float velocityY = 0f;

        @Optional
        protected float velocityZ = 0f;

        @Optional
        protected float rVelocityX = 0f;

        @Optional
        protected float rVelocityY = 0f;

        @Optional
        protected float rVelocityZ = 0f;

        @Optional
        protected float aVelocityX = 0f;

        @Optional
        protected float aVelocityY = 0f;

        @Optional
        protected float aVelocityZ = 0f;

        @Optional
        @Nullable
        @TGExclude
        private ResourceLocation casingModel;

        @Optional
        protected int tickLife = 40;

        public ShellCasing() {
        }

        public ShellCasing(CompoundTag tag) {
            this.deserializeNBT(tag);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putFloat("VelocityX", velocityX);
            tag.putFloat("VelocityY", velocityY);
            tag.putFloat("VelocityZ", velocityZ);
            tag.putFloat("RVelocityX", rVelocityX);
            tag.putFloat("RVelocityY", rVelocityY);
            tag.putFloat("RVelocityZ", rVelocityZ);
            tag.putFloat("AVelocityX", aVelocityX);
            tag.putFloat("AVelocityY", aVelocityY);
            tag.putFloat("AVelocityZ", aVelocityZ);
            if (this.casingModel != null) {
                tag.putString("CasingModel", this.casingModel.toString());
            }
            tag.putInt("TickLife", tickLife);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            super.deserializeNBT(tag);
            if (tag.contains("VelocityX", Tag.TAG_ANY_NUMERIC)) {
                this.velocityX = tag.getFloat("VelocityX");
            }
            if (tag.contains("VelocityY", Tag.TAG_ANY_NUMERIC)) {
                this.velocityY = tag.getFloat("VelocityY");
            }
            if (tag.contains("VelocityZ", Tag.TAG_ANY_NUMERIC)) {
                this.velocityZ = tag.getFloat("VelocityZ");
            }
            if (tag.contains("RVelocityX", Tag.TAG_ANY_NUMERIC)) {
                this.rVelocityX = tag.getFloat("RVelocityX");
            }
            if (tag.contains("RVelocityY", Tag.TAG_ANY_NUMERIC)) {
                this.rVelocityY = tag.getFloat("RVelocityY");
            }
            if (tag.contains("RVelocityZ", Tag.TAG_ANY_NUMERIC)) {
                this.rVelocityZ = tag.getFloat("RVelocityZ");
            }
            if (tag.contains("AVelocityX", Tag.TAG_ANY_NUMERIC)) {
                this.aVelocityX = tag.getFloat("AVelocityX");
            }
            if (tag.contains("AVelocityY", Tag.TAG_ANY_NUMERIC)) {
                this.aVelocityY = tag.getFloat("AVelocityY");
            }
            if (tag.contains("AVelocityZ", Tag.TAG_ANY_NUMERIC)) {
                this.aVelocityZ = tag.getFloat("AVelocityZ");
            }
            if (tag.contains("CasingModel", Tag.TAG_STRING)) {
                this.casingModel = this.createResource(tag, "CasingModel");
            }
            if (tag.contains("TickLife", Tag.TAG_ANY_NUMERIC)) {
                this.tickLife = tag.getInt("TickLife");
            }
        }

        @Nullable
        private ResourceLocation createResource(CompoundTag tag, String key) {
            String resource = tag.getString(key);
            return resource.isEmpty() ? null : new ResourceLocation(resource);
        }

        @Override
        public ShellCasing copy() {
            ShellCasing ms = new ShellCasing();
            ms.xOffset = this.xOffset;
            ms.yOffset = this.yOffset;
            ms.zOffset = this.zOffset;
            ms.scale = this.scale;
            ms.velocityX = this.velocityX;
            ms.velocityY = this.velocityY;
            ms.velocityZ = this.velocityZ;
            ms.rVelocityX = this.rVelocityX;
            ms.rVelocityY = this.rVelocityY;
            ms.rVelocityZ = this.rVelocityZ;
            ms.aVelocityX = this.aVelocityX;
            ms.aVelocityY = this.aVelocityY;
            ms.aVelocityZ = this.aVelocityZ;
            ms.casingModel = this.casingModel;
            ms.tickLife = this.tickLife;
            return ms;
        }

        public float getVelocityX() {
            return velocityX;
        }

        public float getVelocityY() {
            return velocityY;
        }

        public float getVelocityZ() {
            return velocityZ;
        }

        public float getRVelocityX() {
            return rVelocityX;
        }

        public float getRVelocityY() {
            return rVelocityY;
        }

        public float getRVelocityZ() {
            return rVelocityZ;
        }

        public float getAVelocityX() {
            return aVelocityX;
        }

        public float getAVelocityY() {
            return aVelocityY;
        }

        public float getAVelocityZ() {
            return aVelocityZ;
        }

        public ResourceLocation getCasingModel() {
            return casingModel;
        }

        public int getTickLife() {
            return tickLife;
        }
    }


    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("General", this.general.serializeNBT());
        tag.put("Reloads", this.reloads.serializeNBT());
        tag.put("Projectile", this.projectile.serializeNBT());
        tag.put("Sounds", this.sounds.serializeNBT());
        tag.put("Display", this.display.serializeNBT());
        tag.put("Modules", this.modules.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        if(tag.contains("General", Tag.TAG_COMPOUND))
        {
            this.general.deserializeNBT(tag.getCompound("General"));
        }
        if(tag.contains("Reloads", Tag.TAG_COMPOUND))
        {
            this.reloads.deserializeNBT(tag.getCompound("Reloads"));
        }
        if(tag.contains("Projectile", Tag.TAG_COMPOUND))
        {
            this.projectile.deserializeNBT(tag.getCompound("Projectile"));
        }
        if(tag.contains("Sounds", Tag.TAG_COMPOUND))
        {
            this.sounds.deserializeNBT(tag.getCompound("Sounds"));
        }
        if(tag.contains("Display", Tag.TAG_COMPOUND))
        {
            this.display.deserializeNBT(tag.getCompound("Display"));
        }
        if(tag.contains("Modules", Tag.TAG_COMPOUND))
        {
            this.modules.deserializeNBT(tag.getCompound("Modules"));
        }
    }



    public static Gun create(CompoundTag tag)
    {
        Gun gun = new Gun();
        gun.deserializeNBT(tag);
        return gun;
    }

    public Gun copy()
    {
        Gun gun = new Gun();
        gun.general = this.general.copy();
        gun.reloads = this.reloads.copy();
        gun.projectile = this.projectile.copy();
        gun.sounds = this.sounds.copy();
        gun.display = this.display.copy();
        gun.modules = this.modules.copy();
        return gun;
    }

    public boolean canAttachType(@Nullable IAttachment.Type type)
    {
        if(this.modules.attachments != null && type != null)
        {
            switch(type)
            {
                case SCOPE:
                    return this.modules.attachments.scope != null;
                case BARREL:
                    return this.modules.attachments.barrel != null;
                case STOCK:
                    return this.modules.attachments.stock != null;
                case UNDER_BARREL:
                    return this.modules.attachments.underBarrel != null;
                case GUN_SKIN:
                    return this.modules.attachments.gunSkin != null;
                case SIDE_RAIL:
                    return this.modules.attachments.sideRail != null;
                case IR_DEVICE:
                    return this.modules.attachments.irDevice != null;
                case EXTENDED_MAG:
                    return this.modules.attachments.extendedMag != null;
                case OLD_SCOPE:
                    return this.modules.attachments.oldScope != null;
                case PISTOL_SCOPE:
                    return this.modules.attachments.pistolScope != null;
                case PISTOL_BARREL:
                    return this.modules.attachments.pistolBarrel != null;
            }
        }
        return false;
    }

    @Nullable
    public ScaledPositioned getAttachmentPosition(IAttachment.Type type)
    {
        if(this.modules.attachments != null)
        {
            switch(type)
            {
                case SCOPE:
                    return this.modules.attachments.scope;
                case BARREL:
                    return this.modules.attachments.barrel;
                case STOCK:
                    return this.modules.attachments.stock;
                case UNDER_BARREL:
                    return this.modules.attachments.underBarrel;
                case GUN_SKIN:
                    return this.modules.attachments.gunSkin;
                case SIDE_RAIL:
                    return this.modules.attachments.sideRail;
                case IR_DEVICE:
                    return this.modules.attachments.irDevice;
                case EXTENDED_MAG:
                    return this.modules.attachments.extendedMag;
                case OLD_SCOPE:
                    return this.modules.attachments.oldScope;
                case PISTOL_SCOPE:
                    return this.modules.attachments.pistolScope;
                case PISTOL_BARREL:
                    return this.modules.attachments.pistolBarrel;
            }
        }
        return null;
    }

    public boolean canAimDownSight()
    {
        return this.canAttachType(IAttachment.Type.SCOPE) || this.canAttachType(IAttachment.Type.OLD_SCOPE) || this.canAttachType(IAttachment.Type.PISTOL_SCOPE) || this.modules.zoom != null;
    }

   /* public static ItemStack getScopeStack(ItemStack gun)
    {
        CompoundNBT compound = gun.getTag();
        if(compound != null && compound.contains("Attachments", Tag.TAG_COMPOUND))
        {
            CompoundNBT attachment = compound.getCompound("Attachments");
            if(attachment.contains("Scope", Tag.TAG_COMPOUND))
            {
                return ItemStack.read(attachment.getCompound("Scope"));
            }
            else if(attachment.contains("OldScope", Tag.TAG_COMPOUND))
            {
                return ItemStack.read(attachment.getCompound("OldScope"));
            }
            else if(attachment.contains("PistolScope", Tag.TAG_COMPOUND))
            {
                return ItemStack.read(attachment.getCompound("PistolScope"));
            }
        }
        return ItemStack.EMPTY;
    }*/

    public static boolean hasAttachmentEquipped(ItemStack stack, Gun gun, IAttachment.Type type)
    {
        if(!gun.canAttachType(type))
            return false;

        CompoundTag compound = stack.getTag();
        if(compound != null && compound.contains("Attachments", Tag.TAG_COMPOUND))
        {
            CompoundTag attachment = compound.getCompound("Attachments");
            return attachment.contains(type.getTagKey(), Tag.TAG_COMPOUND);
        }
        return false;
    }

    @Nullable
    public static Scope getScope(ItemStack gun)
    {
        CompoundTag compound = gun.getTag();
        Scope scope = null;
        if(compound != null && compound.contains("Attachments", Tag.TAG_COMPOUND))
        {
            CompoundTag attachment = compound.getCompound("Attachments");
            if(attachment.contains("Scope", Tag.TAG_COMPOUND))
            {
                ItemStack scopeStack = ItemStack.of(attachment.getCompound("Scope"));
                if(scopeStack.getItem() instanceof IScope)
                {
                    scope = ((IScope) scopeStack.getItem()).getProperties();
                }

            }
            else if(attachment.contains("OldScope", Tag.TAG_COMPOUND))
            {
                ItemStack OldScopeStack = ItemStack.of(attachment.getCompound("OldScope"));
                if(OldScopeStack.getItem() instanceof IScope)
                {
                    scope = ((IScope) OldScopeStack.getItem()).getProperties();
                }
           }
            else if(attachment.contains("PistolScope", Tag.TAG_COMPOUND))
            {
                ItemStack OldScopeStack = ItemStack.of(attachment.getCompound("PistolScope"));
                if(OldScopeStack.getItem() instanceof IScope)
                {
                    scope = ((IScope) OldScopeStack.getItem()).getProperties();
                }
            }
        }
        return scope;
    }

    public static ItemStack getAttachment(IAttachment.Type type, ItemStack gun)
    {
        CompoundTag compound = gun.getTag();
        if(compound != null && compound.contains("Attachments", Tag.TAG_COMPOUND))
        {
            CompoundTag attachment = compound.getCompound("Attachments");
            if(attachment.contains(type.getTagKey(), Tag.TAG_COMPOUND))
            {
                return ItemStack.of(attachment.getCompound(type.getTagKey()));
            } else if(type == IAttachment.Type.SCOPE && (attachment.contains(IAttachment.Type.PISTOL_SCOPE.getTagKey(), Tag.TAG_COMPOUND) || attachment.contains(IAttachment.Type.OLD_SCOPE.getTagKey(), Tag.TAG_COMPOUND)))
                return !attachment.getCompound(IAttachment.Type.PISTOL_SCOPE.getTagKey()).isEmpty() ? ItemStack.of(attachment.getCompound(IAttachment.Type.PISTOL_SCOPE.getTagKey())) :
                        ItemStack.of(attachment.getCompound(IAttachment.Type.OLD_SCOPE.getTagKey()));
            else if(type == IAttachment.Type.SIDE_RAIL && (attachment.contains(IAttachment.Type.IR_DEVICE.getTagKey(), Tag.TAG_COMPOUND)))
                return ItemStack.of(attachment.getCompound(IAttachment.Type.IR_DEVICE.getTagKey()));

        }
        return ItemStack.EMPTY;
    }

    public static float getAdditionalDamage(ItemStack gunStack)
    {
        CompoundTag tag = gunStack.getOrCreateTag();
        return tag.getFloat("AdditionalDamage");
    }

    public static ItemStack[] findAmmo(Player player, ResourceLocation id) // Refactor to return multiple stacks, reload to take as much of value as required from hash
    {
        if(!player.isAlive())
            return new ItemStack[]{};
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if(player.isCreative())
        {
            Item item = ForgeRegistries.ITEMS.getValue(id);
            stacks.add(item != null ? new ItemStack(item, Integer.MAX_VALUE) : ItemStack.EMPTY);
            return stacks.toArray(new ItemStack[]{});
        }
        for(int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(isAmmo(stack, id)) {
                stacks.add(stack);
            }
        }
        // Get wearable that holds ammo
        ItemStack wornRig = WearableHelper.PlayerWornRig(player);
        if(!wornRig.isEmpty())
        {
            RigSlotsHandler itemHandler = (RigSlotsHandler) wornRig.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            List<ItemStack> list = itemHandler.getStacks();
            for(ItemStack ammoStack : list){
                if(isAmmo(ammoStack, id)) {
                    stacks.add(ammoStack);
                }
            }
        }

        return stacks.toArray(new ItemStack[]{});
    }

    // Only for HuD renderer, maybe reload check before message is sent
    public static ItemStack[] findAmmoRigOnly(ItemStack rig, ResourceLocation id) // Refactor to return multiple stacks, reload to take as much of value as required from hash
    {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if(rig != null)
        {
            ListTag nbtTagList = (ListTag) ((ArmorRigItem)rig.getItem()).getShareTag(rig).getCompound("storage").get("Items");
            for (int i = 0; i < ((ArmorRigItem)rig.getItem()).getShareTag(rig).getCompound("storage").getInt("Size"); i++)
            {
                stacks.add(ItemStack.of(nbtTagList.getCompound(i)));
            }
        }
        return stacks.toArray(new ItemStack[]{});
    }

    public static int ammoCountInRig(ItemStack rig, ResourceLocation id) // Refactor to return multiple stacks, reload to take as much of value as required from hash
    {
        int counter = 0;
        ItemStack[] stacks = findAmmoRigOnly(rig, id);
        for (ItemStack x : stacks)
        {
            if(x != null && x != ItemStack.EMPTY && x.getCount() != 0 && isAmmo(x, id))
                counter+=x.getCount();
        }
        return counter;
    }


    public static int ammoCountInRig(CompoundTag rigData, ResourceLocation id) // Refactor to return multiple stacks, reload to take as much of value as required from hash
    {
        int counter = 0;
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ListTag nbtTagList = (ListTag) rigData.getCompound("storage").get("Items");
        for (int i = 0; i < rigData.getCompound("storage").getInt("Size"); i++)
        {
            stacks.add(ItemStack.of(nbtTagList.getCompound(i)));
        }
        for (ItemStack x : stacks)
        {
            if(x != null && x != ItemStack.EMPTY && x.getCount() != 0 && isAmmo(x, id))
                counter+=x.getCount();
        }
        /*RigSlotsHandler itemHandler = (RigSlotsHandler) rig.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
        for (ItemStack x : itemHandler.getStacks()) {
            if(Gun.isAmmo(x, id))
                counter+=x);
        }*/
        return counter;
    }


    public static boolean isAmmo(ItemStack stack, ResourceLocation id)
    {
        return stack != null && stack.getItem().getRegistryName().equals(id);
    }

    public static boolean hasAmmo(ItemStack gunStack)
    {
        CompoundTag tag = gunStack.getOrCreateTag();
        return tag.getBoolean("IgnoreAmmo") || tag.getInt("AmmoCount") > 0;
    }
    public static ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            return field.getAnnotation(TGExclude.class) != null;
        }
    };
}
