package com.tac.guns.client.render.model;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is used to identify and reorganize guns' component models.
 *
 * "key" is used to identify a component model. It appears in the file name in suffix form.
 * For example, ak47_stock_light.json is a model file that contain the light stock of the ak47. Its corresponding component key is "stock_light".
 *
 * "namespace" is used to distinguish GunComponents under different organizations.
 * If two GunComponent with the same key have different namespaces, they are not regarded as the same.
 * */
public class GunComponent implements Comparable<GunComponent>{
    private static final HashMap<String, HashMap<String, GunComponent>> componentMap = new HashMap<>(); // namespace -> (key -> GunComponent)

    public final String key;
    public final String namespace;

    public GunComponent(@Nullable String key){
        this(null, key);
    }

    public GunComponent(@Nullable String namespace, @Nullable String key){
        this.key = key;
        this.namespace = namespace;
    }

    public void registerThis(){
        componentMap.compute(namespace, (k, map)->{
            if(map == null){
                map = new HashMap<>();
            }
            map.put(key, this);
            return map;
        });
    }

    public static void register(GunComponent component){
        component.registerThis();
    }

    public static GunComponent getComponent(@Nullable String namespace, @Nullable String key){
        Map<String, GunComponent> map = componentMap.get(namespace);
        if(map != null){
            return map.get(key);
        }
        return null;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof GunComponent component){
            return
                    Objects.equals(this.key, component.key) &&
                    Objects.equals(this.namespace, component.namespace);
        }
        else
            return false;
    }

    @Override
    public int hashCode(){
        return (namespace == null ? 0 : namespace.hashCode()) * 31 + (key == null ? 0 : key.hashCode());
    }

    @Override
    public int compareTo(@Nonnull GunComponent o) {
        int r = Objects.compare(namespace, o.namespace, String::compareTo);
        if(r != 0) return r;
        return Objects.compare(key, o.key, String::compareTo);
    }

    @Nullable
    public ResourceLocation getModelLocation(String mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this.key == null ? "" : "_" + this.key));
    }
    @Nullable
    public ResourceLocation getModelLocation(ResourceLocation mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this.key == null ? "" : "_" + this.key));
    }
}
