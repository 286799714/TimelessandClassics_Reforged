package com.tac.guns.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class IDLNBTUtil {
    //  As for item stack NBT, a total string length of 65000 is ok,
    //but 66000 will crash. Please do not spam it.
    //  Also, {} does not equals null, ignoring this causes unable to stack
    public static CompoundTag getNBT(ItemStack stack)
    {
        CompoundTag nbt = stack.getTag();
        if (nbt == null)
        {
            nbt = new CompoundTag();
            stack.setTag(nbt);
        }
        return nbt;
    }

    public static CompoundTag getNBTReadOnly(ItemStack stack)
    {
        CompoundTag nbt = stack.getTag();
        if (nbt == null)
        {
            nbt = new CompoundTag();
        }
        return nbt;
    }

    public static CompoundTag getNBT(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        return nbt;
    }


    public static CompoundTag getNBT(CompoundTag tag) {
        if(tag == null) {
            return new CompoundTag();
        }

        return tag;
    }

    //writeEntityToNBT
    //readEntityFromNBT

    @Nullable
    public static boolean stackHasKey(ItemStack stack, String key) {
        return !(stack.isEmpty() || !getNBTReadOnly(stack).contains(key));
    }

    //Boolean
    public static boolean setBoolean(ItemStack stack, String key, boolean value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putBoolean(key, value);
        return true;
    }

    public static boolean getBoolean(ItemStack stack, String key, boolean defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static boolean getBoolean(ItemStack stack, String key)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getBoolean(key);
        }
        else
        {
            return false;
        }
    }
    //get with default val
    public static boolean getBooleanDF(ItemStack stack, String key, boolean defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultVal;
        }
    }

    //Double
    public static boolean setDouble(ItemStack stack, String key, double value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putDouble(key, value);
        return true;
    }

    public static double getDouble(ItemStack stack, String key, double defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getDouble(key);
        }
        else
        {
            return defaultVal;
        }
    }

    //Integer
    public static boolean setLong(ItemStack stack, String key, long value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putLong(key, value);
        return true;
    }
    public static boolean setInt(ItemStack stack, String key, int value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putInt(key, value);
        return true;
    }
    public static boolean setIntOptimized(ItemStack stack, String key, int value)
    {
        CompoundTag nbt = getNBT(stack);
        if (nbt.getInt(key) != value)
        {
            nbt.putInt(key, value);
        }
        return true;
    }
    public static boolean setInt(Entity entity, String key, int value)
    {
        CompoundTag nbt = getNBT(entity);
        nbt.putInt(key, value);
        return true;
    }
    public static boolean setIntAuto(Entity entity, String key, int value)
    {
        if (entity instanceof Player)
        {
            setPlayerIdeallandTagSafe((Player) entity, key, value);
            return true;
        }
        CompoundTag nbt = getNBT(entity);
        nbt.putInt(key, value);
        return true;
    }

    public static boolean addIntAuto(Entity entity, String key, int value)
    {
        int oldVal = getIntAuto(entity, key, 0);
        setIntAuto(entity, key, value + oldVal);
        return true;
    }

    public static int getInt(Entity entity, String key, int defaultVal)
    {
        if (entityHasKey(entity, key))
        {
            CompoundTag nbt = getNBT(entity);
            return nbt.getInt(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static int getIntAuto(Entity entity, String key, int defaultVal)
    {
        if (entity instanceof Player)
        {
            return getPlayerIdeallandIntSafe((Player) entity, key);
        }

        if (entityHasKey(entity, key))
        {
            CompoundTag nbt = getNBT(entity);
            return nbt.getInt(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static int getInt(ItemStack stack, String key, int defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getInt(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static long getLong(ItemStack stack, String key, int defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getLong(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static int getInt(ItemStack stack, String key)
    {
        return getInt(stack, key, 0);
    }

    //String
    public static String getString(ItemStack stack, String key, String defaultVal)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getString(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static boolean setString(ItemStack stack, String key, String value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putString(key, value);

        return true;
    }


    //entity
    @Nullable
    public static boolean entityHasKey(Entity entity, String key)
    {
        return getNBT(entity).contains(key);
    }

    //Boolean
    public static boolean getBoolean(Entity entity, String key, boolean defaultVal)
    {
        if (entityHasKey(entity, key))
        {
            CompoundTag nbt = getNBT(entity);
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultVal;
        }
    }

    public static boolean setBoolean(Entity stack, String key, boolean value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putBoolean(key, value);
        return true;
    }

    public static boolean setString(Entity stack, String key, String value)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putString(key, value);
        return true;
    }

    public static int[] getIntArray(ItemStack stack, String key)
    {
        if (stackHasKey(stack, key))
        {
            CompoundTag nbt = getNBTReadOnly(stack);
            return nbt.getIntArray(key);
        }
        else
        {
            return new int[0];
        }
    }

    public static int[] getIntArray(LivingEntity entity, String key)
    {
        if (entityHasKey(entity, key))
        {
            CompoundTag nbt = getNBT(entity);
            return nbt.getIntArray(key);
        }
        else
        {
            return new int[0];
        }
    }

    public static void setIntArray(ItemStack stack, String key, int[] array)
    {
        CompoundTag nbt = getNBT(stack);
        nbt.putIntArray(key, array);
    }

    //Player section
    //Since multiple mods shares the player nbt, it is likely to have conficts.
    //This is why we put all our data under a special tag.
    public static final String MOD_SECTION_NAME = "tac_nbt";

    public static CompoundTag getTagSafe(CompoundTag tag, String key) {
        if(tag == null) {
            return new CompoundTag();
        }

        return tag.getCompound(key);
    }

    public static CompoundTag getPlyrIdlTagSafe(Player player) {
        if(player == null) {
            return new CompoundTag();
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);

        return getTagSafe(data, MOD_SECTION_NAME);
    }

    public static CompoundTag getPlayerIdeallandTagGroupSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getCompound(key);
    }

    public static int[] getPlayerIdeallandIntArraySafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getIntArray(key);
    }

    public static int getPlayerIdeallandIntSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getInt(key);
    }
    public static float getPlayerIdeallandFloatSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getFloat(key);
    }
    public static double getPlayerIdeallandDoubleSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getDouble(key);
    }
    public static boolean getPlayerIdeallandBoolSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getBoolean(key);
    }
    public static String getPlayerIdeallandStrSafe(Player player, String key) {
        return getPlyrIdlTagSafe(player).getString(key);
    }
    public static BlockPos getPlayerIdeallandBlockPosSafe(Player player, String key) {
        if (player == null)
        {
            return BlockPos.ZERO;
        }

        Tag inbt = getPlyrIdlTagSafe(player).get(key);
        if (inbt instanceof CompoundTag)
        {
            return NbtUtils.readBlockPos((CompoundTag) inbt);
        }
        return BlockPos.ZERO;
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, int value) {
        if (player == null)
        {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.putInt(key, value);

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, int[] value) {
        if (player == null)
        {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.putIntArray(key, value);

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, double value) {
        if (player == null)
        {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.putDouble(key, value);

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, boolean value) {
        if (player == null)
        {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.putBoolean(key, value);

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, String value) {
        if (player == null)
        {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.putString(key, value);

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static void setPlayerIdeallandTagSafe(Player player, String key, BlockPos value) {
        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = getTagSafe(playerData, Player.PERSISTED_NBT_TAG);
        CompoundTag idl_data = getPlyrIdlTagSafe(player);

        idl_data.put(key, NbtUtils.writeBlockPos(value));

        data.put(MOD_SECTION_NAME, idl_data);
        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    //--------------------------------------------
//
//    public static BlockPos getMarkedPos(ItemStack stack)
//    {
//        CompoundNBT NBT = IDLNBTUtil.getNBT(stack);
//        return new BlockPos(NBT.getDouble(ANCHOR_X), NBT.getDouble(ANCHOR_Y), NBT.getDouble(ANCHOR_Z));
//    }
//
//    public static BlockPos getMarkedPos2(ItemStack stack)
//    {
//        CompoundNBT NBT = IDLNBTUtil.getNBT(stack);
//        return new BlockPos(NBT.getDouble(ANCHOR_X_2), NBT.getDouble(ANCHOR_Y_2), NBT.getDouble(ANCHOR_Z_2));
//    }
//
//    public static void markPosToStack(ItemStack stack, BlockPos pos)
//    {
//        IDLNBTUtil.SetBoolean(stack, ANCHOR_READY, true);
//        IDLNBTUtil.SetDouble(stack, ANCHOR_X, pos.getX());
//        IDLNBTUtil.SetDouble(stack, ANCHOR_Y, pos.getY());
//        IDLNBTUtil.SetDouble(stack, ANCHOR_Z, pos.getZ());
//    }
//
//    public static void markPosToStack2(ItemStack stack, BlockPos pos)
//    {
//        IDLNBTUtil.SetBoolean(stack, ANCHOR_READY_2, true);
//        IDLNBTUtil.SetDouble(stack, ANCHOR_X_2, pos.getX());
//        IDLNBTUtil.SetDouble(stack, ANCHOR_Y_2, pos.getY());
//        IDLNBTUtil.SetDouble(stack, ANCHOR_Z_2, pos.getZ());
//    }
}