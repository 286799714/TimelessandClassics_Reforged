package com.tac.guns.client;

import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.client.handler.*;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.armor.models.MediumArmor;
import com.tac.guns.client.render.armor.models.ModernArmor;
import com.tac.guns.client.render.armor.vestlayer.VestLayerRender;
import com.tac.guns.client.render.entity.GrenadeRenderer;
import com.tac.guns.client.render.entity.MissileRenderer;
import com.tac.guns.client.render.entity.ProjectileRenderer;
import com.tac.guns.client.render.entity.ThrowableGrenadeRenderer;
import com.tac.guns.client.render.model.scope.*;
import com.tac.guns.client.render.model.OverrideModelManager;
import com.tac.guns.client.screen.*;
import com.tac.guns.client.screen.AmmoScreen;
import com.tac.guns.client.settings.GunOptions;
import com.tac.guns.init.ModBlocks;
import com.tac.guns.init.ModContainers;
import com.tac.guns.init.ModEntities;
import com.tac.guns.init.ModItems;
import com.tac.guns.inventory.gear.armor.implementations.*;
import com.tac.guns.item.IColored;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageAttachments;
import com.tac.guns.network.message.MessageInspection;
import com.tac.guns.util.IDLNBTUtil;
import com.tac.guns.util.math.SecondOrderDynamics;
import de.javagl.jgltf.model.animation.AnimationRunner;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {
    private static Field mouseOptionsField;

    public static void setup(Minecraft mc) {
        MinecraftForge.EVENT_BUS.register(AimingHandler.get());
        MinecraftForge.EVENT_BUS.register(BulletTrailRenderingHandler.get());
        MinecraftForge.EVENT_BUS.register(CrosshairHandler.get());
        MinecraftForge.EVENT_BUS.register(GunRenderingHandler.get());
        MinecraftForge.EVENT_BUS.register(RecoilHandler.get());
        MinecraftForge.EVENT_BUS.register(ReloadHandler.get());
        MinecraftForge.EVENT_BUS.register(ShootingHandler.get());
        registerEntityRenders();
        MinecraftForge.EVENT_BUS.register(SoundHandler.get());
        MinecraftForge.EVENT_BUS.register(HUDRenderingHandler.get());
        MinecraftForge.EVENT_BUS.register(FireModeSwitchEvent.get()); // Technically now a handler but, yes I need some naming reworks
        MinecraftForge.EVENT_BUS.register(SightSwitchEvent.get()); // Still, as well an event, am uncertain on what to name it, in short handles upcoming advanced iron sights
        MinecraftForge.EVENT_BUS.register(ArmorInteractionHandler.get());

        //MinecraftForge.EVENT_BUS.register(FlashlightHandler.get()); // Completely broken... Needs a full rework
        //MinecraftForge.EVENT_BUS.register(FloodLightSource.get());

        MinecraftForge.EVENT_BUS.register(ScopeJitterHandler.getInstance()); // All built by MayDayMemory part of the Timeless dev team, amazing work!!!!!!!!!!!
        MinecraftForge.EVENT_BUS.register(MovementAdaptationsHandler.get());
        MinecraftForge.EVENT_BUS.register(AnimationHandler.INSTANCE); //Mainly controls when the animation should play.
        if (Config.COMMON.development.enableTDev.get()) {
            /*MinecraftForge.EVENT_BUS.register(GuiEditor.get());
            MinecraftForge.EVENT_BUS.register(GunEditor.get());
            MinecraftForge.EVENT_BUS.register(ScopeEditor.get());
            MinecraftForge.EVENT_BUS.register(ObjectRenderEditor.get());*/
        }

        //ClientRegistry.bindTileEntityRenderer(ModTileEntities.UPGRADE_BENCH.get(), UpgradeBenchRenderUtil::new);

        setupRenderLayers();
        registerColors();
        registerModelOverrides();
        registerScreenFactories();

        AnimationHandler.preloadAnimations();
        new AnimationRunner(); //preload thread pool
        new SecondOrderDynamics(1f, 1f, 1f, 1f); //preload thread pool

        Map<String, EntityRenderer<? extends Player>> skins = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        //addVestLayer(skins.get("default"));
        //addVestLayer(skins.get("slim"));
    }

    private static void addVestLayer(LivingEntityRenderer<? extends Player, HumanoidModel<Player>> renderer) {
        //renderer.addLayer(new VestLayerRender<>(RenderLayerParent));
    }

    private static void setupRenderLayers() {
        //ItemBlockRenderTypes.setRenderLayer(ModBlocks.UPGRADE_BENCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WORKBENCH.get(), RenderType.cutout());
    }

    private static void registerEntityRenders() {
        EntityRenderers.register(ModEntities.PROJECTILE.get(), ProjectileRenderer::new);
        EntityRenderers.register(ModEntities.GRENADE.get(), GrenadeRenderer::new);
        EntityRenderers.register(ModEntities.THROWABLE_GRENADE.get(), ThrowableGrenadeRenderer::new);
        EntityRenderers.register(ModEntities.THROWABLE_STUN_GRENADE.get(), ThrowableGrenadeRenderer::new);
        //EntityRenderers.register(ModEntities.MISSILE.get(), MissileRenderer::new);
        EntityRenderers.register(ModEntities.RPG7_MISSILE.get(), MissileRenderer::new);
    }

    private static void registerColors() {
        ItemColor color = (stack, index) -> {
            if (!((IColored) stack.getItem()).canColor(stack)) {
                return -1;
            }
            if (index == 0) {
                return IDLNBTUtil.getInt(stack, "Color", -1);
            }
            return -1;
        };
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof IColored) {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });
    }

    private static void registerModelOverrides() {
        OverrideModelManager.register(ModItems.COYOTE_SIGHT.get(), new CoyoteSightModel());
        OverrideModelManager.register(ModItems.STANDARD_6_10x_SCOPE.get(), new Standard6_10xScopeModel());
        OverrideModelManager.register(ModItems.VORTEX_LPVO_3_6.get(), new VortexLPVO_3_6xScopeModel());
        //TODO: Fix up the SLX 2x, give a new reticle, new scope data, new mount and eye pos, pretty much remake the code end.
        //ModelOverrides.register(ModItems.SLX_2X.get(), new SLX_2X_ScopeModel());
        OverrideModelManager.register(ModItems.ACOG_4.get(), new ACOG_4x_ScopeModel());
        OverrideModelManager.register(ModItems.ELCAN_DR_14X.get(), new elcan_14x_ScopeModel());
        OverrideModelManager.register(ModItems.AIMPOINT_T2_SIGHT.get(), new AimpointT2SightModel());

        OverrideModelManager.register(ModItems.AIMPOINT_T1_SIGHT.get(), new AimpointT1SightModel());

        OverrideModelManager.register(ModItems.EOTECH_N_SIGHT.get(), new EotechNSightModel());
        OverrideModelManager.register(ModItems.VORTEX_UH_1.get(), new VortexUh1SightModel());
        OverrideModelManager.register(ModItems.EOTECH_SHORT_SIGHT.get(), new EotechShortSightModel());
        OverrideModelManager.register(ModItems.SRS_RED_DOT_SIGHT.get(), new SrsRedDotSightModel());
        OverrideModelManager.register(ModItems.QMK152.get(), new Qmk152ScopeModel());

        OverrideModelManager.register(ModItems.OLD_LONGRANGE_8x_SCOPE.get(), new OldLongRange8xScopeModel());
        OverrideModelManager.register(ModItems.OLD_LONGRANGE_4x_SCOPE.get(), new OldLongRange4xScopeModel());

        OverrideModelManager.register(ModItems.MINI_DOT.get(), new MiniDotSightModel());
        //ModelOverrides.register(ModItems.MICRO_HOLO_SIGHT.get(), new MicroHoloSightModel());
        OverrideModelManager.register(ModItems.SRO_DOT.get(), new SroDotSightModel());

        // Armor registry, kept manual cause nice and simple, requires registry on client side only
        VestLayerRender.registerModel(ModItems.LIGHT_ARMOR.get(), new ModernArmor());
        VestLayerRender.registerModel(ModItems.MEDIUM_STEEL_ARMOR.get(), new MediumArmor());
        //VestLayerRender.registerModel(ModItems.CARDBOARD_ARMOR_FUN.get(), new CardboardArmor());
    }

    private static void registerScreenFactories() {
        MenuScreens.register(ModContainers.WORKBENCH.get(), WorkbenchScreen::new);
        MenuScreens.register(ModContainers.UPGRADE_BENCH.get(), UpgradeBenchScreen::new);
        MenuScreens.register(ModContainers.ATTACHMENTS.get(), AttachmentScreen::new);
        MenuScreens.register(ModContainers.INSPECTION.get(), InspectScreen::new);
        MenuScreens.register(ModContainers.ARMOR_R1.get(), AmmoScreen<R1_RigContainer>::new);
        MenuScreens.register(ModContainers.ARMOR_R2.get(), AmmoScreen<R2_RigContainer>::new);
        MenuScreens.register(ModContainers.ARMOR_R3.get(), AmmoScreen<R3_RigContainer>::new);
        MenuScreens.register(ModContainers.ARMOR_R4.get(), AmmoScreen<R4_RigContainer>::new);
        MenuScreens.register(ModContainers.ARMOR_R5.get(), AmmoScreen<R5_RigContainer>::new);
        //ScreenManager.registerFactory(ModContainers.COLOR_BENCH.get(), ColorBenchAttachmentScreen::new);
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof MouseSettingsScreen) {
            MouseSettingsScreen screen = (MouseSettingsScreen) event.getScreen();
            if (mouseOptionsField == null) {
                mouseOptionsField = ObfuscationReflectionHelper.findField(MouseSettingsScreen.class, "f_96218_");
                mouseOptionsField.setAccessible(true);
            }
            try {
                OptionsList list = (OptionsList) mouseOptionsField.get(screen);
                list.addSmall(GunOptions.ADS_SENSITIVITY, GunOptions.HOLD_TO_AIM/*, GunOptions.CROSSHAIR*/);
                list.addSmall(GunOptions.ALLOW_CHESTS, GunOptions.ALLOW_FENCE_GATES);
                list.addSmall(GunOptions.ALLOW_LEVER, GunOptions.ALLOW_BUTTON);
                list.addSmall(GunOptions.ALLOW_DOORS, GunOptions.ALLOW_TRAP_DOORS);
                list.addSmall(new CycleOption[]{GunOptions.ALLOW_CRAFTING_TABLE});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (event.getScreen() instanceof PauseScreen) {
            PauseScreen  screen = (PauseScreen ) event.getScreen();

            event.addListener((new Button(screen.width / 2 - 215, 10, 75, 20, new TranslatableComponent("tac.options.gui_settings"), (p_213126_1_) -> {
                Minecraft.getInstance().setScreen(new TaCSettingsScreen(screen, Minecraft.getInstance().options));
            })));
        }
    }

    static {
        Keys.ATTACHMENTS.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.ATTACHMENTS))
                return;

            final Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null)
                PacketHandler.getPlayChannel().sendToServer(new MessageAttachments());
        });

        Keys.INSPECT.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.INSPECT))
                return;

            final Minecraft mc = Minecraft.getInstance();
            if (
                    mc.player != null
                            && mc.screen == null
                            && GunAnimationController.fromItem(
                            Minecraft.getInstance().player.getInventory().getSelected().getItem()
                    ) == null
            ) PacketHandler.getPlayChannel().sendToServer(new MessageInspection());
        });
    }


    /* Uncomment for debugging headshot hit boxes */

    /*@SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRenderLiving(RenderLivingEvent.Post event)
    {
        LivingEntity entity = event.getEntity();
        IHeadshotBox<LivingEntity> headshotBox = (IHeadshotBox<LivingEntity>) BoundingBoxManager.getHeadshotBoxes(entity.getType());
        if(headshotBox != null)
        {
            AxisAlignedBB box = headshotBox.getHeadshotBox(entity);
            if(box != null)
            {
                WorldRenderer.drawBoundingBox(event.getMatrixStack(), event.getBuffers().getBuffer(RenderType.getLines()), box, 1.0F, 1.0F, 0.0F, 1.0F);

                AxisAlignedBB boundingBox = entity.getBoundingBox().offset(entity.getPositionVec().inverse());
                boundingBox = boundingBox.grow(Config.COMMON.gameplay.growBoundingBoxAmountV2.get(), 0, Config.COMMON.gameplay.growBoundingBoxAmountV2.get());
                WorldRenderer.drawBoundingBox(event.getMatrixStack(), event.getBuffers().getBuffer(RenderType.getLines()), boundingBox, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }*/
}
