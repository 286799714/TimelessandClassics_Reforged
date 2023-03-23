package com.tac.guns.item;

import com.tac.guns.common.Gun;
import com.tac.guns.item.attachment.IScope;
import com.tac.guns.item.attachment.impl.Scope;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

import com.tac.guns.item.attachment.IAttachment.Type;
import net.minecraft.item.Item.Properties;

/**
 * A basic scope attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class OldScopeItem extends ScopeItem
{
    public OldScopeItem(Scope scope, Properties properties)
    {
        super(scope,properties);
    }
    @Override
    public Type getType()
    {
        return Type.OLD_SCOPE;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, worldIn, tooltip, flag);
        tooltip.add((new TranslationTextComponent("info.tac.oldScope_type").withStyle(TextFormatting.LIGHT_PURPLE).withStyle(TextFormatting.BOLD)));
    }
}
