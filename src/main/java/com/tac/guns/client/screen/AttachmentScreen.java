package com.tac.guns.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.container.AttachmentContainer;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.IrDeviceItem;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.SideRailItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentScreen extends ContainerScreen<AttachmentContainer> {
    private static final ResourceLocation GUN_GUI_TEXTURES = new ResourceLocation("tac:textures/gui/attachments.png");
    private static final ResourceLocation SCOPE_GUI_TEXTURES = new ResourceLocation("tac:textures/gui/scope_attachments.png");

    private final PlayerInventory playerInventory;
    private final IInventory weaponInventory;

    private boolean showHelp = true;
    private int windowZoom = 14;
    private int windowX, windowY;
    private float windowRotationX, windowRotationY;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX, mouseClickedY;

    public AttachmentScreen(AttachmentContainer screenContainer, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(screenContainer, playerInventory, titleIn);
        this.playerInventory = playerInventory;
        this.weaponInventory = screenContainer.getWeaponInventory();
        this.ySize = 184;
    }

    @Override
    public void tick() {
        super.tick();
        if (SyncedPlayerData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.RELOADING))
            Minecraft.getInstance().displayGuiScreen(null);
        if (this.minecraft != null && this.minecraft.player != null) {
            if (!(this.minecraft.player.getHeldItemMainhand().getItem() instanceof GunItem) &&
                    !(this.minecraft.player.getHeldItemMainhand().getItem() instanceof ScopeItem) &&
                    !(this.minecraft.player.getHeldItemMainhand().getItem() instanceof SideRailItem) &&
                    !(this.minecraft.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem)) {
                Minecraft.getInstance().displayGuiScreen(null);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY); //Render tool tips
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX + 30, (float) this.titleY, 4210752);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;

        if ((this.minecraft.player.getHeldItemMainhand().getItem() instanceof ScopeItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof SideRailItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem))
            RenderUtil.scissor(left + 97, top + 17, 67, 67);
        else
            RenderUtil.scissor(left + 26, top + 17, 123, 70);
        RenderSystem.pushMatrix();
        {
            RenderSystem.translatef(96, 50, 100);
            RenderSystem.translated(this.windowX + (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseX - this.mouseClickedX : 0), 0, 0);
            RenderSystem.translated(0, this.windowY + (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseY - this.mouseClickedY : 0), 0);
            RenderSystem.rotatef(-30F, 1, 0, 0);
            RenderSystem.rotatef(this.windowRotationY - (this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseY - this.mouseClickedY : 0), 1, 0, 0);
            RenderSystem.rotatef(this.windowRotationX + (this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseX - this.mouseClickedX : 0), 0, 1, 0);
            RenderSystem.rotatef(150F, 0, 1, 0);
            RenderSystem.scalef(this.windowZoom / 10F, this.windowZoom / 10F, this.windowZoom / 10F);
            RenderSystem.scalef(90F, -90F, 90F);
            RenderSystem.rotatef(5F, 1, 0, 0);
            RenderSystem.rotatef(90F, 0, 1, 0);

            RenderSystem.enableRescaleNormal();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            IRenderTypeBuffer.Impl buffer = this.minecraft.getRenderTypeBuffers().getBufferSource();
            if ((this.minecraft.player.getHeldItemMainhand().getItem() instanceof GunItem)) {
                matrixStack.translate(0.0, 0.0, -0.4);
                GunRenderingHandler.get().renderWeapon(this.minecraft.player, this.minecraft.player.getHeldItemMainhand(), ItemCameraTransforms.TransformType.GROUND, matrixStack, buffer, 15728880, 0F);
            } else {
                matrixStack.push();
                matrixStack.scale(1.25f, 1.25f, 1.25f);
                GunRenderingHandler.get().renderScope(this.minecraft.player, this.minecraft.player.getHeldItemMainhand(), ItemCameraTransforms.TransformType.FIXED, matrixStack, buffer, 15728880, 0F); // GROUND, matrixStack, buffer, 15728880, 0F);
                matrixStack.pop();
            }
            buffer.finish();

            RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
        }
        RenderSystem.popMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (this.showHelp) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
            minecraft.fontRenderer.drawString(matrixStack, I18n.format("container.tac.attachments.window_help"), 56, 38, 0xFFFFFF);
            RenderSystem.popMatrix();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        if (!(this.minecraft.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem) && !(this.minecraft.player.getHeldItemMainhand().getItem() instanceof SideRailItem) && !(this.minecraft.player.getHeldItemMainhand().getItem() instanceof ScopeItem))
            minecraft.getTextureManager().bindTexture(GUN_GUI_TEXTURES);
        else
            minecraft.getTextureManager().bindTexture(SCOPE_GUI_TEXTURES);

        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.blit(matrixStack, left, top, 0, 0, this.xSize, this.ySize);

        if ((this.minecraft.player.getHeldItemMainhand().getItem() instanceof ScopeItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof SideRailItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem))
            for (int i = 11; i < IAttachment.Type.values().length; i++) {
                if (i == 11 && !this.container.getSlot(i).isEnabled()) {
                    this.blit(matrixStack, left + 70, top + 50 + 18, 176, 16, 16, 16);
                } else if (i == 11 && this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 70, top + 50 + 18, 176, 16, 16, 16);
                }
                if (i == 12 && !this.container.getSlot(i).isEnabled()) {
                    this.blit(matrixStack, left + 40, top + -1 + 18, 176, 0, 16, 16);
                } else if (i == 12 && this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 40, top + -1 + 18, 176, 0, 16, 16);
                }
                if (i == 13 && !this.container.getSlot(i).isEnabled()) {
                    this.blit(matrixStack, left + 10, top + 50 + 18, 176, 32, 16, 16);
                } else if (i == 13 && this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 10, top + 50 + 18, 176, 32, 16, 16);
                }
            }
        else
            for (int i = 0; i < IAttachment.Type.values().length - 7; i++) {
                if (!this.container.getSlot(i).isEnabled()) {
                    if (i > 3)
                        if (i == 5 && this.container.hasExMag())
                            this.blit(matrixStack, left + 155, top + 17 + 18, 176 + 16, 0, 16, 16);
                        else
                            this.blit(matrixStack, left + 155, top + 17 + (i - 4) * 18, 176, 0, 16, 16);
                    else
                        this.blit(matrixStack, left + 5, top + 17 + i * 18, 176, 0, 16, 16);
                } else if (i == 6 && this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 155, top + 17 + 2 * 18, 176, 16 + 8 * 16, 16, 16);
                } else if (i > 3 && this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 155, top + 17 + (i - 4) * 18, 176, 16 + i * 16, 16, 16);
                } else if (this.weaponInventory.getStackInSlot(i).isEmpty()) {
                    this.blit(matrixStack, left + 5, top + 17 + i * 18, 176, 16 + i * 16, 16, 16);
                }
            }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 142, 70)) {
            if (scroll < 0 && this.windowZoom > 0) {
                this.showHelp = false;
                this.windowZoom--;
            } else if (scroll > 0) {
                this.showHelp = false;
                this.windowZoom++;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        if ((this.minecraft.player.getHeldItemMainhand().getItem() instanceof ScopeItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof SideRailItem) ||
                (this.minecraft.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem)) {
            if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 93, startY + 18, 65, 67)) {
                if (!this.mouseGrabbed && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                    this.mouseGrabbed = true;
                    this.mouseGrabbedButton = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? 1 : 0;
                    this.mouseClickedX = (int) mouseX;
                    this.mouseClickedY = (int) mouseY;
                    this.showHelp = false;
                    return true;
                }
            }
        } else {
            if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 126, 70)) {
                if (!this.mouseGrabbed && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                    this.mouseGrabbed = true;
                    this.mouseGrabbedButton = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? 1 : 0;
                    this.mouseClickedX = (int) mouseX;
                    this.mouseClickedY = (int) mouseY;
                    this.showHelp = false;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseGrabbed) {
            if (this.mouseGrabbedButton == 0 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                this.mouseGrabbed = false;
                this.windowX += (mouseX - this.mouseClickedX - 1);
                this.windowY += (mouseY - this.mouseClickedY);
            } else if (mouseGrabbedButton == 1 && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                this.mouseGrabbed = false;
                this.windowRotationX += (mouseX - this.mouseClickedX);
                this.windowRotationY -= (mouseY - this.mouseClickedY);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
