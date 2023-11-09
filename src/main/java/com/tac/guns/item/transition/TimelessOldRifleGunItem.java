package com.tac.guns.item.transition;


import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.common.Gun;
import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.util.Process;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;


public class TimelessOldRifleGunItem extends TimelessGunItem {
    public TimelessOldRifleGunItem(Process<Properties> properties, IGunModifier... modifiers)
    {
        super(properties1 -> properties.process(new Properties().stacksTo(1).tab(GunMod.GROUP)), modifiers);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        Gun modifiedGun = this.getModifiedGun(stack);
        CompoundTag tagCompound = stack.getTag();
        super.appendHoverText(stack, worldIn, tooltip, flag);
        boolean isShift = Keys.MORE_INFO_HOLD.isDown();
        if (isShift) {
            if (tagCompound != null) {
                //tooltip.add((new TranslatableComponent("info.tac.oldRifle", new TranslatableComponent(IAttachment.Type.OLD_SCOPE.getTranslationKey())).withStyle(ChatFormatting.GREEN)));
                tooltip.add((new TranslatableComponent("info.tac.oldRifleScope", new TranslatableComponent("OldScope").withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.LIGHT_PURPLE)));
            }
        }
    }

    public TimelessOldRifleGunItem(IGunModifier... modifiers) {
        this(properties -> properties, modifiers);
    }
}