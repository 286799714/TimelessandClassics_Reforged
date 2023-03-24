package com.tac.guns.client;

import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.handler.*;
import com.tac.guns.client.handler.command.GuiEditor;
import com.tac.guns.client.handler.command.GunEditor;
import com.tac.guns.client.handler.command.ObjectRenderEditor;
import com.tac.guns.client.handler.command.ScopeEditor;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.armor.VestLayer.VestLayerRender;
import com.tac.guns.client.render.armor.models.MediumArmor;
import com.tac.guns.client.render.armor.models.ModernArmor;
import com.tac.guns.client.render.entity.GrenadeRenderer;
import com.tac.guns.client.render.entity.MissileRenderer;
import com.tac.guns.client.render.entity.ProjectileRenderer;
import com.tac.guns.client.render.entity.ThrowableGrenadeRenderer;
import com.tac.guns.client.render.gun.ModelOverrides;
import com.tac.guns.client.render.gun.model.scope.*;
import com.tac.guns.client.screen.*;
import com.tac.guns.client.settings.GunOptions;
import com.tac.guns.client.util.UpgradeBenchRenderUtil;
import com.tac.guns.init.*;
import com.tac.guns.item.IColored;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageAttachments;
import com.tac.guns.network.message.MessageInspection;
import com.tac.guns.util.IDLNBTUtil;
import com.tac.guns.util.math.SecondOrderDynamics;
import de.javagl.jgltf.model.animation.AnimationRunner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientHandler
{
    private static Field mouseOptionsField;

    private static File keyBindsFile;
    
    public static void setup( Minecraft mc ) {
        MinecraftForge.EVENT_BUS.register(AimingHandler.get());
        MinecraftForge.EVENT_BUS.register(BulletTrailRenderingHandler.get());
        MinecraftForge.EVENT_BUS.register(CrosshairHandler.get());
        MinecraftForge.EVENT_BUS.register(GunRenderingHandler.get());
        MinecraftForge.EVENT_BUS.register(RecoilHandler.get());
        MinecraftForge.EVENT_BUS.register(ReloadHandler.get());
        MinecraftForge.EVENT_BUS.register(ShootingHandler.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::registerEntityRenders);
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
            MinecraftForge.EVENT_BUS.register(GuiEditor.get());
            MinecraftForge.EVENT_BUS.register(GunEditor.get());
            MinecraftForge.EVENT_BUS.register(ScopeEditor.get());
            MinecraftForge.EVENT_BUS.register(ObjectRenderEditor.get());
        }

        //ClientRegistry.bindTileEntityRenderer(ModTileEntities.UPGRADE_BENCH.get(), UpgradeBenchRenderUtil::new);

        // Load key binds
        InputHandler.initKeys();
        keyBindsFile = new File(mc.gameDirectory, "config/tac-key-binds.json");
        if (!keyBindsFile.exists()) {
            try {
                keyBindsFile.createNewFile();
            } catch (IOException e) {
                GunMod.LOGGER.error("Fail to create key bindings file");
            }
            InputHandler.saveTo(keyBindsFile);
        } else InputHandler.readFrom(keyBindsFile);

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
    private static void addVestLayer(LivingEntityRenderer<? extends Player, HumanoidModel<Player>> renderer)
    {
        //renderer.addLayer(new VestLayerRender<>(RenderLayerParent));
    }

    private static void setupRenderLayers()
    {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.UPGRADE_BENCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WORKBENCH.get(), RenderType.cutout());
    }

    private static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.PROJECTILE.get(), ProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.GRENADE.get(), GrenadeRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWABLE_GRENADE.get(), ThrowableGrenadeRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWABLE_STUN_GRENADE.get(), ThrowableGrenadeRenderer::new);
        //RenderingRegistry.registerEntityRenderingHandler(ModEntities.MISSILE.get(), MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.RPG7_MISSILE.get(), MissileRenderer::new);
    }

    private static void registerColors()
    {
        ItemColor color = (stack, index) -> {
            if(!((IColored) stack.getItem()).canColor(stack))
            {
                return -1;
            }
            if(index == 0)
            {
                return IDLNBTUtil.getInt(stack,"Color",-1);
            }
            return -1;
        };
        ForgeRegistries.ITEMS.forEach(item -> {
            if(item instanceof IColored)
            {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });
    }

    private static void registerModelOverrides()
    {
        ModelOverrides.register(ModItems.COYOTE_SIGHT.get(), new CoyoteSightModel());
        ModelOverrides.register(ModItems.LONGRANGE_8x_SCOPE.get(), new LongRange8xScopeModel());
        ModelOverrides.register(ModItems.VORTEX_LPVO_1_6.get(), new VortexLPVO_1_4xScopeModel());
        //TODO: Fix up the SLX 2x, give a new reticle, new scope data, new mount and eye pos, pretty much remake the code end.
        //ModelOverrides.register(ModItems.SLX_2X.get(), new SLX_2X_ScopeModel());
        ModelOverrides.register(ModItems.ACOG_4.get(), new ACOG_4x_ScopeModel());
        ModelOverrides.register(ModItems.ELCAN_DR_14X.get(), new elcan_14x_ScopeModel());
        ModelOverrides.register(ModItems.AIMPOINT_T2_SIGHT.get(), new AimpointT2SightModel());

        ModelOverrides.register(ModItems.AIMPOINT_T1_SIGHT.get(), new AimpointT1SightModel());

        ModelOverrides.register(ModItems.EOTECH_N_SIGHT.get(), new EotechNSightModel());
        ModelOverrides.register(ModItems.VORTEX_UH_1.get(), new VortexUh1SightModel());
        ModelOverrides.register(ModItems.EOTECH_SHORT_SIGHT.get(), new EotechShortSightModel());
        ModelOverrides.register(ModItems.SRS_RED_DOT_SIGHT.get(), new SrsRedDotSightModel());
        ModelOverrides.register(ModItems.QMK152.get(), new Qmk152ScopeModel());

        ModelOverrides.register(ModItems.OLD_LONGRANGE_8x_SCOPE.get(), new OldLongRange8xScopeModel());
        ModelOverrides.register(ModItems.OLD_LONGRANGE_4x_SCOPE.get(), new OldLongRange4xScopeModel());

        ModelOverrides.register(ModItems.MINI_DOT.get(), new MiniDotSightModel());
        //ModelOverrides.register(ModItems.MICRO_HOLO_SIGHT.get(), new MicroHoloSightModel());
        ModelOverrides.register(ModItems.SRO_DOT.get(), new SroDotSightModel());

        // Armor registry, kept manual cause nice and simple, requires registry on client side only
        VestLayerRender.registerModel(ModItems.LIGHT_ARMOR.get(), new ModernArmor());
        VestLayerRender.registerModel(ModItems.MEDIUM_STEEL_ARMOR.get(), new MediumArmor());
        //VestLayerRender.registerModel(ModItems.CARDBOARD_ARMOR_FUN.get(), new CardboardArmor());
    }

    private static void registerScreenFactories()
    {
        MenuScreens.register(ModContainers.WORKBENCH.get(), WorkbenchScreen::new);
        MenuScreens.register(ModContainers.UPGRADE_BENCH.get(), UpgradeBenchScreen::new);
        MenuScreens.register(ModContainers.ATTACHMENTS.get(), AttachmentScreen::new);
        MenuScreens.register(ModContainers.INSPECTION.get(), InspectScreen::new);
        MenuScreens.register(ModContainers.ARMOR_TEST.get(), AmmoPackScreen::new);
        //ScreenManager.registerFactory(ModContainers.COLOR_BENCH.get(), ColorBenchAttachmentScreen::new);
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.InitScreenEvent.Post event)
    {
        if(event.getScreen() instanceof MouseSettingsScreen)
        {
            MouseSettingsScreen screen = (MouseSettingsScreen) event.getScreen();
            if(mouseOptionsField == null)
            {
                mouseOptionsField = ObfuscationReflectionHelper.findField(MouseSettingsScreen.class, "f_96218_");
                mouseOptionsField.setAccessible(true);
            }
            try
            {
                OptionsList list = (OptionsList) mouseOptionsField.get(screen);
                //list.addSmall(GunOptions.ADS_SENSITIVITY, GunOptions.CROSSHAIR);
                /*, GunOptions.BURST_MECH);*/
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        if(event.getScreen() instanceof VideoSettingsScreen)
        {
            VideoSettingsScreen screen = (VideoSettingsScreen) event.getScreen();;

            event.addListener((new Button(screen.width / 2 - 215, 10, 75, 20, new TranslatableComponent("tac.options.gui_settings"), (p_213126_1_) -> {
                Minecraft.getInstance().setScreen(new TaCSettingsScreen(screen, Minecraft.getInstance().options));
            })));
        }
        /*if(event.getGui() instanceof MainMenuScreen)
        {
            MainMenuScreen screen = (MainMenuScreen) event.getGui();

            event.addWidget((new Button(screen.width / 2 - 215, 10, 75, 20, new TranslationTextComponent("tac.options.gui_settings"), (p_213126_1_) -> {
                Minecraft.getInstance().displayGuiScreen(new TaCSettingsScreen(screen, Minecraft.getInstance().gameSettings));
            })));
        }
*/
    }
    
    private static Screen prevScreen = null;

    @SubscribeEvent
    public static void onGUIChange(ScreenOpenEvent evt )
    {
        final Screen gui = evt.getScreen();

        // Show key binds if control GUI is activated
        if( gui instanceof ControlsScreen )
            InputHandler.restoreKeyBinds();
        else if( prevScreen instanceof ControlsScreen )
            InputHandler.clearKeyBinds( keyBindsFile );

        prevScreen = gui;
    }
    
    static {
        InputHandler.ATTACHMENTS.addPressCallback(() -> {
            final Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null)
                PacketHandler.getPlayChannel().sendToServer(new MessageAttachments());
        });

        final Runnable callback = () -> {
            final Minecraft mc = Minecraft.getInstance();
            if (
                    mc.player != null
                            && mc.screen == null
                            && GunAnimationController.fromItem(
                            Minecraft.getInstance().player.getInventory().getSelected().getItem()
                    ) == null
            ) PacketHandler.getPlayChannel().sendToServer(new MessageInspection());
        };
        InputHandler.INSPECT.addPressCallback(callback);
        InputHandler.CO_INSPECT.addPressCallback(callback);
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
