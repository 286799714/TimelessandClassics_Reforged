package com.tac.guns.client.render.gun;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import static com.tac.guns.client.render.gun.CommonComponents.BODY;

public class GunModelComponent {
    //special
    public final String key;
    GunModelComponent(String key){
        this.key = key;
    }
    /**
     * @return The default model location of the component according to the main component
     * */
    @Nullable
    public static ResourceLocation getModelLocation(GunModelComponent component, String mainLocation){
        return component.getModelLocation(mainLocation);
    }
    @Nullable
    public static ResourceLocation getModelLocation(GunModelComponent component, ResourceLocation mainLocation){
        return component.getModelLocation(mainLocation);
    }
    @Nullable
    public ResourceLocation getModelLocation(String mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this==BODY ? "" : "_" + this.key));
    }
    @Nullable
    public ResourceLocation getModelLocation(ResourceLocation mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this==BODY ? "" : "_" + this.key));
    }
}
