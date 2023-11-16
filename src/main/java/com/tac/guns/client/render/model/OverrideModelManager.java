package com.tac.guns.client.render.model;

import com.tac.guns.Reference;
import com.tac.guns.item.GunItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class OverrideModelManager
{
    private static final Map<Item, IOverrideModel> MODEL_MAP = new HashMap<>();

    /**
     * Registers an override model to the given item.
     * OverrideModels will not affect the common rendering of items.
     * Only when rendering guns and their attachments, {@link com.tac.guns.client.handler.GunRenderingHandler} will call them at the appropriate time.
     * @param item  the item to override it's model.
     *              It should be an item related to guns. It may be the gun item itself, or it may be an attachment such as a scope.
     * @param model an IOverrideModel implementation
     */
    public static void register(Item item, IOverrideModel model)
    {
        if(MODEL_MAP.putIfAbsent(item, model) == null)
        {
            /* Register model overrides as an event for ease. Doesn't create an extra overhead because
             * Forge will just ignore it if it contains no events */
            MinecraftForge.EVENT_BUS.register(model);
        }
    }

    /**
     * Checks if the given ItemStack has an overridden model
     *
     * @param stack the stack to check
     * @return True if overridden model exists
     */
    public static boolean hasModel(ItemStack stack)
    {
        return MODEL_MAP.containsKey(stack.getItem());
    }

    /**
     * Gets the overridden model for the given ItemStack.
     *
     * @return The overridden model for the stack or null if no overridden model exists.
     */
    @Nullable
    public static IOverrideModel getModel(ItemStack stack)
    {
        return MODEL_MAP.get(stack.getItem());
    }
}
