package com.tac.guns.client;

import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import static com.tac.guns.client.InputHandler.addNormal;
import static com.tac.guns.client.InputHandler.addGlobal;
import static com.tac.guns.client.InputHandler.addDebug;

@OnlyIn( Dist.CLIENT )
public final class Keys
{
	public static final KeyBind
		PULL_TRIGGER = addGlobal( new KeyBind( "key.tac.pull_trigger", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT ) ),
		AIM_HOLD = addGlobal( new KeyBind( "key.tac.aim_hold", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT ) ),
		AIM_TOGGLE = addGlobal( new KeyBind( "key.tac.aim_toggle" ) );
	
	public static KeyBind
		RELOAD = addNormal( new KeyBind( "key.tac.reload", GLFW.GLFW_KEY_R ) ),
		UNLOAD = addNormal( new KeyBind( "key.tac.unload", GLFW.GLFW_KEY_R, KeyModifier.CONTROL ) ),
		ATTACHMENTS = addNormal( new KeyBind( "key.tac.attachments", GLFW.GLFW_KEY_Z ) ),
		ARMOR_REPAIRING = addNormal( new KeyBind( "key.tac.armor_repairing", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT ) ),
		
		FIRE_SELECT = addNormal( new KeyBind( "key.tac.fireSelect", GLFW.GLFW_KEY_G ) ),
		INSPECT = addNormal( new KeyBind( "key.tac.inspect", GLFW.GLFW_KEY_H ) ),
		SIGHT_SWITCH = addNormal( new KeyBind( "key.tac.sight_switch", GLFW.GLFW_KEY_V ) ),
		ACTIVATE_SIDE_RAIL = addNormal( new KeyBind( "key.tac.activeSideRail", GLFW.GLFW_KEY_B ) ),
		MORE_INFO_HOLD = addNormal( new KeyBind( "key.tac.moreInfoHold", GLFW.GLFW_KEY_LEFT_SHIFT ) );
	
	public static final KeyBind
		SHIFTY = addDebug( new KeyBind( "key.tac.ss", GLFW.GLFW_KEY_LEFT_SHIFT ) ),
		CONTROLLY = addDebug( new KeyBind( "key.tac.cc", GLFW.GLFW_KEY_LEFT_CONTROL ) ),
		ALTY = addDebug( new KeyBind( "key.tac.aa", GLFW.GLFW_KEY_LEFT_ALT ) ),
		SHIFTYR = addDebug( new KeyBind( "key.tac.ssr", GLFW.GLFW_KEY_RIGHT_SHIFT ) ),
		CONTROLLYR = addDebug( new KeyBind( "key.tac.ccr", GLFW.GLFW_KEY_RIGHT_CONTROL ) ),
		ALTYR = addDebug( new KeyBind( "key.tac.aar", GLFW.GLFW_KEY_RIGHT_ALT ) ),
		SIZE_OPT = addDebug( new KeyBind( "key.tac.sizer", GLFW.GLFW_KEY_PERIOD ) ),
	
	P = addDebug( new KeyBind( "key.tac.p", GLFW.GLFW_KEY_P ) ),
		L = addDebug( new KeyBind( "key.tac.l", GLFW.GLFW_KEY_L ) ),
		O = addDebug( new KeyBind( "key.tac.o", GLFW.GLFW_KEY_O ) ),
		K = addDebug( new KeyBind( "key.tac.k", GLFW.GLFW_KEY_K ) ),
		M = addDebug( new KeyBind( "key.tac.m", GLFW.GLFW_KEY_M ) ),
		I = addDebug( new KeyBind( "key.tac.i", GLFW.GLFW_KEY_I ) ),
		J = addDebug( new KeyBind( "key.tac.j", GLFW.GLFW_KEY_J ) ),
		N = addDebug( new KeyBind( "key.tac.n", GLFW.GLFW_KEY_N ) ),
		
		UP = addDebug( new KeyBind( "key.tac.bbb", GLFW.GLFW_KEY_UP ) ),
		RIGHT = addDebug( new KeyBind( "key.tac.vvv", GLFW.GLFW_KEY_RIGHT ) ),
		LEFT = addDebug( new KeyBind( "key.tac.ccc", GLFW.GLFW_KEY_LEFT ) ),
		DOWN = addDebug( new KeyBind( "key.tac.zzz", GLFW.GLFW_KEY_DOWN ) );
	
	private Keys() { }
}
