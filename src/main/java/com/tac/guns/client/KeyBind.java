package com.tac.guns.client;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Helps to fix key conflict issue. Adapted solution from FMUM.
 *
 * @see InputHandler
 * @author Giant_Salted_Fish
 */
public final class KeyBind
{
	public static final HashMap< String, KeyBind > REGISTRY = new HashMap<>();
	
	private boolean is_down;
	
	private Input key_code;
	private KeyModifier key_modifier;
	
	private final KeyBinding vanilla_key_bind;
	
	private final LinkedList< Runnable >
		press_callbacks = new LinkedList<>(),
		release_callbacks = new LinkedList<>();
	
	/**
	 * Create a new key binding with default key set to {@link InputMappings#INPUT_INVALID}.
	 */
	KeyBind( String name ) {
		this( name, InputMappings.INPUT_INVALID, KeyModifier.NONE );
	}
	
	/**
	 * Create a new key binding with default key set to given key on keyboard.
	 *
	 * @param key_code
	 *     Must be a keyboard key code. See fields in {@link GLFW} with {@code GLFW_KEY_} prefix.
	 */
	KeyBind( String name, int key_code ) {
		this( name, key_code, KeyModifier.NONE );
	}
	
	/**
	 * Create a new key binding with default key set to given key on keyboard.
	 *
	 * @param key_code
	 *     Must be a keyboard key code. See fields in {@link GLFW} with {@code GLFW_KEY_} prefix.
	 * @param key_modifier Combination key to be used for bounden key.
	 */
	KeyBind( String name, int key_code, KeyModifier key_modifier ) {
		this( name, Type.KEYSYM.getOrMakeInput( key_code ), key_modifier );
	}
	
	/**
	 * Can be used to create key bindings with default key set to mouse buttons.
	 *
	 * @param key_code_type Use {@link Type#MOUSE} if you want to bound to mouse button.
	 * @param key_code See fields in {@link GLFW} with {@code GLFW_MOUSE_BUTTON_} prefix.
	 */
	KeyBind( String name, Type key_code_type, int key_code ) {
		this( name, key_code_type.getOrMakeInput( key_code ), KeyModifier.NONE );
	}
	
	KeyBind( String name, Input key_code, KeyModifier key_modifier )
	{
		REGISTRY.put( name, this );
		
		this.is_down = false;
		this.key_code = key_code;
		this.key_modifier = key_modifier;
		this.vanilla_key_bind = new KeyBinding(
			name, GunConflictContext.IN_GAME_HOLDING_WEAPON,
			key_modifier, key_code, "key.categories.tac"
		);
	}
	
	public String name() {
		return this.vanilla_key_bind.getKeyDescription();
	}
	
	public boolean isDown() {
		return this.is_down;
	}
	
	public Input keyCode() {
		return this.key_code;
	}
	
	public KeyModifier keyModifier() {
		return this.key_modifier;
	}
	
	public void setKeyCodeAndModifier( Input key_code, KeyModifier key_modifier )
	{
		this.key_code = key_code;
		this.key_modifier = key_modifier;
	}
	
	public void addPressCallback( Runnable callback ) {
		this.press_callbacks.add( callback );
	}
	
	public void addReleaseCallback( Runnable callback ) {
		this.release_callbacks.add( callback );
	}
	
	void activeUpdate( boolean is_down )
	{
		if ( this.is_down != is_down )
		{
			this.is_down = is_down;
			( is_down ? this.press_callbacks : this.release_callbacks ).forEach( Runnable::run );
		}
	}
	
	void inactiveUpdate( boolean is_down )
	{
		if ( !is_down && this.is_down )
		{
			this.is_down = false;
			this.release_callbacks.forEach( Runnable::run );
		}
	}
	
	void restoreVanillaKeyBind() {
		this.vanilla_key_bind.setKeyModifierAndCode( this.key_modifier, this.key_code );
	}
	
	boolean clearVanillaKeyBind()
	{
		final Input key_code = this.vanilla_key_bind.getKey();
		final KeyModifier key_modifier = this.vanilla_key_bind.getKeyModifier();
		final boolean is_key_bind_changed
			= this.key_code != key_code || this.key_modifier != key_modifier;
		if ( is_key_bind_changed )
		{
			this.key_code = key_code;
			this.key_modifier = key_modifier;
		}
		return is_key_bind_changed;
	}
	
	void selfRegis() {
		ClientRegistry.registerKeyBinding( this.vanilla_key_bind );
	}
}
