package com.tac.guns.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.client.animation.*;
import com.tac.guns.client.animation.gltf.AnimationStructure;
import com.tac.guns.client.animation.module.*;
import com.tac.guns.client.event.BeforeRenderHandEvent;
import com.tac.guns.client.model.BedrockAnimatedModel;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.render.item.OverrideModelManager;
import com.tac.guns.client.resource.model.ModelLoader;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.event.GunReloadEvent;
import com.tac.guns.init.ModItems;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunModifierHelper;
import de.javagl.jgltf.model.animation.AnimationRunner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.*;

/**
 * Mainly controls when the animation should play.
 */
@OnlyIn(Dist.CLIENT)
public enum AnimationHandler {
    INSTANCE;

    public static final Map<ResourceLocation, AnimationController> controllers = new HashMap<>();

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event){
        for(AnimationController controller : controllers.values()){
            controller.update();
        }
    }

    @SubscribeEvent
    public void applyCameraAnimation(EntityViewRenderEvent.CameraSetup event){
        if(Minecraft.getInstance().player == null) return;
        if(!Minecraft.getInstance().options.bobView) return;
        //apply BedrockAnimatedModel's camera animation transform
        IOverrideModel model = OverrideModelManager.getModel(Minecraft.getInstance().player.getMainHandItem().getItem());
        if(model instanceof BedrockAnimatedModel bedrockAnimatedModel){
            Quaternion q = bedrockAnimatedModel.getCameraAnimationObject().rotationQuaternion;
            double yaw = Math.asin(2 * (q.r() * q.j() - q.i() * q.k()));
            double pitch = Math.atan2(2 * (q.r() * q.i() + q.j() * q.k()), 1 - 2 * (q.i() * q.i() + q.j() * q.j()));
            double roll = Math.atan2(2 * (q.r() * q.k() + q.i() * q.j()), 1 - 2 * (q.j() * q.j() + q.k() * q.k()));
            yaw = Math.toDegrees(yaw);
            pitch = Math.toDegrees(pitch);
            roll = Math.toDegrees(roll);
            event.setYaw((float) yaw + event.getYaw());
            event.setPitch((float) pitch + event.getPitch());
            event.setRoll((float) roll + event.getRoll());
        }
    }

    @SubscribeEvent
    public void applyItemLayerCameraAnimation(BeforeRenderHandEvent event){
        if(!Minecraft.getInstance().options.bobView) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        IOverrideModel overrideModel = OverrideModelManager.getModel(player.getMainHandItem());
        if(overrideModel == null) return;
        if(overrideModel instanceof BedrockAnimatedModel animatedModel){
            PoseStack poseStack = event.getPoseStack();
            poseStack.mulPose(animatedModel.getCameraAnimationObject().rotationQuaternion);
        }
    }

    public static void preloadAnimations(){

        try {
            OverrideModelManager.register(
                    ModItems.AK47.get(),
                    ModelLoader.loadBedrockGunModel(
                            new ResourceLocation("tac", "models/gunskin/ak47/ak47.geo.json"),
                            new ResourceLocation("tac", "textures/items/ak47_uv.png")
                    )
            );
            OverrideModelManager.register(
                    ModItems.DEAGLE_357.get(),
                    ModelLoader.loadBedrockGunModel(
                            new ResourceLocation("tac", "models/gunskin/deagle50/deagle_50_cc.geo.json"),
                            new ResourceLocation("tac", "textures/items/deagle_50_cc.png")
                    )
            );
        } catch (IOException e) {
            GunMod.LOGGER.info("test fail: {}", e.toString());
        }

        try {
            BedrockAnimatedModel model = (BedrockAnimatedModel) OverrideModelManager.getModel(ModItems.AK47.get());
            if(model == null) return;
            model.setFunctionalRenderer("LeftHand", bedrockPart -> (poseStack, transformType, consumer, light, overlay) -> {
                if(transformType.firstPerson()){
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
                    Matrix3f normal = poseStack.last().normal().copy();
                    Matrix4f pose = poseStack.last().pose().copy();
                    //需要把手臂的渲染委托到枪械模型渲染结束，因为它和枪械模型共用缓冲区，渲染手臂之后枪械模型的贴图会错误
                    model.delegateRender((poseStack1, transformType1, consumer1, light1, overlay1) -> {
                        PoseStack poseStack2 = new PoseStack();
                        poseStack2.last().normal().mul(normal);
                        poseStack2.last().pose().multiply(pose);
                        RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.LEFT, poseStack2, Minecraft.getInstance().renderBuffers().bufferSource(), light1);
                    });
                }
            });
            model.setFunctionalRenderer("RightHand", bedrockPart -> (poseStack, transformType, consumer, light, overlay) -> {
                if(transformType.firstPerson()){
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
                    Matrix3f normal = poseStack.last().normal().copy();
                    Matrix4f pose = poseStack.last().pose().copy();
                    //需要把手臂的渲染委托到枪械模型渲染结束，因为它和枪械模型共用缓冲区，渲染手臂之后枪械模型的贴图会错误
                    model.delegateRender((poseStack1, transformType1, consumer1, light1, overlay1) -> {
                        PoseStack poseStack2 = new PoseStack();
                        poseStack2.last().normal().mul(normal);
                        poseStack2.last().pose().multiply(pose);
                        RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.RIGHT, poseStack2, Minecraft.getInstance().renderBuffers().bufferSource(), light1);
                    });
                }
            });

            //create animation runner
            AnimationStructure structure =
                    AnimationResources.getInstance().loadAnimationStructure(new ResourceLocation("tac","animations/ak47.geo.gltf"));
            controllers.put(ModItems.AK47.get().getRegistryName(), new AnimationController(structure, model));
            controllers.get(ModItems.AK47.get().getRegistryName()).refreshPrototypes();
            for(ObjectAnimation animation : controllers.get(ModItems.AK47.get().getRegistryName()).getPrototypes()){
                Collection<List<ObjectAnimationChannel>> channels = animation.getChannels().values();
                for(List<ObjectAnimationChannel> list : channels){
                    for(ObjectAnimationChannel channel : list) {
                        GunMod.LOGGER.info("testing " + channel.node + " " + channel.type);
                        for (int i = 0; i < channel.content.keyframeTimeS.length; i++) {
                            StringBuilder str = new StringBuilder();
                            str.append(channel.content.keyframeTimeS[i]).append(":");
                            for (int j = 0; j < channel.content.values[i].length; j++) {
                                str.append(channel.content.values[i][j]).append(",");
                            }
                            GunMod.LOGGER.info(str.toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            GunMod.LOGGER.warn("testing fail!");
            throw new RuntimeException(e);
        }
        /*
        //TODO: Make automatic or have some sort of check for this
        AA12AnimationController.getInstance();
        Dp28AnimationController.getInstance();
        Glock17AnimationController.getInstance();
        HkMp5a5AnimationController.getInstance();
        HK416A5AnimationController.getInstance();
        M870AnimationController.getInstance();
        Mp7AnimationController.getInstance();
        Type81AnimationController.getInstance();
        Ak47AnimationController.getInstance();
        AWPAnimationController.getInstance();
        M60AnimationController.getInstance();
        M1014AnimationController.getInstance();
        TtiG34AnimationController.getInstance();
        MK18MOD1AnimationController.getInstance();
        M4AnimationController.getInstance();
        STI2011AnimationController.getInstance();
        M1911AnimationController.getInstance();
        MK47AnimationController.getInstance();
        MK14AnimationController.getInstance();
        SCAR_HAnimationController.getInstance();
        SCAR_LAnimationController.getInstance();
        CZ75AnimationController.getInstance();
        CZ75AutoAnimationController.getInstance();
        DBShotgunAnimationController.getInstance();
        FNFALAnimationController.getInstance();
        M16A4AnimationController.getInstance();
        SPR15AnimationController.getInstance();
        Deagle50AnimationController.getInstance();
        Type95LAnimationController.getInstance();
        Type191AnimationController.getInstance();
        MAC10AnimationController.getInstance();
        Vector45AnimationController.getInstance();
        SKSTacticalAnimationController.getInstance();
        M24AnimationController.getInstance();
        M82A2AnimationController.getInstance();
        //TODO: RPK redo due to static animation issue
        RPKAnimationController.getInstance();
        M249AnimationController.getInstance();
        M1A1AnimationController.getInstance();
        Glock18AnimationController.getInstance();
        SIGMCXAnimationController.getInstance();
        M92FSAnimationController.getInstance();
        MP9AnimationController.getInstance();
        MK23AnimationController.getInstance();
        RPG7AnimationController.getInstance();
        UDP9AnimationController.getInstance();
        COLTPYTHONAnimationController.getInstance();
        HK_G3AnimationController.getInstance();
        MRADAnimationController.getInstance();
        P90AnimationController.getInstance();
        SCAR_MK20AnimationController.getInstance();
        TEC9AnimationController.getInstance();
        Timeless50AnimationController.getInstance();
        UZIAnimationController.getInstance();

         */
    }

    public void onGunReload(boolean reloading, ItemStack itemStack) {
        AnimationController animationController = controllers.get(itemStack.getItem().getRegistryName());
        if(animationController != null)
            animationController.runAnimation(0, "reload_empty", 0.3f);
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (itemStack.getItem() instanceof GunItem) {
            GunItem gunItem = (GunItem) itemStack.getItem();
            CompoundTag tag = itemStack.getOrCreateTag();
            int reloadingAmount = GunModifierHelper.getAmmoCapacity(itemStack, gunItem.getGun()) - tag.getInt("AmmoCount");
            if (reloadingAmount <= 0) return;
        }
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if (controller == null) return;
        if(!reloading)
            return;
        AnimationMeta reloadEmptyMeta = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
        AnimationMeta reloadNormalMeta = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL);
        if (Gun.hasAmmo(itemStack)) {
            if (controller.getPreviousAnimation() != null && !controller.getPreviousAnimation().equals(reloadNormalMeta))
                controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL);
        } else {
            if (controller.getPreviousAnimation() != null && !controller.getPreviousAnimation().equals(reloadEmptyMeta))
                controller.stopAnimation();

            if(GunAnimationController.fromItem(itemStack.getItem()) instanceof PumpShotgunAnimationController)
                ((PumpShotgunAnimationController) GunAnimationController.fromItem(itemStack.getItem())).setEmpty(true);

            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
        }
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Pre event) {
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        if(event.getStack().getItem() instanceof GunItem){
            AnimationController controller = controllers.get(event.getStack().getItem().getRegistryName());
            if(controller != null)
                controller.runAnimation(0, "shoot", 0.01f);
        }
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller == null) return;
        if (controller.isAnimationRunning()) {
            AnimationMeta meta = controller.getPreviousAnimation();
            if(meta == null) return;
            if (meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT)) || meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY)))
                controller.stopAnimation();
            else {
                AnimationRunner runner = Animations.getAnimationRunner(meta.getResourceLocation());
                if(runner == null) return;
                float current = runner.getAnimationManager().getCurrentTimeS();
                float max = runner.getAnimationManager().getMaxEndTimeS();
                if(!(meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PUMP)) ||
                        meta.equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PULL_BOLT))))
                    if(max - current <= 0.25f) return;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPumpShotgunFire(GunFireEvent.Post event) {
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PUMP);
        }
    }

    @SubscribeEvent
    public void onBoltActionRifleFire(GunFireEvent.Post event){
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof BoltActionAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PULL_BOLT);
        }
    }
    
    static
    {
        Keys.INSPECT.addPressCallback( () -> {
            if (!Keys.noConflict(Keys.INSPECT))
                return;

            final Player player = Minecraft.getInstance().player;
            if( player == null ) return;

            if(AimingHandler.get().getNormalisedAdsProgress() != 0)
                return;

            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.getItem() instanceof GunItem){
                AnimationController controller = controllers.get(itemStack.getItem().getRegistryName());
                if(controller != null)
                    controller.runAnimation(0, "inspect", 0.3f);
            }
        } );
    }
    
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
    }

    @SubscribeEvent
    public void onClientPlayerReload(GunReloadEvent.Pre event){
        if(event.isClient()){
            GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
            if(controller != null){
                if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event){
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack itemStack = player.getInventory().getSelected();
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if(controller == null) return;
        if(controller.isAnimationRunning()){

        }
    }

    public boolean isReloadingIntro(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return false;
        return controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO);
    }

    public void onReloadLoop(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        controller.stopAnimation();
        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP);
    }

    public void onReloadEnd(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        if(controller instanceof PumpShotgunAnimationController ) {
            if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) != null) {
                if(SyncedEntityData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.STOP_ANIMA))
                    controller.stopAnimation();
                //controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
            }
        }else{
            if(SyncedEntityData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.STOP_ANIMA)) {
                controller.stopAnimation();
                controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
                controller.stopAnimation();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(Minecraft.getInstance().player == null) return;
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        GunAnimationController controller = GunAnimationController.fromItem(stack.getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            if(controller.getPreviousAnimation() != null && controller.getPreviousAnimation().equals(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_LOOP)) && !ReloadHandler.get().isReloading()){
                if(!controller.isAnimationRunning()){
                    if(((PumpShotgunAnimationController) controller).isEmpty()) {
                        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_EMPTY_END);
                        ((PumpShotgunAnimationController) controller).setEmpty(false);
                    }
                    else
                        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
                }
            }
        }
    }

    /*@SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
    }

    @SubscribeEvent
    public void onClientPlayerReload(GunReloadEvent.Pre event){
        if(event.isClient()){
            GunAnimationController controller = GunAnimationController.fromItem(event.getStack().getItem());
            if(controller != null){
                if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW) ||
                        controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack itemStack = player.getInventory().getCurrentItem();
        GunAnimationController controller = GunAnimationController.fromItem(itemStack.getItem());
        if(controller == null) return;
        if(controller.isAnimationRunning()){

        }
    }

    public boolean isReloadingIntro(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return false;
        return controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO);
    }

    public void onReloadLoop(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        controller.stopAnimation();
        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP);
    }

    public void onReloadEnd(Item item){
        GunAnimationController controller = GunAnimationController.fromItem(item);
        if(controller == null) return;
        if(controller instanceof PumpShotgunAnimationController ) {
            if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) != null) {
                controller.stopAnimation();
                controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
            }
        }else{
            controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
            controller.stopAnimation();
        }
    }*/
}
