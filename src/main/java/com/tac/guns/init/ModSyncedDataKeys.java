package com.tac.guns.init;

import com.mrcrayfish.framework.api.data.sync.Serializers;
import com.mrcrayfish.framework.api.data.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ModSyncedDataKeys
{
    public static final SyncedDataKey<LivingEntity, Boolean> AIMING = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "aiming"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .build();

    public static final SyncedDataKey<LivingEntity, Boolean> SHOOTING = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "shooting"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .build();

    public static final SyncedDataKey<LivingEntity, Boolean> RELOADING = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "reloading"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .build();

    public static final SyncedDataKey<LivingEntity, Boolean> STOP_ANIMA = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "stop_anima"))
            .defaultValueSupplier(() -> true)
            .resetOnDeath()
            .build();
    public static final SyncedDataKey<LivingEntity, Float> MOVING = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.FLOAT)
            .id(new ResourceLocation(Reference.MOD_ID, "moving"))
            .defaultValueSupplier(() -> 0f)
            .resetOnDeath()
            .build();

    public static final SyncedDataKey<LivingEntity, Boolean> QREPAIRING = SyncedDataKey.builder(SyncedClassKey.LIVING_ENTITY, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "qrepairing"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .build();

    public static void register()
    {
        SyncedEntityData.instance().registerDataKey(AIMING);
        SyncedEntityData.instance().registerDataKey(SHOOTING);
        SyncedEntityData.instance().registerDataKey(RELOADING);
        SyncedEntityData.instance().registerDataKey(MOVING);
        SyncedEntityData.instance().registerDataKey(STOP_ANIMA);
        SyncedEntityData.instance().registerDataKey(QREPAIRING);
    }
}
