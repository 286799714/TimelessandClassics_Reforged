package com.tac.guns.client.screen.ammo_screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.inventory.gear.armor.implementations.R1_RigContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class R1_AmmoScreen extends AbstractContainerScreen<R1_RigContainer> implements MenuAccess<R1_RigContainer> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int rows;

    public R1_AmmoScreen(R1_RigContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.rows = container.getNumRows();
        this.imageHeight = 114 + rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        // Draw for ammo pack, current issue is the number of slots not being drawn correctly, we can't cut this off either due to the background, get design team to create alternative off generic_54.png baseline
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, (this.rows) * 18 + 17);
        this.blit(matrixStack, i, j + (this.rows) * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}