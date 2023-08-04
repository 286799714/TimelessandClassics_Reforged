package com.tac.guns.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import static com.tac.guns.client.InputHandler._addNormal;
import static com.tac.guns.client.InputHandler._addGlobal;
import static com.tac.guns.client.InputHandler._addDebug;

@OnlyIn( Dist.CLIENT )
public final class Keys
{
	public static final KeyBind
		PULL_TRIGGER = _addGlobal( new KeyBind( "key.tac.pull_trigger", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT ) ),
		AIM_HOLD = _addGlobal( new KeyBind( "key.tac.aim_hold", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT ) ),
		AIM_TOGGLE = _addGlobal( new KeyBind( "key.tac.aim_toggle" ) );
	
	public static KeyBind
		RELOAD = _addNormal( new KeyBind( "key.tac.reload", GLFW.GLFW_KEY_R ) ),
		UNLOAD = _addNormal( new KeyBind( "key.tac.unload", GLFW.GLFW_KEY_R, KeyModifier.CONTROL ) ),
		ATTACHMENTS = _addNormal( new KeyBind( "key.tac.attachments", GLFW.GLFW_KEY_Z ) ),
		ARMOR_REPAIRING = _addNormal( new KeyBind( "key.tac.armor_repairing", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT ) ),
		
		FIRE_SELECT = _addNormal( new KeyBind( "key.tac.fireSelect", GLFW.GLFW_KEY_G ) ),
		INSPECT = _addNormal( new KeyBind( "key.tac.inspect", GLFW.GLFW_KEY_H ) ),
		SIGHT_SWITCH = _addNormal( new KeyBind( "key.tac.sight_switch", GLFW.GLFW_KEY_V ) ),
		ACTIVATE_SIDE_RAIL = _addNormal( new KeyBind( "key.tac.activeSideRail", GLFW.GLFW_KEY_B ) );
	
	public static final KeyBind
		SHIFTY = _addDebug( new KeyBind( "key.tac.ss", GLFW.GLFW_KEY_LEFT_SHIFT ) ),
		CONTROLLY = _addDebug( new KeyBind( "key.tac.cc", GLFW.GLFW_KEY_LEFT_CONTROL ) ),
		ALTY = _addDebug( new KeyBind( "key.tac.aa", GLFW.GLFW_KEY_LEFT_ALT ) ),
		SHIFTYR = _addDebug( new KeyBind( "key.tac.ssr", GLFW.GLFW_KEY_RIGHT_SHIFT ) ),
		CONTROLLYR = _addDebug( new KeyBind( "key.tac.ccr", GLFW.GLFW_KEY_RIGHT_CONTROL ) ),
		ALTYR = _addDebug( new KeyBind( "key.tac.aar", GLFW.GLFW_KEY_RIGHT_ALT ) ),
		SIZE_OPT = _addDebug( new KeyBind( "key.tac.sizer", GLFW.GLFW_KEY_PERIOD ) ),
		
		P = _addDebug( new KeyBind( "key.tac.p", GLFW.GLFW_KEY_P ) ),
		L = _addDebug( new KeyBind( "key.tac.l", GLFW.GLFW_KEY_L ) ),
		O = _addDebug( new KeyBind( "key.tac.o", GLFW.GLFW_KEY_O ) ),
		K = _addDebug( new KeyBind( "key.tac.k", GLFW.GLFW_KEY_K ) ),
		M = _addDebug( new KeyBind( "key.tac.m", GLFW.GLFW_KEY_M ) ),
		I = _addDebug( new KeyBind( "key.tac.i", GLFW.GLFW_KEY_I ) ),
		J = _addDebug( new KeyBind( "key.tac.j", GLFW.GLFW_KEY_J ) ),
		N = _addDebug( new KeyBind( "key.tac.n", GLFW.GLFW_KEY_N ) ),
		
		UP = _addDebug( new KeyBind( "key.tac.bbb", GLFW.GLFW_KEY_UP ) ),
		RIGHT = _addDebug( new KeyBind( "key.tac.vvv", GLFW.GLFW_KEY_RIGHT ) ),
		LEFT = _addDebug( new KeyBind( "key.tac.ccc", GLFW.GLFW_KEY_LEFT ) ),
		DOWN = _addDebug( new KeyBind( "key.tac.zzz", GLFW.GLFW_KEY_DOWN ) );
	
	public static final KeyBind MORE_INFO_HOLD = new KeyBind( "", GLFW.GLFW_KEY_LEFT_SHIFT )
	{
		@Override
		protected KeyBinding _createVanillaShadowKeyBinding(
			String name, Input key_code, KeyModifier key_modifier
		) { return Minecraft.getInstance().gameSettings.keyBindSneak; }
		
		@Override
		public String name() {
			return "key.tac.moreInfoHold";
		}
		
		@Override
		public Input keyCode() {
			return this.vanilla_key_bind.getKey();
		}
		
		@Override
		public KeyModifier keyModifier()
		{
			return SP_KEY_2_MODIFIER.getOrDefault(
				this.keyCode(), this.vanilla_key_bind.getKeyModifier() );
		}
		
		@Override
		public void setKeyCodeAndModifier( Input key_code, KeyModifier key_modifier ) { }
		
		@Override
		protected void _restoreVanillaKeyBind() { }
		
		@Override
		protected boolean _clearVanillaKeyBind()
		{
			// Always fetch from sneak key, so never need to save change.
			final boolean is_key_bind_changed = false;
			return is_key_bind_changed;
		}
		
		@Override
		protected void _selfRegis() { }
	};
	
	private Keys() { }
}
