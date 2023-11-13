package com.tac.guns.client.settings;

import com.mojang.blaze3d.platform.Window;
import com.tac.guns.Config;
import com.tac.guns.client.handler.CrosshairHandler;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunOptions
{

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0#");

    public static final ProgressOption ADS_SENSITIVITY = new GunSliderPercentageOption("tac.options.adsSensitivity", 0.0, 2.0, 0.01F, gameSettings -> {
        return Config.CLIENT.controls.aimDownSightSensitivity.get();
    }, (gameSettings, value) -> {
        Config.CLIENT.controls.aimDownSightSensitivity.set(Mth.clamp(value, 0.0, 2.0));
        Config.saveClientConfig();
    }, (gameSettings, option) -> {
        double adsSensitivity = Config.CLIENT.controls.aimDownSightSensitivity.get();
        return new TranslatableComponent("tac.options.adsSensitivity.format", FORMAT.format(adsSensitivity));
    });

    public static final CycleOption<Boolean> DOUBLE_RENDER_EXIST = CycleOption.createOnOff("tac.options.doubleRender", (settings) -> {
        return Config.CLIENT.display.scopeDoubleRender.get();
    }, (settings, function, value) -> {
        Config.CLIENT.display.scopeDoubleRender.set(value);
        Config.saveClientConfig();
    });

    public static final CycleOption<Boolean> HOLD_TO_AIM = CycleOption.createOnOff("tac.options.holdToAim", (settings) -> {
        return Config.CLIENT.controls.holdToAim.get();
    }, (settings, function, value) -> {
        Config.CLIENT.controls.holdToAim.set(value);
        Config.saveClientConfig();
    });

    public static final CycleOption<Boolean> ALLOW_LEVER = CycleOption.createOnOff("tac.options.allowLever", (settings) -> {
        return Config.CLIENT.rightClickUse.allowLever.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowLever.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_BUTTON = CycleOption.createOnOff("tac.options.allowButton", (settings) -> {
        return Config.CLIENT.rightClickUse.allowButton.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowButton.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_DOORS = CycleOption.createOnOff("tac.options.allowDoors", (settings) -> {
        return Config.CLIENT.rightClickUse.allowDoors.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowDoors.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_TRAP_DOORS = CycleOption.createOnOff("tac.options.allowTrapDoors", (settings) -> {
        return Config.CLIENT.rightClickUse.allowTrapDoors.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowTrapDoors.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_CRAFTING_TABLE = CycleOption.createOnOff("tac.options.allowCraftingTable", (settings) -> {
        return Config.CLIENT.rightClickUse.allowCraftingTable.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowCraftingTable.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_CHESTS = CycleOption.createOnOff("tac.options.allowChests", (settings) -> {
        return Config.CLIENT.rightClickUse.allowChests.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowChests.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> ALLOW_FENCE_GATES = CycleOption.createOnOff("tac.options.allowFenceGates", (settings) -> {
        return Config.CLIENT.rightClickUse.allowFenceGates.get();
    }, (settings, function, value) -> {
        Config.CLIENT.rightClickUse.allowFenceGates.set(value);
        Config.saveClientConfig();
    });

   /* public static final CycleOption<Boolean> BURST_MECH = CycleOption.createOnOff("tac.options.burstPress", (settings) -> {
        return Config.CLIENT.controls.burstPress.get();
    }, (settings, value) -> {
        Config.CLIENT.controls.burstPress.set(value);
        Config.saveClientConfig();
    });
    // this.minecraft.displayGuiScreen
*/


    /*private static Object CrosshairHandler;
    public static final Option CROSSHAIR = new GunListOption<>("tac.options.crosshair", () -> {
        return CrosshairHandler.get().getRegisteredCrosshairs();
    }, () -> {
        return ResourceLocation.tryParse(Config.CLIENT.display.crosshair.get());
    }, (value) -> {
        Config.CLIENT.display.crosshair.set(value toString());
        Config.saveClientConfig();
        CrosshairHandler.get().setCrosshair(value);
    }, (value) -> {
        ResourceLocation id = value.getLocation();
        return new TranslatableComponent(id.getNamespace() + ".crosshair." + id.getPath());
    }).setRenderer((button, matrixStack, partialTicks) -> {
        matrixStack.pushPose();
        matrixStack.translate(button.x, button.y, 0);
        matrixStack.translate(button.getWidth() + 2, 2, 0);
        Crosshair crosshair = CrosshairHandler.get().getCurrentCrosshair();
        if(crosshair != null)
        {
            if(crosshair.isDefault())
            {
                Minecraft mc = Minecraft.getInstance();
                mc.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
                GuiComponent.blit(matrixStack, (16 - 15) / 2, (16 - 15) / 2, 0, 0, 0, 15, 15, 256, 256);
            }
            else
            {
                crosshair.render(Minecraft.getInstance(), matrixStack, 16, 16, partialTicks);
            }
        }
        matrixStack.popPose();
    });

    public static final CycleOption<Boolean> SHOW_FPS_TRAILS_EXIST = CycleOption.createOnOff("tac.options.showFirstPersonBulletTrails", (settings) -> {
        return Config.CLIENT.display.showFirstPersonBulletTrails.get();
    }, (settings, value) -> {
        Config.CLIENT.display.showFirstPersonBulletTrails.set(value);
        Config.saveClientConfig();
    });*/






    /*
    public static final CycleOption<Boolean> REDDOT_SQUISH_EXIST = CycleOption.createOnOff("tac.options.reddotSquish", (settings) -> {
        return Config.CLIENT.display.redDotSquishUpdate.get();
    }, (settings, value) -> {
        Config.CLIENT.display.redDotSquishUpdate.set(value);
        Config.saveClientConfig();
    });

    public static final CycleOption<Boolean> FIREMODE_EXIST = CycleOption.createOnOff("tac.options.firemodeExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.set(value);
        Config.saveClientConfig();
    });
    //Firemode positioning

    public static final ProgressOption X_FIREMODE_POS = new GunSliderPercentageOption("tac.options.xFiremodePos", -500, 500, 0.001F,
    gameSettings ->
    {
        return Config.CLIENT.weaponGUI.weaponFireMode.x.get();
        //return Config.CLIENT.controls.aimDownSightSensitivity.get();
    },
    (gameSettings, value) ->
    {
        Config.CLIENT.weaponGUI.weaponFireMode.x.set(Mth.clamp(value, -500, 500));
        Config.saveClientConfig();
    },
    (gameSettings, option) -> {
        double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.x.get();
        return new TranslatableComponent("tac.options.xFiremodePos.format", FORMAT.format(adsSensitivity));
    });
    public static final ProgressOption Y_FIREMODE_POS = new GunSliderPercentageOption("tac.options.yFiremodePos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponFireMode.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponFireMode.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.y.get();
                return new TranslatableComponent("tac.options.yFiremodePos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_FIREMODE_POS = new GunSliderPercentageOption("tac.options.sizeFiremodePos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.get();
                return new TranslatableComponent("tac.options.sizeFiremodePos.format", FORMAT.format(adsSensitivity));
            });

    //AmmoCounter positioning
    public static final CycleOption<Boolean> AMMOCOUNTER_EXIST = CycleOption.createOnOff("tac.options.ammoCounterExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.xAmmoCounterPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get();
                return new TranslatableComponent("tac.options.xAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.yAmmoCounterPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                return new TranslatableComponent("tac.options.yAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.sizeAmmoCounterPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.get();
                return new TranslatableComponent("tac.options.sizeAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });

    //WeaponIcon positioning
    public static final CycleOption<Boolean> WeaponIcon_EXIST = CycleOption.createOnOff("tac.options.iconExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_Icon_POS = new GunSliderPercentageOption("tac.options.xIconPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponTypeIcon.x.get();
                return new TranslatableComponent("tac.options.xIconPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_Icon_POS = new GunSliderPercentageOption("tac.options.yIconPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                return new TranslatableComponent("tac.options.yIconPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_Icon_POS = new GunSliderPercentageOption("tac.options.sizeIconPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.get();
                return new TranslatableComponent("tac.options.sizeIconPos.format", FORMAT.format(adsSensitivity));
            });

    //WeaponIcon positioning
    public static final CycleOption<Boolean> ReloadBar_EXIST = CycleOption.createOnOff("tac.options.reloadBarExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponReloadTimer.showWeaponReloadTimer.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponReloadTimer.showWeaponReloadTimer.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_ReloadBar_POS = new GunSliderPercentageOption("tac.options.xReloadBarPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.x.get();
                return new TranslatableComponent("tac.options.xReloadBarPos.format", FORMAT.format(adsSensitivity));
            });

    public static final ProgressOption Fire_Volume = new GunSliderPercentageOption("tac.options.weaponsVolume", 0f, 1f, 0.01F,
            gameSettings ->
            {
                return Config.CLIENT.sounds.weaponsVolume.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.sounds.weaponsVolume.set(Mth.clamp(value, 0f, 1f));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.sounds.weaponsVolume.get();
                return new TranslatableComponent("tac.options.weaponsVolume.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_ReloadBar_POS = new GunSliderPercentageOption("tac.options.yReloadBarPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.y.get();
                return new TranslatableComponent("tac.options.yReloadBarPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_ReloadBar_POS = new GunSliderPercentageOption("tac.options.sizeReloadBarPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.get();
                return new TranslatableComponent("tac.options.sizeReloadBarPos.format", FORMAT.format(adsSensitivity));
            });
    */
}