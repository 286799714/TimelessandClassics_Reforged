package com.tac.guns.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.client.animation.*;
import com.tac.guns.client.animation.gltf.AnimationStructure;
import com.tac.guns.client.event.BeforeRenderHandEvent;
import com.tac.guns.client.model.BedrockAnimatedModel;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.render.item.OverrideModelManager;
import com.tac.guns.client.resource.model.ModelLoader;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public enum AnimationHandler {
    INSTANCE;

    public static final Map<ResourceLocation, AnimationController> controllers = new HashMap<>();
    public static final int MAIN_TRACK = 0;
    private boolean lastTickSprint = false;
    private boolean lastTickWalk = false;
    //记录开始冲刺时玩家的walk distance，以便让冲刺动画有统一的开头
    private float baseDistanceWalked = 0.0f;
    //玩家以冲刺状态脱离地面时，需要保持walk distance不变以保持姿态。
    private float keepDistanceWalked = 0.0f;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event){
        if(!event.phase.equals(TickEvent.Phase.START))
            return;

        for(AnimationController controller : controllers.values()){
            ObjectAnimationRunner runner = controller.getAnimation(MAIN_TRACK);
            if (runner != null) {
                //为了让冲刺和行走动画和原版的viewBobbing相适应，需要手动更新冲刺动画的进度
                //当前动画是run或者正在过渡向run动画的时候，就手动设置run动画的进度。
                Entity entity = Minecraft.getInstance().getCameraEntity();
                if (entity instanceof Player playerEntity) {
                    float deltaDistanceWalked = playerEntity.walkDist - playerEntity.walkDistO;
                    float distanceWalked;
                    if(playerEntity.isOnGround())
                        distanceWalked = playerEntity.walkDist + deltaDistanceWalked * event.renderTickTime - baseDistanceWalked;
                    else {
                        distanceWalked = keepDistanceWalked;
                        baseDistanceWalked = playerEntity.walkDist + deltaDistanceWalked * event.renderTickTime - keepDistanceWalked;
                    }
                    keepDistanceWalked = distanceWalked;
                    if((runner.getAnimation().name.equals("run") || runner.getAnimation().name.contains("walk")) && runner.isRunning()) {
                        runner.setProgressNs((long) (runner.getAnimation().getMaxEndTimeS() * (distanceWalked % 2f) / 2f * 1e9f));
                    }
                    if(runner.isTransitioning() && runner.getTransitionTo()!= null &&
                            (runner.getTransitionTo().getAnimation().name.equals("run") || runner.getTransitionTo().getAnimation().name.contains("walk"))){
                        runner.getTransitionTo().setProgressNs((long) (runner.getTransitionTo().getAnimation().getMaxEndTimeS() * (distanceWalked % 2f) / 2f * 1e9f));
                    }
                }
            }
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

    //在ItemLayer应用反向的摄像机动画，让ItemLayer不要随着摄像机一起运动。
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
                            new ResourceLocation("tac", "models/gun/ak47.meta.json")
                    )
            );
            OverrideModelManager.register(
                    ModItems.SRO_DOT.get(),
                    ModelLoader.loadBedrockAttachmentModel(
                            new ResourceLocation("tac", "models/scope/sro_dot.geo.json"),
                            new ResourceLocation("tac", "textures/items/sro_dot_uv.png")
                    )
            );
        } catch (IOException e) {
            GunMod.LOGGER.info("test fail: {}", e.toString());
        }

        try {
            BedrockAnimatedModel model = (BedrockAnimatedModel) OverrideModelManager.getModel(ModItems.AK47.get());
            if(model == null) return;
            AnimationStructure structure =
                    AnimationResources.getInstance().loadAnimationStructure(new ResourceLocation("tac","animations/ak47.geo.gltf"));
            controllers.put(ModItems.AK47.get().getRegistryName(), new AnimationController(structure, model));
        } catch (IOException e) {
            GunMod.LOGGER.warn("testing fail!");
            throw new RuntimeException(e);
        }
    }

    public void onGunReload(boolean reloading, ItemStack itemStack) {
        AnimationController animationController = controllers.get(itemStack.getItem().getRegistryName());
        if(animationController != null)
            animationController.runAnimation(MAIN_TRACK, "reload_empty", ObjectAnimation.PlayType.PLAY_ONCE_HOLD,0.3f);
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Pre event) {
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        if(event.getStack().getItem() instanceof GunItem){
            AnimationController controller = controllers.get(event.getStack().getItem().getRegistryName());
            if(controller != null)
                controller.runAnimation(MAIN_TRACK, "shoot", ObjectAnimation.PlayType.PLAY_ONCE_HOLD, 0.05f);
        }
    }

    @SubscribeEvent
    public void onPumpShotgunFire(GunFireEvent.Post event) {
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        //todo
    }

    @SubscribeEvent
    public void onBoltActionRifleFire(GunFireEvent.Post event){
        if (!event.isClient()) return;
        if(Minecraft.getInstance().player == null) return;
        if(!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        //todo
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
                    controller.runAnimation(0, "inspect", ObjectAnimation.PlayType.PLAY_ONCE_HOLD,0.3f);
            }
        } );
    }

    @SubscribeEvent
    public void applyRunAnimation(TickEvent.ClientTickEvent event){
        if(Minecraft.getInstance().player == null) return;
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        if(Minecraft.getInstance().player.isSprinting() && Minecraft.getInstance().player.isOnGround() && !lastTickSprint) {
            lastTickSprint = true;
            lastTickWalk = false;
            AnimationController animationController = controllers.get(stack.getItem().getRegistryName());
            if (animationController != null) {
                ArrayDeque<AnimationController.AnimationPlan> deque = new ArrayDeque<>();
                deque.add(new AnimationController.AnimationPlan("run_start", ObjectAnimation.PlayType.PLAY_ONCE_HOLD, 0.2f));
                deque.add(new AnimationController.AnimationPlan("run", ObjectAnimation.PlayType.LOOP, 0.4f));
                animationController.queueAnimation(MAIN_TRACK, deque);
                baseDistanceWalked = Minecraft.getInstance().player.walkDist;
            }
        }
        if(!Minecraft.getInstance().player.isSprinting() && lastTickSprint) {
            lastTickSprint = false;
            AnimationController animationController = controllers.get(stack.getItem().getRegistryName());
            if (animationController != null) {
                animationController.runAnimation(MAIN_TRACK, "run_end", ObjectAnimation.PlayType.PLAY_ONCE_HOLD, 0.2f);
            }
        }
    }

    @SubscribeEvent
    public void applyWalkAnimation(TickEvent.ClientTickEvent event){
        if(Minecraft.getInstance().player == null) return;
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack stack = player.getMainHandItem();
        boolean isMoving = player.xOld != player.getX() || player.yOld != player.getY() || player.zOld != player.getZ();
        if(!player.isSprinting() && isMoving && !lastTickWalk){
            lastTickWalk = true;
        }
        if(!isMoving && lastTickWalk){

        }
    }
}
