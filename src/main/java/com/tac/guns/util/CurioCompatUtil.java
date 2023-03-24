package com.tac.guns.util;

import com.tac.guns.item.TransitionalTypes.wearables.CurioCapabilityProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosCapability;

public class CurioCompatUtil {
    public static<T> T getCurioCapability(@NotNull Capability<T> cap, ItemStack stack){
        if(cap == CuriosCapability.ITEM){
            return (T) new CurioCapabilityProvider(stack);
        }
        else{
            return null;
        }
    }
}
