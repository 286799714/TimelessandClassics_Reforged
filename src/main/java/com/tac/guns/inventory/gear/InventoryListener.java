package com.tac.guns.inventory.gear;

import com.mojang.logging.LogUtils;
import com.tac.guns.Reference;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.IAmmoItemHandler;
import com.tac.guns.item.transition.wearables.IArmoredRigItem;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.InvocationTargetException;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class InventoryListener {

    public static Capability<IWearableItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});;

    public static Capability<IAmmoItemHandler> RIG_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});;
    /*public static Method addSlotMethod;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJoin(EntityJoinWorldEvent event) throws InvocationTargetException, IllegalAccessException {
        if(!(event.getEntity() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getEntity();
        if(addSlotMethod == null) {
            addSlotMethod = ObfuscationReflectionHelper.findMethod(Container.class, "addSlot", Slot.class);
        }
    //    GearSlotsHandler wearableItemHandler = (GearSlotsHandler) player.getCapability(ITEM_HANDLER_CAPABILITY).resolve().get();
    //    addSlotMethod.invoke(player.container, new ArmorRigSlot(wearableItemHandler, 0, 170, 84)); // Rig
    //    addSlotMethod.invoke(player.container, new BackpackSlot(wearableItemHandler, 1, 170, 102)); // Backpack
    }*/

    /*@SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) throws InvocationTargetException, IllegalAccessException {
        if(!(event.getObject() instanceof PlayerEntity)) return;

        WearableCapabilityProvider wearableCapability = new WearableCapabilityProvider();
        event.addCapability(new ResourceLocation("tac", "inventory_capability"), wearableCapability);
        event.addListener(wearableCapability. getOptionalStorage()::invalidate);
    }*/

    @SubscribeEvent
    public static void onAttachCapabilitiesStack(AttachCapabilitiesEvent<ItemStack> event) throws InvocationTargetException, IllegalAccessException {
        if(!(event.getObject().getItem() instanceof IArmoredRigItem)) return;
        if(!event.getCapabilities().containsKey(new ResourceLocation("tac", "rig"))) {
            ArmorRigCapabilityProvider armorRigInventoryCapability = new ArmorRigCapabilityProvider();
            event.addCapability(new ResourceLocation("tac", "rig"), armorRigInventoryCapability);
            event.addListener(armorRigInventoryCapability.getOptionalStorage()::invalidate);
        }
    }
}
