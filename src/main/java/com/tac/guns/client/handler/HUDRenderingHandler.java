package com.tac.guns.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix4f;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.common.Gun;
import com.tac.guns.common.ReloadTracker;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.transition.TimelessGunItem;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageFireMode;
import com.tac.guns.util.WearableHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Objects;

public class HUDRenderingHandler extends GuiComponent {
    private static HUDRenderingHandler instance;

    private static final ResourceLocation[] AMMO_ICONS = new ResourceLocation[]
            {
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/counterassule_rifle.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/counterlmg.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/counterpistol.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/countershotgun.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/countersmg.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/countersniper.png")
            };

    private static final ResourceLocation[] FIREMODE_ICONS_OLD = new ResourceLocation[]
            {
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/safety.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/semi.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/full.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/burst.png"),
            };
    private static final ResourceLocation[] FIREMODE_ICONS = new ResourceLocation[]
            {
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/firemode_safety.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/firemode_semi.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/firemode_auto.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/firemode_burst.png"),
            };
    private static final ResourceLocation[] RELOAD_ICONS = new ResourceLocation[]
            {
                    new ResourceLocation(Reference.MOD_ID, "textures/gui/reloadbar.png")
            };
    private static final ResourceLocation[] NOISE_S = new ResourceLocation[]
            {
                    new ResourceLocation(Reference.MOD_ID, "textures/screen_effect/noise1.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/screen_effect/noise2.png")
                    /*new ResourceLocation(Reference.MOD_ID, "textures/screen_effect/noise4.png"),
                    new ResourceLocation(Reference.MOD_ID, "textures/screen_effect/noise5.png")*/
            };

    public static HUDRenderingHandler get() {
        return instance == null ? instance = new HUDRenderingHandler() : instance;
    }

    private HUDRenderingHandler() {
    }

    private int ammoReserveCount = 0;

    private ResourceLocation heldAmmoID = new ResourceLocation("");

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END)
            return;
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof GunItem) {
            GunItem gunItem = (GunItem) Minecraft.getInstance().player.getMainHandItem().getItem();
            this.ammoReserveCount = ReloadTracker.calcMaxReserveAmmo(Gun.findAmmo(Minecraft.getInstance().player, gunItem.getGun().getProjectile().getItem()));
            // Only send if current id doesn't equal previous id, otherwise other serverside actions can force this to change like reloading
            if (player.isCreative())
                return;
            //if(gunItem.getGun().getProjectile().getItem().compareTo(heldAmmoID) != 0 || ammoReserveCount == 0) {
            heldAmmoID = gunItem.getGun().getProjectile().getItem();
            //}
        }
    }


    // Jitter minecraft player screen a tiny bit per system nano time
    private void jitterScreen(float partialTicks) {
        long time = System.nanoTime();
        float jitterX = (float) (Math.sin(time / 1000000000.0) * 0.0005);
        float jitterY = (float) (Math.cos(time / 1000000000.0) * 0.0005);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);//beginWrite(false);
        GL11.glPushMatrix();
        GL11.glTranslatef(jitterX, jitterY, 0);
        tessellator.end();
    }


    // EnchancedVisuals-1.16.5 helped with this one
    private ResourceLocation getNoiseTypeResource(boolean doNoise) {
        long time = Math.abs(System.nanoTime() / 3000000 / 50);
        return NOISE_S[(int) (time % NOISE_S.length)];
    }

    // A method that tints the screen green like night vision if true
    private void renderNightVision(boolean doNightVision) {
        brightenScreen(doNightVision);
        if (doNightVision) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            bufferbuilder.vertex(0.0D, (double) Minecraft.getInstance().getWindow().getGuiScaledHeight(), -90.0D).endVertex();
            bufferbuilder.vertex((double) Minecraft.getInstance().getWindow().getGuiScaledWidth(), (double) Minecraft.getInstance().getWindow().getGuiScaledHeight(), -90.0D).endVertex();
            bufferbuilder.vertex((double) Minecraft.getInstance().getWindow().getGuiScaledWidth(), 0.0D, -90.0D).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).endVertex();
            tessellator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    // Final
    private double defaultGameGamma = 0;


    // "Brightens the screen" However this example is more useful less gimick, I want to be a bit more gimicky, but a great V1 to be honest - ClumsyAlien
    private void brightenScreen(boolean doNightVision) {

        // Basic force gammed night vision
        if (doNightVision) {
            if (defaultGameGamma == 0)
                defaultGameGamma = Minecraft.getInstance().options.gamma;
            Minecraft.getInstance().options.gamma = 200;
        } else {
            Minecraft.getInstance().options.gamma = defaultGameGamma;
        }
    }

    private static final ResourceLocation fleshHitMarker = new ResourceLocation(Reference.MOD_ID, "textures/crosshair_hit/hit_marker_no_opac.png");
    public boolean hitMarkerHeadshot = false;
    public static final float hitMarkerRatio = 14f;
    public float hitMarkerTracker = 0;

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getMainHandItem();
        PoseStack stack = event.getMatrixStack();
        float anchorPointX = event.getWindow().getGuiScaledWidth() / 12F * 11F;
        float anchorPointY = event.getWindow().getGuiScaledHeight() / 10F * 9F;

        float configScaleWeaponCounter = Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.get().floatValue();
        float configScaleWeaponFireMode = Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.get().floatValue();
        float configScaleWeaponReloadBar = Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.get().floatValue();

        float counterSize = 1.8F * configScaleWeaponCounter;
        float fireModeSize = 32.0F * configScaleWeaponFireMode;
        float ReloadBarSize = 32.0F * configScaleWeaponReloadBar;

        float hitMarkerSize = 128.0F;

        RenderSystem.enableDepthTest();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        int width = event.getWindow().getScreenWidth();
        int height = event.getWindow().getScreenHeight();
        int centerX = event.getWindow().getGuiScaledWidth() / 2;
        int centerY = event.getWindow().getGuiScaledHeight() / 2;

        if (Config.CLIENT.display.showHitMarkers.get()) {
            if (this.hitMarkerTracker > 0)//Hit Markers
            {
                RenderSystem.enableBlend();
                stack.pushPose();
                {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.setShaderTexture(0, fleshHitMarker); // Future options to render bar types

                    float opac = Math.max(Math.min(this.hitMarkerTracker / hitMarkerRatio, 100f), 0.20f);
                    if (hitMarkerHeadshot)
                        RenderSystem.setShaderColor(1.0f, 0.075f, 0.075f, opac); // Only render red
                    else
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opac);
                    blit(stack, centerX - 8, centerY - 8, 0, 0, 16, 16, 16, 16); //-264 + (int)(-9.0/4),-134,
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                stack.popPose();
            }
        }

        // All code for rendering night vision, still only a test
        if (false) {
            renderNightVision(Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get());
            if (Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get()) {

                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                stack.pushPose();

                RenderSystem.setShaderTexture(0, this.getNoiseTypeResource(true));
                float opacity = 0.25f;//0.125f;// EnchancedVisuals-1.16.5 helped with this one, instead have a fading opacity visual.getOpacity();
                Matrix4f matrix = stack.last().pose();
                buffer.vertex(matrix, 0, width, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, opacity).endVertex();
                buffer.vertex(matrix, width, height, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, opacity).endVertex();
                buffer.vertex(matrix, width, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, opacity).endVertex();
                buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, opacity).endVertex();

                buffer.end();
                BufferUploader.end(buffer);
                stack.popPose();
            }
        }


        // FireMode rendering
        /*RenderSystem.enableDepthTest();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        stack.pushPose();
        {
            stack.translate(anchorPointX - (ReloadBarSize * 4.35) / 4F, anchorPointY + (ReloadBarSize * 1.625F) / 5F * 3F, 0);//stack.translate(anchorPointX - (fireModeSize*6) / 4F, anchorPointY - (fireModeSize*1F) / 5F * 3F, 0); // *68for21F
            stack.translate(-ReloadBarSize, -ReloadBarSize, 0);
            // stack.translate(0, 0, );
            stack.scale(2.1F * (1 - ArmorInteractionHandler.get().getRepairProgress(event.getPartialTicks(), player)), 0.25F, 0); // *21F
            RenderSystem.setShaderTexture(0, RELOAD_ICONS[0]);
            Matrix4f matrix = stack.last().pose();
            buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
        }
        buffer.end();
        BufferUploader.end(buffer);
        stack.popPose();*/


        /*if(ArmorInteractionHandler.get().isRepairing())//Replace with reload bar checker
        {
            // FireMode rendering
            RenderSystem.enableAlphaTest();
            buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            stack.push();
            {
                stack.translate(anchorPointX - (ReloadBarSize*4.35) / 4F, anchorPointY + 20f + (ReloadBarSize*1.625F) / 5F * 3F, 0);//stack.translate(anchorPointX - (fireModeSize*6) / 4F, anchorPointY - (fireModeSize*1F) / 5F * 3F, 0); // *68for21F
                stack.translate(-ReloadBarSize, -ReloadBarSize, 0);
                // stack.translate(0, 0, );
                stack.scale(2.1F*(1-ArmorInteractionHandler.get().getRepairProgress(event.getPartialTicks(), player)),0.25F,0); // *21F
                Minecraft.getInstance().getTextureManager().bindTexture(RELOAD_ICONS[0]); // Future options to render bar types

                Matrix4f matrix = stack.getLast().getMatrix();
                buffer.pos(matrix, 0, ReloadBarSize, 0).tex(0, 1).color(1.0F, 0.0F, 1.0F, 0.99F).endVertex();
                buffer.pos(matrix, ReloadBarSize, ReloadBarSize, 0).tex(1, 1).color(1.0F, 0.0F, 1.0F, 0.99F).endVertex();
                buffer.pos(matrix, ReloadBarSize, 0, 0).tex(1, 0).color(1.0F, 0.0F, 1.0F, 0.99F).endVertex();
                buffer.pos(matrix, 0, 0, 0).tex(0, 0).color(1.0F, 0.0F, 1.0F, 0.99F).endVertex();
            }
            buffer.finishDrawing();
            WorldVertexBufferUploader.draw(buffer);
            stack.pop();
        }*/


        if (!(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TimelessGunItem))
            return;
        TimelessGunItem gunItem = (TimelessGunItem) heldItem.getItem();
        Gun gun = gunItem.getGun();

        if (!Config.CLIENT.weaponGUI.weaponGui.get()) {
            return;
        }

        if(Config.CLIENT.weaponGUI.weaponFireMode.showWeaponFireMode.get()) {
            // FireMode rendering
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            stack.pushPose();
            {
                stack.translate(anchorPointX - (fireModeSize * 2) / 4F, anchorPointY - (fireModeSize * 2) / 5F * 3F, 0);
                stack.translate(-fireModeSize + (-62.7) + (-Config.CLIENT.weaponGUI.weaponFireMode.x.get().floatValue()), -fireModeSize + 52.98 + (-Config.CLIENT.weaponGUI.weaponFireMode.y.get().floatValue()), 0);

                stack.translate(20, 5, 0);
                int fireMode = 0;

                if (player.getMainHandItem().getItem() instanceof GunItem) {
                    try {
                        if (heldItem.getTag() == null) {
                            heldItem.getOrCreateTag();
                        }
                        int[] gunItemFireModes = heldItem.getTag().getIntArray("supportedFireModes");
                        if (ArrayUtils.isEmpty(gunItemFireModes)) {
                            gunItemFireModes = gun.getGeneral().getRateSelector();
                            heldItem.getTag().putIntArray("supportedFireModes", gunItemFireModes);
                            heldItem.getTag().putInt("CurrentFireMode", gunItemFireModes[0]);
                        } else if (!Arrays.equals(gunItemFireModes, gun.getGeneral().getRateSelector())) {
                            heldItem.getTag().putIntArray("supportedFireModes", gun.getGeneral().getRateSelector());
                            if (!heldItem.getTag().contains("CurrentFireMode"))
                                heldItem.getTag().putInt("CurrentFireMode", gunItemFireModes[0]);
                        }
                        if (player.getMainHandItem().getTag() == null)
                            if (!Config.COMMON.gameplay.safetyExistence.get() && Objects.requireNonNull(player.getMainHandItem().getTag()).getInt("CurrentFireMode") == 0 && gunItemFireModes.length > 1)
                                fireMode = gunItemFireModes[1];
                            else
                                fireMode = gunItemFireModes[0];
                        else if (player.getMainHandItem().getTag().getInt("CurrentFireMode") == 0 && gunItemFireModes.length > 1)
                            if (!Config.COMMON.gameplay.safetyExistence.get()) {
                                fireMode = gunItemFireModes[0];
                                if (fireMode == 0)
                                    fireMode = gunItemFireModes[1];
                            } else
                                fireMode = gunItemFireModes[0];
                        else {
                            fireMode = Objects.requireNonNull(player.getMainHandItem().getTag()).getInt("CurrentFireMode");
                            //int fireMode = gunItem.getSupportedFireModes()[gunItem.getCurrFireMode()];
                            if (!Config.COMMON.gameplay.safetyExistence.get() && fireMode == 0) {
                                fireMode = gunItemFireModes[0];
                                if (fireMode == 0)
                                    fireMode = gunItemFireModes[1];
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        fireMode = gun.getGeneral().getRateSelector()[0];
                    } catch (Exception e) {
                        fireMode = 0;
                        GunMod.LOGGER.log(Level.ERROR, "TaC HUD_RENDERER has failed obtaining the fire mode");
                    }
                    RenderSystem.setShaderTexture(0, FIREMODE_ICONS[fireMode]); // Render true firemode

                    Matrix4f matrix = stack.last().pose();
                    buffer.vertex(matrix, 0, fireModeSize / 2, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                    buffer.vertex(matrix, fireModeSize / 2, fireModeSize / 2, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                    buffer.vertex(matrix, fireModeSize / 2, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                    buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                }
            }
            stack.popPose();
            buffer.end();
            BufferUploader.end(buffer);
        }
        if(Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.get()) {
            // Text rendering
            stack.pushPose();
            {
                stack.translate(
                        (anchorPointX - (counterSize * 32) / 2) + (-Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get().floatValue()),
                        (anchorPointY - (counterSize * 32) / 4) + (-Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get().floatValue()),
                        0
                );
                if (player.getMainHandItem().getTag() != null) {
                    MutableComponent currentAmmo;
                    MutableComponent reserveAmmo;
                    int ammo = player.getMainHandItem().getTag().getInt("AmmoCount");
                    if (player.getMainHandItem().getTag().getInt("AmmoCount") <= gun.getReloads().getMaxAmmo() / 4 && this.ammoReserveCount <= gun.getReloads().getMaxAmmo()) {
                        currentAmmo = byPaddingZeros(ammo).append(new TranslatableComponent("" + ammo)).withStyle(ChatFormatting.RED);
                        reserveAmmo =
                                byPaddingZeros(this.ammoReserveCount > 10000 ? 10000 : this.ammoReserveCount).append(new TranslatableComponent("" + (this.ammoReserveCount > 10000 ? 9999 : this.ammoReserveCount))).withStyle(ChatFormatting.RED);
                    } else if (this.ammoReserveCount <= gun.getReloads().getMaxAmmo()) {
                        currentAmmo = byPaddingZeros(ammo).append(new TranslatableComponent("" + ammo).withStyle(ChatFormatting.WHITE));
                        reserveAmmo = byPaddingZeros(this.ammoReserveCount > 10000 ? 10000 : this.ammoReserveCount).append(new TranslatableComponent("" + (this.ammoReserveCount > 10000 ? 9999 : this.ammoReserveCount))).withStyle(ChatFormatting.RED);
                    } else if (player.getMainHandItem().getTag().getInt("AmmoCount") <= gun.getReloads().getMaxAmmo() / 4) {
                        currentAmmo = byPaddingZeros(ammo).append(new TranslatableComponent("" + ammo)).withStyle(ChatFormatting.RED);
                        reserveAmmo = byPaddingZeros(this.ammoReserveCount > 10000 ? 10000 : this.ammoReserveCount).append(new TranslatableComponent("" + (this.ammoReserveCount > 10000 ? 9999 : this.ammoReserveCount))).withStyle(ChatFormatting.GRAY);
                    } else {
                        currentAmmo = byPaddingZeros(ammo).append(new TranslatableComponent("" + ammo).withStyle(ChatFormatting.WHITE));
                        reserveAmmo = byPaddingZeros(this.ammoReserveCount > 10000 ? 10000 : this.ammoReserveCount).append(new TranslatableComponent("" + (this.ammoReserveCount > 10000 ? 9999 : this.ammoReserveCount))).withStyle(ChatFormatting.GRAY);
                    }
                    stack.scale(counterSize, counterSize, counterSize);
                    stack.pushPose();
                    {
                        stack.translate(-21.15, 0, 0);
                        drawString(stack, Minecraft.getInstance().font, currentAmmo, 0, 0, 0xffffff); // Gun ammo
                    }
                    stack.popPose();

                    stack.pushPose();
                    {
                        stack.scale(0.7f, 0.7f, 0.7f);
                        stack.translate(
                                (3.7),
                                (3.4),
                                0);
                        drawString(stack, Minecraft.getInstance().font, reserveAmmo, 0, 0, 0xffffff); // Reserve ammo
                    }
                    stack.popPose();
                }
            }
            stack.popPose();
        }

        //ARMOR
        if(Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.get())
        {
            ItemStack rig = ((PlayerWithSynData)player).getRig();
            if(!rig.isEmpty()){
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                stack.pushPose();
                {
                    MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                    float iconAnchorX = (anchorPointX - (counterSize*32) / 2) + (-Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get().floatValue()) + 38;
                    float iconAnchorY = (anchorPointY - 3.5f + (-Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get().floatValue()));
                    itemRenderer.renderGuiItem(rig, (int) iconAnchorX , (int) iconAnchorY);
                }
                stack.popPose();
            }

            stack.pushPose();
            buffer = Tesselator.getInstance().getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            stack.translate(anchorPointX - (ReloadBarSize*4.35) / 4F, anchorPointY + (ReloadBarSize*1.625F) / 5F * 3F, 0);//stack.translate(anchorPointX - (fireModeSize*6) / 4F, anchorPointY - (fireModeSize*1F) / 5F * 3F, 0); // *68for21F
            stack.translate(-ReloadBarSize, -ReloadBarSize, 0);

            stack.translate(-16.25-7.3, 0.15+1.6, 0);
            stack.scale(3.05F,0.028F,0); // *21F
            RenderSystem.setShaderTexture(0, RELOAD_ICONS[0]); // Future options to render bar types

            Matrix4f matrix = stack.last().pose();
            buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();

            stack.translate(19.25, (1.5+(-63.4))*10, 0);
            // stack.translate(0, 0, );
            stack.scale(0.0095F,20.028F,0); // *21F

            buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
            buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();

            buffer.end();
            BufferUploader.end(buffer);
            stack.popPose();

            stack.pushPose();
            {
                //HANDLE ARMOR REPAIR TIMER
                buffer = Tesselator.getInstance().getBuilder();
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                stack.translate(anchorPointX - (ReloadBarSize * 4.35) / 4F, anchorPointY + (ReloadBarSize * 1.625F) / 5F * 3F, 0);//stack.translate(anchorPointX - (fireModeSize*6) / 4F, anchorPointY - (fireModeSize*1F) / 5F * 3F, 0); // *68for21F
                stack.translate(-ReloadBarSize, -ReloadBarSize, 0);

                stack.translate(-16.25-7.3, 7.25, 0);
                stack.scale(3.05F* (1 - ArmorInteractionHandler.get().getRepairProgress(player)),0.1F,0); // *21F
                RenderSystem.setShaderTexture(0, RELOAD_ICONS[0]); // Future options to render bar types

                matrix = stack.last().pose();
                buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.99F).endVertex();
                buffer.end();
                BufferUploader.end(buffer);
            }
            stack.popPose();

            if(!rig.isEmpty()) {
                float blackBarAlpha = 0.325F;
                var rigData = ((ArmorRigItem)rig.getItem()).getRig();
                //RENDER BACKGROUND FOR ARMOR HEALTH
                stack.pushPose();
                {

                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    buffer = Tesselator.getInstance().getBuilder();
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                    stack.translate(anchorPointX - (ReloadBarSize * 4.35) / 4F, anchorPointY + (ReloadBarSize * 1.625F) / 5F * 3F, 0);
                    stack.translate(-ReloadBarSize, -ReloadBarSize, 0);

                    stack.translate(-16.25 - 7.3, 3.25, 0);
                    stack.scale(3.05F, 0.224F, 0);

                    matrix = stack.last().pose();

                    buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(0.0F, 0.0F, 0.0F, blackBarAlpha).endVertex();
                    buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(0.0F, 0.0F, 0.0F, blackBarAlpha).endVertex();
                    buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(0.0F, 0.0F, 0.0F, blackBarAlpha).endVertex();
                    buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(0.0F, 0.0F, 0.0F, blackBarAlpha).endVertex();
                    buffer.end();
                    BufferUploader.end(buffer);
                }
                stack.popPose();
                //RENDER ARMOR HEALTH
                stack.pushPose();
                {
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    buffer = Tesselator.getInstance().getBuilder();
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                    stack.translate(anchorPointX - (ReloadBarSize * 4.35) / 4F, anchorPointY + (ReloadBarSize * 1.625F) / 5F * 3F, 0);
                    stack.translate(-ReloadBarSize, -ReloadBarSize, 0);

                    stack.translate(-16.25 - 5.35, 4.2, 0);
                    float healthPercentage = WearableHelper.currentDurabilityPercentage(rig);
                    stack.scale(2.925F*healthPercentage, 0.16F, 0);

                    matrix = stack.last().pose();
                    buffer.vertex(matrix, 0, ReloadBarSize, 0).uv(0, 1).color(1.0F, 1.0F, 1.0F, 0.8F).endVertex();
                    buffer.vertex(matrix, ReloadBarSize, ReloadBarSize, 0).uv(1, 1).color(1.0F, 1.0F, 1.0F, 0.8F).endVertex();
                    buffer.vertex(matrix, ReloadBarSize, 0, 0).uv(1, 0).color(1.0F, 1.0F, 1.0F, 0.8F).endVertex();
                    buffer.vertex(matrix, 0, 0, 0).uv(0, 0).color(1.0F, 1.0F, 1.0F, 0.8F).endVertex();
                    buffer.end();
                    BufferUploader.end(buffer);
                }
                stack.popPose();
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
            }
        }
    }
            /*if (Minecraft.getInstance().gameSettings.viewBobbing) {
                if (Minecraft.getInstance().player.ticksExisted % 2 == 0) {
                    Minecraft.getInstance().getTextureManager().bindTexture(NOISE_S[0]);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
                    RenderSystem.disableAlphaTest();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(0.0F, 0.0F, -0.01F);
                    float f = 5.0F;
                    RenderSystem.scalef(f, f, f);
                    float f1 = (float) (Minecraft.getInstance().player.ticksExisted % 3000) / 3000.0F / f;
                    float f2 = 0.0F;
                    float f3 = 0.0F;
                    float f4 = 0.0F;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                    bufferbuilder.pos(0.0D, (double) Minecraft.getInstance().getMainWindow().getScaledHeight(), (double) Minecraft.getInstance().getMainWindow().getScaledHeight()).tex((float) (f1 + f4), (float) (f2 + f3)).endVertex();
                    bufferbuilder.pos((double) Minecraft.getInstance().getMainWindow().getScaledWidth(), (double) Minecraft.getInstance().getMainWindow().getScaledHeight(), (double) Minecraft.getInstance().getMainWindow().getScaledHeight()).tex((float) (f1 + 1.0F / f + f4), (float) (f2 + f3)).endVertex();
                    bufferbuilder.pos((double) Minecraft.getInstance().getMainWindow().getScaledWidth(), 0.0D, (double) Minecraft.getInstance().getMainWindow().getScaledHeight()).tex((float) (f1 + 1.0F / f + f4), (float) (f2 + 1.0F / f + f3)).endVertex();
                    bufferbuilder.pos(0.0D, 0.0D, (double) Minecraft.getInstance().getMainWindow().getScaledHeight()).tex((float) (f1 + f4), (float) (f2 + 1.0F / f + f3)).endVertex();
                    tessellator.draw();
                    RenderSystem.popMatrix();
                    RenderSystem.enableAlphaTest();
                    RenderSystem.disableBlend();
                }
            }*/

    private static MutableComponent byPaddingZeros(int number) {
        String text = String.format("%0" + (byPaddingZerosCount(number)+1) + "d", 1);
        text = text.substring(0, text.length()-1);
        return new TranslatableComponent(text).withStyle(ChatFormatting.GRAY);
    }
    private static int byPaddingZerosCount(int length) {
        if(length < 10)
            return 2;
        if(length < 100)
            return 1;
        if(length < 1000)
            return 0;
        return 0;
    }
}