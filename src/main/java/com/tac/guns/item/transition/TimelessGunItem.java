package com.tac.guns.item.transition;


import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.common.Gun;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.Process;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class TimelessGunItem extends GunItem {
    private final IGunModifier[] modifiers;
    private Boolean integratedOptic = false;
    public TimelessGunItem(Process<Item.Properties> properties, IGunModifier... modifiers) {
        super(properties.process(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
        this.modifiers = modifiers;
    }

    public TimelessGunItem(Process<Item.Properties> properties, Boolean integratedOptic, IGunModifier... modifiers) {
        super(properties.process(new Item.Properties().stacksTo(1).tab(GunMod.GROUP)));
        this.modifiers = modifiers;
        this.integratedOptic = integratedOptic;
    }

    public TimelessGunItem() {
        this(properties -> properties);
    }

    public Boolean isIntegratedOptic() {
        return integratedOptic;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        Gun modifiedGun = this.getModifiedGun(stack);
        Item ammo = ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
        if (ammo != null) {
            tooltip.add((new TranslatableComponent("info.tac.ammo_type", new TranslatableComponent(ammo.getDescriptionId()).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.DARK_GRAY)));
        }

        String additionalDamageText = "";
        CompoundTag tagCompound = stack.getTag();
        float additionalDamage;
        if (tagCompound != null && tagCompound.contains("AdditionalDamage", 99)) {
            additionalDamage = tagCompound.getFloat("AdditionalDamage");
            additionalDamage += GunModifierHelper.getAdditionalDamage(stack);
            if (additionalDamage > 0.0F) {
                additionalDamageText = ChatFormatting.GREEN + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
            } else if (additionalDamage < 0.0F) {
                additionalDamageText = ChatFormatting.RED + " " + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
            }
        }

        additionalDamage = modifiedGun.getProjectile().getDamage();
        additionalDamage = GunModifierHelper.getModifiedProjectileDamage(stack, additionalDamage);
        additionalDamage = GunEnchantmentHelper.getAcceleratorDamage(stack, additionalDamage);
        tooltip.add((new TranslatableComponent("info.tac.damage", ChatFormatting.GOLD + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage) + additionalDamageText)).withStyle(ChatFormatting.DARK_GRAY));
        if (tagCompound != null) {
            if (tagCompound.getBoolean("IgnoreAmmo")) {
                tooltip.add((new TranslatableComponent("info.tac.ignore_ammo")).withStyle(ChatFormatting.AQUA));
            } else {
                int ammoCount = tagCompound.getInt("AmmoCount");
                tooltip.add((new TranslatableComponent("info.tac.ammo", ChatFormatting.GOLD.toString() + ammoCount + "/" + GunModifierHelper.getAmmoCapacity(stack, modifiedGun))).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        boolean isShift = Keys.MORE_INFO_HOLD.isDown();
        if (!isShift) {
            //String text = "SHIFT";
            //if(!InputHandler.MORE_INFO_HOLD.keyCode().equals(GLFW.GLFW_KEY_LEFT_SHIFT))
            String text = (new KeybindComponent("key.tac.more_info_hold")).getString().toUpperCase(Locale.ENGLISH);
            tooltip.add((new TranslatableComponent("info.tac.more_info_gunitem", text)).withStyle(ChatFormatting.YELLOW));
        }
        if (isShift) {
            GunItem gun = (GunItem) stack.getItem();
            if (tagCompound != null) {
                double armorPen = gun.getGun().getProjectile().getGunArmorIgnore() >= 0 ?
                        Math.min((Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * gun.getGun().getProjectile().getGunArmorIgnore() * 100), 100F) : 0F;
                tooltip.add((new TranslatableComponent("info.tac.armorPen", new TranslatableComponent(String.format("%.1f", armorPen) + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_AQUA)));

                int headDamgeModifier = Config.COMMON.gameplay.headShotDamageMultiplier.get() * gun.getGun().getProjectile().getGunHeadDamage() >= 0 ?
                        (int) (Config.COMMON.gameplay.headShotDamageMultiplier.get() * gun.getGun().getProjectile().getGunHeadDamage() * 100) : 0;
                tooltip.add((new TranslatableComponent("info.tac.headDamageModifier", new TranslatableComponent(String.format("%d", headDamgeModifier) + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_AQUA)));

                float speed = ServerPlayHandler.calceldGunWeightSpeed(gun.getGun(), stack);
                speed = Math.max(Math.min(speed, 0.1F), 0.075F);
                if (speed > 0.094f)
                    tooltip.add((new TranslatableComponent("info.tac.lightWeightGun", new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_AQUA)));
                else if (speed < 0.095 && speed > 0.0875)
                    tooltip.add((new TranslatableComponent("info.tac.standardWeightGun", new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_GREEN)));
                else
                    tooltip.add((new TranslatableComponent("info.tac.heavyWeightGun", new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_RED)));

                float percentageToNextLevel = (tagCompound.getFloat("levelDmg") * 100) / (modifiedGun.getGeneral().getLevelReq() * (((tagCompound.getInt("level")) * 3.0f)));
                tooltip.add((new TranslatableComponent("info.tac.current_level").append(new TranslatableComponent(" " + tagCompound.getInt("level") + " : " + String.format("%.2f", percentageToNextLevel) + "%")))
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
            }

            tooltip.add((new TranslatableComponent("info.tac.attachment_help", Keys.ATTACHMENTS.getKey().getDisplayName().getContents().toUpperCase(Locale.ENGLISH))).withStyle(ChatFormatting.YELLOW));
            if (gun.getGun().canAttachType(IAttachment.Type.PISTOL_SCOPE))
                tooltip.add((new TranslatableComponent("info.tac.pistolScope", new TranslatableComponent("MiniScope").withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.LIGHT_PURPLE)));
            if (gun.getGun().canAttachType(IAttachment.Type.IR_DEVICE))
                tooltip.add((new TranslatableComponent("info.tac.irLaserEquip", new TranslatableComponent("IrLaser").withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.AQUA)));
        }
    }

    public IGunModifier[] getModifiers() {
        return this.modifiers;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return Objects.requireNonNull(ChatFormatting.GOLD.getColor());
    }


    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (Config.CLIENT.display.weaponAmmoBar.get()) {
            CompoundTag tagCompound = stack.getOrCreateTag();
            Gun modifiedGun = this.getModifiedGun(stack);
            return !tagCompound.getBoolean("IgnoreAmmo") && tagCompound.getInt("AmmoCount") != GunModifierHelper.getAmmoCapacity(stack, modifiedGun);
        } else
            return false;
    }

    @Override
    public boolean isFoil(ItemStack gunItem) {
        return false;
    }
}