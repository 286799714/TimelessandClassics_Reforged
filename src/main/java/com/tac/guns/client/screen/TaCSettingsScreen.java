package com.tac.guns.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.Config;
import com.tac.guns.client.settings.GunOptions;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.stream.Stream;


@OnlyIn(Dist.CLIENT)
public class TaCSettingsScreen extends OptionsSubScreen {
    private OptionsList optionsList;
    private static final Option[] OPTIONS = new Option[]
            {
                    GunOptions.DOUBLE_RENDER_EXIST,
                    GunOptions.Fire_Volume,
                    GunOptions.ALLOW_LEVER,
                    GunOptions.ALLOW_BUTTON,
                    GunOptions.ALLOW_FENCE_GATES,
                    GunOptions.ALLOW_CHESTS,
                    GunOptions.ALLOW_DOORS,
                    GunOptions.ALLOW_CRAFTING_TABLE,
                    GunOptions.ALLOW_TRAP_DOORS,
                    GunOptions.FIREMODE_EXIST,
                    GunOptions.X_FIREMODE_POS,
                    GunOptions.Y_FIREMODE_POS,
                    GunOptions.SIZE_FIREMODE_POS,
                    GunOptions.ReloadBar_EXIST,
                    GunOptions.X_ReloadBar_POS,
                    GunOptions.Y_ReloadBar_POS,
                    GunOptions.SIZE_ReloadBar_POS,
                    GunOptions.AMMOCOUNTER_EXIST,
                    GunOptions.X_AMMOCOUNTER_POS,
                    GunOptions.Y_AMMOCOUNTER_POS,
                    GunOptions.SIZE_AMMOCOUNTER_POS,
                    GunOptions.WeaponIcon_EXIST,
                    GunOptions.X_Icon_POS,
                    GunOptions.Y_Icon_POS,
                    GunOptions.SIZE_Icon_POS};

    public TaCSettingsScreen(Screen p_i225929_1_, Options p_i225929_2_) {
        super(p_i225929_1_, p_i225929_2_, new TranslatableComponent("tac.options.screenFormat"));
    }

    protected void init() {

        this.optionsList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        if (InputConstants.isRawMouseInputSupported()) {
            this.optionsList.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(new Option[]{})).toArray((p_96225_) -> {
                return new Option[p_96225_];
            }));
        } else {
            this.optionsList.addSmall(OPTIONS);
        }
        this.addWidget(this.optionsList);
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (p_96232_) -> {
            this.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    @Override
    public void tick() {
        super.tick();
        Config.saveClientConfig();
    }
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.optionsList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
