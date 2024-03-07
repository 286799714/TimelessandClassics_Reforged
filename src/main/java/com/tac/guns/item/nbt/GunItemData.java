package com.tac.guns.item.nbt;

import com.tac.guns.GunMod;
import com.tac.guns.api.gun.FireMode;
import com.tac.guns.api.item.IGun;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface GunItemData extends IGun {
    ResourceLocation DEFAULT = new ResourceLocation(GunMod.MOD_ID, "ak47");
    ResourceLocation DEFAULT_DISPLAY = new ResourceLocation(GunMod.MOD_ID, "ak47_display");
    ResourceLocation DEFAULT_DATA = new ResourceLocation(GunMod.MOD_ID, "ak47_data");

    String GUN_ID_TAG = "GunId";
    String GUN_FIRE_MODE_TAG = "GunFireMode";
    String GUN_CURRENT_AMMO_COUNT_TAG = "GunCurrentAmmoCount";

    @Override
    @Nonnull
    default ResourceLocation getGunId(ItemStack gun) {
        CompoundTag nbt = gun.getOrCreateTag();
        if (nbt.contains(GUN_ID_TAG, Tag.TAG_STRING)) {
            ResourceLocation gunId = ResourceLocation.tryParse(nbt.getString(GUN_ID_TAG));
            return Objects.requireNonNullElse(gunId, DEFAULT);
        }
        return DEFAULT;
    }

    @Override
    default void setGunId(ItemStack gun, @Nullable ResourceLocation gunId) {
        CompoundTag nbt = gun.getOrCreateTag();
        if (gunId != null) {
            nbt.putString(GUN_ID_TAG, gunId.toString());
            return;
        }
        nbt.putString(GUN_ID_TAG, DEFAULT.toString());
    }

    @Override
    default FireMode getFireMode(ItemStack gun) {
        CompoundTag nbt = gun.getOrCreateTag();
        if (nbt.contains(GUN_FIRE_MODE_TAG, Tag.TAG_STRING)) {
            return FireMode.valueOf(nbt.getString(GUN_FIRE_MODE_TAG));
        }
        return FireMode.UNKNOWN;
    }

    @Override
    default void setFireMode(ItemStack gun, @Nullable FireMode fireMode) {
        CompoundTag nbt = gun.getOrCreateTag();
        if (fireMode != null) {
            nbt.putString(GUN_FIRE_MODE_TAG, fireMode.name());
            return;
        }
        nbt.putString(GUN_FIRE_MODE_TAG, FireMode.UNKNOWN.name());
    }

    @Override
    default int getCurrentAmmoCount(ItemStack gun) {
        CompoundTag nbt = gun.getOrCreateTag();
        if (nbt.contains(GUN_CURRENT_AMMO_COUNT_TAG, Tag.TAG_INT)) {
            return nbt.getInt(GUN_CURRENT_AMMO_COUNT_TAG);
        }
        return 0;
    }

    @Override
    default void setCurrentAmmoCount(ItemStack gun, int ammoCount) {
        CompoundTag nbt = gun.getOrCreateTag();
        nbt.putInt(GUN_CURRENT_AMMO_COUNT_TAG, Math.max(ammoCount, 0));
    }

    @Override
    default void reduceCurrentAmmoCount(ItemStack gun) {
        setCurrentAmmoCount(gun, getCurrentAmmoCount(gun) - 1);
    }
}
