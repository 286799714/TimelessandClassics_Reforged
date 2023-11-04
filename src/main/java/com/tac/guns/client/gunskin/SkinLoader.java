package com.tac.guns.client.gunskin;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.tac.guns.client.SpecialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.registries.RegistryObject;


import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinLoader {
    public final static Map<ResourceLocation, SkinLoader> skinLoaders = new HashMap<>();
    private DefaultSkin defaultSkin;
    public static UnbakedModel missingModel;
    public static Map<ResourceLocation, UnbakedModel> unbakedModels;
    public static Map<ResourceLocation, UnbakedModel> topUnbakedModels;
    private final List<ModelComponent> components;
    private final ResourceLocation name;
    public SkinLoader(ResourceLocation name, ModelComponent... components) {
        this.components = Arrays.asList(components);
        this.name = name;
    }

    public SkinLoader(RegistryObject<?> item, ModelComponent... components) {
        this(item.getId(), components);
    }

    public static void register(ResourceLocation name, SkinLoader loader){
        skinLoaders.put(name,loader);
    }

    public List<ModelComponent> getComponents() {
        return components;
    }
    public static SkinLoader getSkinLoader(String name) {
        ResourceLocation rl = null;
        if(name.indexOf(':')>=0){
                        rl = ResourceLocation.tryParse(name);
        }else{
            rl = ResourceLocation.tryParse("tac:"+name);
        }
        if(rl==null)return null;
        return skinLoaders.get(rl);
    }

    public static SkinLoader getSkinLoader(ResourceLocation name) {
        return skinLoaders.get(name);
    }

    public ResourceLocation getGun() {
        return name;
    }

    public DefaultSkin loadDefaultSkin() {
        DefaultSkin skin = new DefaultSkin(this.name);
        String mainLoc = this.name.getNamespace()+ ":special/" + getGun().getPath();
        for (ModelComponent key : this.components) {
            tryLoadComponent(skin, mainLoc, key);
        }
        this.defaultSkin = skin;

        ResourceLocation iconLoc = ResourceLocation.tryParse(this.name.getNamespace()+":textures/gui/icon/"+this.name.getPath()+".png");
        if(iconLoc!=null && Minecraft.getInstance().getResourceManager().hasResource(iconLoc)){
            skin.setIcon(iconLoc);
        }

        ResourceLocation miniIconLoc = ResourceLocation.tryParse(this.name.getNamespace()+":textures/gui/icon/mini/"+this.name.getPath()+".png");
        if(iconLoc!=null && Minecraft.getInstance().getResourceManager().hasResource(iconLoc)){
            skin.setMiniIcon(miniIconLoc);
        }

        return skin;
    }

    /**
     * Should be called during model loading.<br>
     * Try to load a gun skin with unique models, then add them into bake queue.<br>
     * If there exist the key 'auto', it will attempt to load all model components that comply with the default naming format.
     *
     * @return the skin, or return null if the default skin is null.
     */
    public GunSkin loadCustomSkin(ResourceLocation skinName, Map<String, String> models) {
        if (defaultSkin == null) return null;

        GunSkin skin = new GunSkin(skinName, getGun());
        skin.setDefaultSkin(this.defaultSkin);

        if (models.containsKey("auto")) {
            String main = models.get("auto");
            for (ModelComponent key : this.components) {
                tryLoadComponent(skin, main, key);
            }
        } else {
            for (ModelComponent key : this.components) {
                tryLoadComponent(skin, models, key);
            }
        }
        return skin;
    }

    public boolean loadSkinIcon(@Nonnull GunSkin skin, @Nonnull ResourceLocation iconLocation){
        if(Minecraft.getInstance().getResourceManager().hasResource(iconLocation)){
            skin.setIcon(iconLocation);
            return true;
        }else{
            ResourceLocation tl = ResourceLocation.tryParse(iconLocation.getNamespace()+":textures/"+iconLocation.getPath()+".png");
            if(tl !=null && Minecraft.getInstance().getResourceManager().hasResource(tl)){
                skin.setIcon(tl);
                return true;
            }else {
                return false;
            }
        }
    }

    public boolean loadSkinMiniIcon(@Nonnull GunSkin skin, @Nonnull ResourceLocation iconLocation){
        if(Minecraft.getInstance().getResourceManager().hasResource(iconLocation)){
            skin.setIcon(iconLocation);
            return true;
        }else{
            ResourceLocation tl = ResourceLocation.tryParse(iconLocation.getNamespace()+":textures/"+iconLocation.getPath()+".png");
            if(tl !=null && Minecraft.getInstance().getResourceManager().hasResource(tl)){
                skin.setMiniIcon(tl);
                return true;
            }else {
                return false;
            }
        }
    }

    private static void tryLoadComponent(GunSkin skin, Map<String, String> models, ModelComponent component) {
        if (models.containsKey(component.key)) {
            ResourceLocation loc = ResourceLocation.tryParse(models.get(component.key));
            if (loc != null) {
                SpecialModel mainModel = new SpecialModel(loc);
                ForgeModelBakery.addSpecialModel(loc);
                skin.addComponent(component, mainModel);
            }
        }
    }

    private static void tryLoadComponent(GunSkin skin, String mainLocation, ModelComponent component) {
        ResourceLocation loc = component.getModelLocation(mainLocation);
        if (loc != null) {
            ResourceLocation test = new ResourceLocation(loc.getNamespace(), "models/" + loc.getPath() + ".json");
            if (Minecraft.getInstance().getResourceManager().hasResource(test)) {
                SpecialModel mainModel = new SpecialModel(loc);
                ForgeModelBakery.addSpecialModel(loc);
                skin.addComponent(component, mainModel);
            }
        }
    }

    /**
     * Should be called during model loading.<br>
     * Try to load a gun skin without unique models from given textures, then add them into bake queue.
     *
     * @return the skin, or return null if the default skin is null.
     * @see net.minecraftforge.client.model.ForgeModelBakery
     */
    public GunSkin loadTextureOnlySkin(ResourceLocation skinName, List<Pair<String, ResourceLocation>> textures) {
        if (defaultSkin == null) return null;

        GunSkin skin = new GunSkin(skinName, this.getGun());
        skin.setDefaultSkin(this.defaultSkin);
        //create unbaked models for every component of this gun.
        for (ModelComponent component : this.components) {
            ResourceLocation parent = component.getModelLocation(this.name.getNamespace()+ ":special/" + this.name.getPath());
            TextureModel model = TextureModel.tryCreateCopy(parent);
            if (model != null) {
                model.applyTextures(textures);
                ResourceLocation componentLoc = component.getModelLocation(skinName.getNamespace()+
                        ":gunskin/generated/"+this.name.getNamespace()+this.name.getPath()+"_"+skinName.getPath());
                skin.addComponent(component, new SpecialModel(componentLoc));

                unbakedModels.put(componentLoc, model.getModel());
                topUnbakedModels.put(componentLoc, model.getModel());
            }
        }
        return skin;
    }
    public static class TextureModel {
        public static final ResourceLocation atlasLocation = new ResourceLocation("minecraft:textures/atlas/blocks.png");
        private final BlockModel unbaked;

        private TextureModel(BlockModel model) {
            this.unbaked = model;
        }

        public static TextureModel tryCreateCopy(ResourceLocation parentLocation) {
            TextureModel textureModel = null;
            if (ForgeModelBakery.instance() != null) {
                BlockModel parent = (BlockModel) ForgeModelBakery.instance().getModel(parentLocation);

                if (parent == missingModel) return null;

                List<BlockElement> list = Lists.newArrayList();
                Map<String, Either<Material, String>> map = Maps.newHashMap();

                BlockModel model = new BlockModel(parentLocation, list, map,
                        true, null, parent.getTransforms(), parent.getOverrides());

                textureModel = new TextureModel(model);
            }
            return textureModel;
        }

        public void applyTextures(List<Pair<String, ResourceLocation>> textures) {
            textures.forEach((p) -> applyTexture(p.getFirst(), p.getSecond()));
        }

        public void applyTexture(String key, ResourceLocation textureLocation) {
            this.unbaked.textureMap.put(key, Either.left(new Material(atlasLocation, textureLocation)));
        }

        public BlockModel getModel() {
            return unbaked;
        }
    }
}
