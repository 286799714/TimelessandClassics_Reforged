package com.tac.guns.item;

import com.tac.guns.common.DiscardOffhand;
import com.tac.guns.common.Gun;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.enchantment.EnchantmentTypes;
import com.tac.guns.init.ModItems;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class GunItem extends Item implements IColored
{
    private WeakHashMap<CompoundNBT, Gun> modifiedGunCache = new WeakHashMap<>();

    private Gun gun = new Gun();
    public GunItem(Item.Properties properties)
    {
        super(properties);
    }

    public void setGun(NetworkGunManager.Supplier supplier)
    {
        this.gun = supplier.getGun();
    }

    public Gun getGun()
    {
        return this.gun;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        Gun modifiedGun = this.getModifiedGun(stack);

        Item ammo = ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
        if(ammo != null)
        {
            tooltip.add(new TranslationTextComponent("info.tac.ammo_type", new TranslationTextComponent(ammo.getDescriptionId()).withStyle(TextFormatting.WHITE)).withStyle(TextFormatting.GRAY));
        }

        String additionalDamageText = "";
        CompoundNBT tagCompound = stack.getTag();
        if(tagCompound != null)
        {
            if(tagCompound.contains("AdditionalDamage", Constants.NBT.TAG_ANY_NUMERIC))
            {
                float additionalDamage = tagCompound.getFloat("AdditionalDamage");
                additionalDamage += GunModifierHelper.getAdditionalDamage(stack);

                if(additionalDamage > 0)
                {
                    additionalDamageText = TextFormatting.GREEN + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                }
                else if(additionalDamage < 0)
                {
                    additionalDamageText = TextFormatting.RED + " " + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                }
            }
        }

        float damage = modifiedGun.getProjectile().getDamage();
        damage = GunModifierHelper.getModifiedProjectileDamage(stack, damage);
        damage = GunEnchantmentHelper.getAcceleratorDamage(stack, damage);
        tooltip.add(new TranslationTextComponent("info.tac.damage", TextFormatting.WHITE + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage) + additionalDamageText).withStyle(TextFormatting.GRAY));

        if(tagCompound != null)
        {
            if(tagCompound.getBoolean("IgnoreAmmo"))
            {
                tooltip.add(new TranslationTextComponent("info.tac.ignore_ammo").withStyle(TextFormatting.AQUA));
            }
            else
            {
                int ammoCount = tagCompound.getInt("AmmoCount");
                tooltip.add(new TranslationTextComponent("info.tac.ammo", TextFormatting.WHITE.toString() + ammoCount + "/" + GunModifierHelper.getAmmoCapacity(stack, modifiedGun)).withStyle(TextFormatting.GRAY));
            }
        }

        tooltip.add(new TranslationTextComponent("info.tac.attachment_help", new KeybindTextComponent("key.tac.attachments").getString().toUpperCase(Locale.ENGLISH)).withStyle(TextFormatting.YELLOW));
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        return true;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks)
    {
        if(this.allowdedIn(group))
        {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("AmmoCount", this.gun.getReloads().getMaxAmmo());
            stacks.add(stack);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getOrCreateTag();
        Gun modifiedGun = this.getModifiedGun(stack);
        return !tagCompound.getBoolean("IgnoreAmmo") && tagCompound.getInt("AmmoCount") != GunModifierHelper.getAmmoCapacity(stack, modifiedGun);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getOrCreateTag();
        Gun modifiedGun = this.getModifiedGun(stack);
        return 1.0 - (tagCompound.getInt("AmmoCount") / (double) GunModifierHelper.getAmmoCapacity(stack, modifiedGun));
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return Objects.requireNonNull(TextFormatting.AQUA.getColor());
    }

    public Gun getModifiedGun(ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getTag();
        if(tagCompound != null && tagCompound.contains("Gun", Constants.NBT.TAG_COMPOUND))
        {
            return this.modifiedGunCache.computeIfAbsent(tagCompound, item ->
            {
                if(tagCompound.getBoolean("Custom"))
                {
                    return Gun.create(tagCompound.getCompound("Gun"));
                }
                else
                {
                    Gun gunCopy = this.gun.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Gun"));
                    return gunCopy;
                }
            });
        }
        return this.gun;
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (isSelected && !worldIn.isClientSide)
        {
            if (entityIn instanceof PlayerEntity)
            {
                PlayerEntity playerEntity = (PlayerEntity) entityIn;
                if (!isSingleHanded(stack) && !DiscardOffhand.isSafeTime(playerEntity))
                {
                    ItemStack offHand = playerEntity.getOffhandItem();
                    if (!(offHand.getItem() instanceof GunItem) && !offHand.isEmpty()) {
                        ItemEntity entity = playerEntity.drop(offHand, false);
                        playerEntity.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                        if (entity != null)
                        {
                            entity.setNoPickUpDelay();
                        }
                    }
                }
            }
        }
    }

    public static boolean isSingleHanded(ItemStack stack)
    {
        Item item = stack.getItem();
        return item == ModItems.M1911.get() || item == ModItems.MICRO_UZI.get()
                || item == ModItems.STI2011.get() || item == ModItems.CZ75.get();
    }

    /*@Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        if(enchantment.type == EnchantmentTypes.SEMI_AUTO_GUN)
        {
            Gun modifiedGun = this.getModifiedGun(stack);
            return !modifiedGun.getGeneral().isAuto();
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return this.getItemStackLimit(stack) == 1;
    }

    @Override
    public int getItemEnchantability()
    {
        return 5;
    }*/
}
