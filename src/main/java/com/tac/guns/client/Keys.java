package com.tac.guns.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.tac.guns.Config;
import gsf.kbp.client.api.PatchedKeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

import java.util.Collections;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class Keys
{
    private static final class Builder extends PatchedKeyBinding.Builder
    {
        private Builder( String description )
        {
            super( description );
            
            this.category = "key.categories.tac";
            this.conflict_context = GunConflictContext.IN_GAME_HOLDING_WEAPON;
        }
    }
    
    private static class MouseKeyBinding extends PatchedKeyBinding
    {
        private MouseKeyBinding( String description, Key mouse_button )
        {
            super(
                description,
                GunConflictContext.IN_GAME_HOLDING_WEAPON,
                mouse_button,
                Collections.emptySet(),
                "key.categories.tac"
            );
        }
        
        @Override
        public boolean same( KeyMapping mapping )
        {
            // No conflict with vanilla attack and item use key binding.
            final Options settings = Minecraft.getInstance().options;
            return(
                mapping != settings.keyAttack
                && mapping != settings.keyUse
                && super.same( mapping )
            );
        }
    }
    
    public static final PatchedKeyBinding
        PULL_TRIGGER = new MouseKeyBinding(
            "key.tac.pull_trigger",
            Type.MOUSE.getOrCreate( InputConstants.MOUSE_BUTTON_LEFT )
        ),
        AIM_TOGGLE = new MouseKeyBinding(
            "key.tac.aim_toggle",
            InputConstants.UNKNOWN
        ) {
            @Override
            public void setKeyAndCombinations( Key key, Set< Key > combinations )
            {
                if ( key != InputConstants.UNKNOWN ) {
                    AIM_HOLD.setKeyAndCombinations( InputConstants.UNKNOWN, Collections.emptySet() );
                }
                
                super.setKeyAndCombinations( key, combinations );
            }
        },
        AIM_HOLD = new MouseKeyBinding(
            "key.tac.aim_hold",
            Type.MOUSE.getOrCreate( InputConstants.MOUSE_BUTTON_RIGHT )
        ) {
            @Override
            public void setKeyAndCombinations( Key key, Set< Key > combinations )
            {
                if ( key != InputConstants.UNKNOWN ) {
                    AIM_TOGGLE.setKeyAndCombinations( InputConstants.UNKNOWN, Collections.emptySet() );
                }
                
                super.setKeyAndCombinations( key, combinations );
            }
        };
    static
    {
        ClientRegistry.registerKeyBinding( PULL_TRIGGER );
        ClientRegistry.registerKeyBinding( AIM_HOLD );
        ClientRegistry.registerKeyBinding( AIM_TOGGLE );
    }
    
    public static final PatchedKeyBinding
        RELOAD = new Builder( "key.tac.reload" ).withKeyboardKey( InputConstants.KEY_R ).buildAndRegis(),
        UNLOAD = new Builder( "key.tac.unload" ).withKeyboardKey( InputConstants.KEY_R ).withKeyboardCombinations( InputConstants.KEY_LALT ).buildAndRegis(),
        ATTACHMENTS = new Builder( "key.tac.attachments" ).withKeyboardKey( InputConstants.KEY_Z ).buildAndRegis(),
        FIRE_SELECT = new Builder( "key.tac.fireSelect" ).withKeyboardKey( InputConstants.KEY_G ).buildAndRegis(),
        INSPECT = new Builder( "key.tac.inspect" ).withKeyboardKey( InputConstants.KEY_H ).buildAndRegis(),
        SIGHT_SWITCH = new Builder( "key.tac.sight_switch" ).withKeyboardKey( InputConstants.KEY_V ).buildAndRegis(),
        ACTIVATE_SIDE_RAIL = new Builder( "key.tac.activeSideRail" ).withKeyboardKey( InputConstants.KEY_B ).buildAndRegis(),
        
        ARMOR_REPAIRING = new Builder( "key.tac.armor_repairing" ).withKeyboardKey( InputConstants.KEY_K ).buildAndRegis();
    
    public static final PatchedKeyBinding MORE_INFO_HOLD =
        new Builder( "key.tac.more_info_hold" )
            .withKeyboardKey( InputConstants.KEY_LSHIFT )
            .withConflictContext( KeyConflictContext.GUI )
            .buildAndRegis();
    
    public static PatchedKeyBinding
        SHIFTY = null,
        CONTROLLY = null,
        ALTY = null,
        SHIFTYR = null,
        CONTROLLYR = null,
        ALTYR = null,
        SIZE_OPT = null,
        P = null,
        L = null,
        O = null,
        K = null,
        M = null,
        I = null,
        J = null,
        N = null,
        UP = null,
        RIGHT = null,
        LEFT = null,
        DOWN = null;
    
    static
    {
        if ( Config.COMMON.development.enableTDev.get() )
        {
            SHIFTY = new Builder( "key.tac.ss" ).withKeyboardKey( InputConstants.KEY_LSHIFT ).buildAndRegis();
            CONTROLLY = new Builder( "key.tac.cc" ).withKeyboardKey( InputConstants.KEY_LCONTROL ).buildAndRegis();
            ALTY = new Builder( "key.tac.aa" ).withKeyboardKey( InputConstants.KEY_LALT ).buildAndRegis();
            SHIFTYR = new Builder( "key.tac.ssr" ).withKeyboardKey( InputConstants.KEY_RSHIFT ).buildAndRegis();
            CONTROLLYR = new Builder( "key.tac.ccr" ).withKeyboardKey( InputConstants.KEY_RCONTROL ).buildAndRegis();
            ALTYR = new Builder("key.tac.aar" ).withKeyboardKey( InputConstants.KEY_RALT ).buildAndRegis();
            SIZE_OPT = new Builder( "key.tac.sizer" ).withKeyboardKey( InputConstants.KEY_PERIOD ).buildAndRegis();
            P = new Builder( "key.tac.p" ).withKeyboardKey( InputConstants.KEY_P ).buildAndRegis();
            L = new Builder( "key.tac.l" ).withKeyboardKey( InputConstants.KEY_L ).buildAndRegis();
            O = new Builder( "key.tac.o" ).withKeyboardKey( InputConstants.KEY_O ).buildAndRegis();
            K = new Builder( "key.tac.k" ).withKeyboardKey( InputConstants.KEY_K ).buildAndRegis();
            M = new Builder( "key.tac.m" ).withKeyboardKey( InputConstants.KEY_M ).buildAndRegis();
            I = new Builder( "key.tac.i" ).withKeyboardKey( InputConstants.KEY_I ).buildAndRegis();
            J = new Builder( "key.tac.j" ).withKeyboardKey( InputConstants.KEY_J ).buildAndRegis();
            N = new Builder( "key.tac.n" ).withKeyboardKey( InputConstants.KEY_N ).buildAndRegis();
            UP = new Builder( "key.tac.bbb" ).withKeyboardKey( InputConstants.KEY_UP ).buildAndRegis();
            RIGHT = new Builder( "key.tac.vvv" ).withKeyboardKey( InputConstants.KEY_RIGHT ).buildAndRegis();
            LEFT = new Builder( "key.tac.ccc" ).withKeyboardKey( InputConstants.KEY_LEFT ).buildAndRegis();
            DOWN = new Builder( "key.tac.zzz" ).withKeyboardKey( InputConstants.KEY_DOWN ).buildAndRegis();
        }
    }
    
    private Keys() { }
}
