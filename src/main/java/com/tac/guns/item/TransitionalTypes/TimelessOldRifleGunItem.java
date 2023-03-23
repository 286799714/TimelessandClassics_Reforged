package com.tac.guns.item.TransitionalTypes;


import com.tac.guns.GunMod;
import com.tac.guns.common.Gun;
import com.tac.guns.common.GunModifiers;
import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.Process;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;


import net.minecraft.item.Item.Properties;

public class TimelessOldRifleGunItem extends TimelessGunItem {
    public TimelessOldRifleGunItem(Process<Properties> properties, IGunModifier... modifiers)
    {
        super(properties1 -> properties.process(new Properties().stacksTo(1).tab(GunMod.GROUP)), modifiers);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Gun modifiedGun = this.getModifiedGun(stack);
        CompoundNBT tagCompound = stack.getTag();
        super.appendHoverText(stack, worldIn, tooltip, flag);
        if(tagCompound != null)
        {
            //tooltip.add((new TranslationTextComponent("info.tac.oldRifle", new TranslationTextComponent(IAttachment.Type.OLD_SCOPE.getTranslationKey())).mergeStyle(TextFormatting.GREEN)));
            tooltip.add((new TranslationTextComponent("info.tac.oldRifleScope", new TranslationTextComponent("OldScope").withStyle(TextFormatting.BOLD)).withStyle(TextFormatting.LIGHT_PURPLE)));
        }
    }

    public TimelessOldRifleGunItem(IGunModifier... modifiers) {
        this(properties -> properties, modifiers);
    }
}