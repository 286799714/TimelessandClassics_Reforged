package com.tac.guns.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.net.optifine.shaders.TACOptifineShadersHelper;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.ai.brain.task.FarmTask;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.tac.guns.util.OptifineHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ScreenTextureState extends RenderState.TexturingState
{
    private static ScreenTextureState instance = null;

    public static ScreenTextureState instance()
    {
        return instance == null ? instance = new ScreenTextureState() : instance;
    }

    private int textureId;
    private int lastWindowWidth;
    private int lastWindowHeight;

    private ScreenTextureState()
    {
        super("screen_texture", () ->
        {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableTexture();
            //RenderSystem.
            RenderSystem.bindTexture(instance().getTextureId());
        }, () ->
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
        });
        //this.getTextureId();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onRenderHUD);
    }

    public int getTextureId()
    {
        if(this.textureId == 0)
        {
            this.textureId = TextureUtil.generateTextureId();
            // Texture params only need to be set once, not once per frame
            RenderSystem.bindTexture(this.textureId);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, 9729);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, 9729); // This final number may be useful
            glTexParameteri(3553 , GL_TEXTURE_MIN_FILTER, 9728);
            glTexParameteri(3553 , GL_TEXTURE_MAG_FILTER, 9728);
            /*int wrap = true ? 33071 : 10497;
            glTexParameteri(3553, 10242, wrap);
            glTexParameteri(3553, 10243, wrap);
            glTexParameteri(3553, 10240, 9728);
            glTexParameteri(3553, 10241, 9728);*/



            // Can I make optifine Shaders compatible?
            /*int wrap = true ? 33071 : 10497;
            glTexParameteri(3553, 10242, wrap);
            glTexParameteri(3553, 10243, wrap);
            glTexParameteri(3553, 10240, 9728);
            glTexParameteri(3553, 10241, 9728);
*/
        }
        return this.textureId;
    }

    public static int MIRROR_TEX;
    public static int OVERLAY_TEX;
    public static int INSIDE_GUN_TEX;
    public static int DEPTH_TEX;
    public static int DEPTH_ERASE_TEX;
    public static int SCOPE_MASK_TEX;
    public static int SCOPE_LIGHTMAP_TEX;
    public static int lastWidth;
    public static int lastHeight;
    /*@SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.player != null && mc.currentScreen == null) {
                //If player has gun, update scope
                if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null && mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemGun && mc.gameSettings.thirdPersonView == 0) {
                    if (GunType.getAttachment(mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Sight) != null) {
                        final ItemAttachment itemAttachment = (ItemAttachment) GunType.getAttachment(mc.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND), AttachmentPresetEnum.Sight).getItem();
                        if (itemAttachment != null) {
                            if (itemAttachment.type != null) {
                                if (itemAttachment.type.sight.modeType.isMirror) {
                                    if (OVERLAY_TEX == -1 || (lastWidth != mc.displayWidth || lastHeight != mc.displayHeight)) {
                                        GL11.glPushMatrix();
                                        if (OVERLAY_TEX != -1) {
                                            GL11.glDeleteTextures(OVERLAY_TEX);
                                        }
                                        OVERLAY_TEX = GL11.glGenTextures();
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, OVERLAY_TEX);
                                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.displayWidth, mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                                        if (INSIDE_GUN_TEX != -1) {
                                            GL11.glDeleteTextures(INSIDE_GUN_TEX);
                                        }
                                        INSIDE_GUN_TEX = GL11.glGenTextures();
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, INSIDE_GUN_TEX);
                                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.displayWidth, mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

                                        if (SCOPE_MASK_TEX != -1) {
                                            GL11.glDeleteTextures(SCOPE_MASK_TEX);
                                        }
                                        SCOPE_MASK_TEX = GL11.glGenTextures();
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, SCOPE_MASK_TEX);
                                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.displayWidth, mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                                        if (SCOPE_LIGHTMAP_TEX != -1) {
                                            GL11.glDeleteTextures(SCOPE_LIGHTMAP_TEX);
                                        }
                                        SCOPE_LIGHTMAP_TEX = GL11.glGenTextures();
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, SCOPE_LIGHTMAP_TEX);
                                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.displayWidth, mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                                        lastWidth = mc.displayWidth;
                                        lastHeight = mc.displayHeight;
                                        GL11.glPopMatrix();
                                    }
                                    if (itemAttachment.type.sight.modeType.isPIP && RenderParameters.adsSwitch != 0) {
                                        renderWorld(mc, itemAttachment, event.renderTickTime);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/
    /*public void renderWorld(Minecraft mc, ItemAttachment itemAttachment, float partialTick) {

        float zoom = getFov(itemAttachment);

        GL11.glPushMatrix();
        GlStateManager.color(1, 1, 1,1);

        RenderGlobal renderBackup = mc.renderGlobal;
        //Save the current settings to be reset later
        long endTime = 0;
        boolean hide = mc.gameSettings.hideGUI;
        int view = mc.gameSettings.thirdPersonView;
        int limit = mc.gameSettings.limitFramerate;
        RayTraceResult mouseOver = mc.objectMouseOver;
        float fov = mc.gameSettings.fovSetting;
        boolean bobbingBackup = mc.gameSettings.viewBobbing;
        float mouseSensitivityBackup = mc.gameSettings.mouseSensitivity;

        mc.renderGlobal = scopeRenderGlobal;

        //Change game settings for the Scope
        mc.gameSettings.hideGUI = true;
        mc.gameSettings.thirdPersonView = 0;
        mc.gameSettings.fovSetting = zoom;
        mc.gameSettings.viewBobbing = false;
        //Make sure the FOV isn't less than 1
        if (mc.gameSettings.fovSetting < 0) {
            mc.gameSettings.fovSetting = 1;
        }

        if (limit != 0 && renderEndNanoTime != null) {
            try {
                endTime = renderEndNanoTime.getLong(mc.entityRenderer);
            } catch (Exception ignored) {
            }
        }

        int fps = Math.max(30, mc.gameSettings.limitFramerate);
        //Minecraft.getMinecraft().getFramebuffer().framebufferClear();
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);

        int tex=Minecraft.getMinecraft().getFramebuffer().framebufferTexture;
        Minecraft.getMinecraft().getFramebuffer().framebufferTexture = MIRROR_TEX;
        GL30.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, MIRROR_TEX, 0);

        mc.entityRenderer.renderWorld(partialTick, endTime);

        GL20.glUseProgram(0);


        Minecraft.getMinecraft().getFramebuffer().framebufferTexture = tex;
        GL30.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, tex, 0);
        //GL43.glCopyImageSubData(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, lastWidth, lastHeight, 1);

        if (limit != 0 && renderEndNanoTime != null) {
            try {
                renderEndNanoTime.setLong(mc.entityRenderer, endTime);
            } catch (Exception ignored) {
            }
        }


        //Go back to the original Settings
        mc.objectMouseOver = mouseOver;
        mc.gameSettings.limitFramerate = limit;
        mc.gameSettings.thirdPersonView = view;
        mc.gameSettings.hideGUI = hide;
        mc.gameSettings.viewBobbing = bobbingBackup;
        mc.gameSettings.mouseSensitivity = mouseSensitivityBackup;

        mc.gameSettings.fovSetting = fov;
        mc.renderGlobal = renderBackup;


        GL11.glPopMatrix();
    }
*/




    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        //genMirror();
        isRenderHand0=false;
    }*/


    // Next im guessing i'll have to check this world render, it says the render is incomplete, maybe this code is supposed to complete it for the specific image output?
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHUD(RenderWorldLastEvent event) {
        /*if(event.getType()!= RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }*/
        /*if(event.getType()!= RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }*/

        ItemStack stack=Minecraft.getInstance().player.getHeldItemMainhand();
        if (stack != null && stack.getItem() instanceof GunItem) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
            //GL11.glBindTexture(GL11.GL_TEXTURE_2D, MIRROR_TEX);
                if(OptifineHelper.isRenderingDfb() && OptifineHelper.getDfb() != null && TACOptifineShadersHelper.getFlipTextures() != null) {
                    //TODO: Optifine and OpenGL 2.1 Compatibility ?

                    //OptifineHelper.getDfb().exists() ? flipTextures.getA(0) : 0
                    GL43.glCopyImageSubData(TACOptifineShadersHelper.getFlipTextures().getA(0), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, this.textureId, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(),
                            Minecraft.getInstance().getMainWindow().getHeight(), 1);
                } else {
                    if (Shaders.capabilities.OpenGL43) {
                        GL43.glCopyImageSubData(Minecraft.getInstance().getFramebuffer().func_242996_f(), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, this.textureId, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(), 1);

                    } else {
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
                        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0,  0, 0,0,  Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight());
                    }
                }
                /*if(OptifineHelper.isShadersEnabled()) {
                    Shaders.pushProgram();
                    Shaders.useProgram(Shaders.ProgramNone);
                }*/
                GlStateManager.enableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);
            }
        }

    public void onRenderWorldLast()
    {
        // OpenGL will spit out an error (GL_INVALID_VALUE) if the window is minimised (or draw calls stop)
        // It seems just testing the width or height if it's zero is enough to prevent it
        MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
        if(mainWindow.getWidth() <= 0 || mainWindow.getHeight() <= 0)
                return;

        RenderSystem.bindTexture(this.textureId);
        if(mainWindow.getWidth() != this.lastWindowWidth || mainWindow.getHeight() != this.lastWindowHeight)
        {
            // When window resizes the texture needs to be re-initialized and copied, so both are done in the same call
            this.lastWindowWidth = mainWindow.getWidth();
            this.lastWindowHeight = mainWindow.getHeight();
            if(OptifineHelper.isRenderingDfb()) {
                FlipTextures flipTextures = new FlipTextures(Shaders.activeProgram.getName(), Shaders.activeProgram.getDrawBuffers().capacity());
                GL43.glCopyImageSubData(3553, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0,
                        ScreenTextureState.instance().lastWidth,
                        ScreenTextureState.instance().lastHeight,
                        1);
            }
        } else {
            GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,0,0,ScreenTextureState.instance().lastWidth, ScreenTextureState.instance().lastHeight,0);
        }
    }

    /*@SubscribeEvent
    private void onRenderWorldLast(RenderWorldLastEvent event)
    {


        MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
        if(mainWindow.getWidth() <= 0 || mainWindow.getHeight() <= 0)
            return;

        RenderSystem.bindTexture(this.getTextureId());
        if(mainWindow.getWidth() != this.lastWindowWidth || mainWindow.getHeight() != this.lastWindowHeight)
        {
            // When window resizes the texture needs to be re-initialized and copied, so both are done in the same call
            this.lastWindowWidth = mainWindow.getWidth();
            this.lastWindowHeight = mainWindow.getHeight();
            glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight(), 0);
        }
        else
        {
            // Copy sub-image is faster than copy because the texture does not need to be initialized
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight());
        }
    }*/


    public void SetImageFromOptifine()
    {
        // Yep scopes will never work with shaders
        MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
        // OpenGL will spit out an error (GL_INVALID_VALUE) if the window is minimised (or draw calls stop)
        // It seems just testing the width or height if it's zero is enough to prevent it
        if(mainWindow.getWidth() <= 0 || mainWindow.getHeight() <= 0)
            return;

        RenderSystem.bindTexture(this.textureId);
        if (mainWindow.getWidth() != this.lastWindowWidth || mainWindow.getHeight() != this.lastWindowHeight) {
            // When window resizes the texture needs to be re-initialized and copied, so both are done in the same call
            this.lastWindowWidth = mainWindow.getWidth();
            this.lastWindowHeight = mainWindow.getHeight();
            //glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_GREEN, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight(), 0);
            GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,0,0,mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight(),0);
            //glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight(), 0);
        } else {
            // Copy sub-image is faster than copy because the texture does not need to be initialized
            //glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight());
            GL43.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mainWindow.getFramebufferWidth(), mainWindow.getFramebufferHeight());
        }
    }
}
