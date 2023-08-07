package com.tac.guns.client;

import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.BiConsumer;

/**
 * Helps to fix key conflict issue. Adapted solution from FMUM.
 *
 * @see InputHandler
 * @author Giant_Salted_Fish
 */
public class KeyBind
{
	public static final HashMap< String, KeyBind > REGISTRY = new HashMap<>();
	
	/**
	 * To handle the special cases, for example, binding ctrl to a key bind.
	 */
	static final HashMap< Input, KeyModifier > SP_KEY_2_MODIFIER = new HashMap<>();
	static
	{
		final BiConsumer< Integer, KeyModifier > add = ( key_code, key_modifier )
			-> SP_KEY_2_MODIFIER.put( Type.KEYSYM.getOrMakeInput( key_code ), key_modifier );
		add.accept( GLFW.GLFW_KEY_LEFT_CONTROL, KeyModifier.CONTROL );
		add.accept( GLFW.GLFW_KEY_RIGHT_CONTROL, KeyModifier.CONTROL );
		add.accept( GLFW.GLFW_KEY_LEFT_SHIFT , KeyModifier.SHIFT );
		add.accept( GLFW.GLFW_KEY_RIGHT_SHIFT , KeyModifier.SHIFT );
		add.accept( GLFW.GLFW_KEY_LEFT_ALT, KeyModifier.ALT );
		add.accept( GLFW.GLFW_KEY_RIGHT_ALT, KeyModifier.ALT );
	}
	
	protected final KeyBinding vanilla_key_bind;
	
	protected Input key_code;
	protected KeyModifier key_modifier;
	
	private boolean is_down;
	
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
		this.setKeyCodeAndModifier( key_code, key_modifier );
		this.vanilla_key_bind = this._createVanillaShadowKeyBinding( name, key_code, key_modifier );
	}
	
	protected KeyBinding _createVanillaShadowKeyBinding(
		String name, Input key_code, KeyModifier key_modifier
	) {
		final KeyBinding vanilla_key_bind = new KeyBinding(
			name, GunConflictContext.IN_GAME_HOLDING_WEAPON,
			key_modifier, key_code, "key.categories.tac"
		);
		
		// Clear key bind to avoid conflict.
		vanilla_key_bind.setKeyModifierAndCode( KeyModifier.NONE, InputMappings.INPUT_INVALID );
		return vanilla_key_bind;
	}
	
	public String name() {
		return this.vanilla_key_bind.getKeyDescription();
	}
	
	public final boolean isDown() {
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
		this.key_modifier = SP_KEY_2_MODIFIER.getOrDefault( key_code, key_modifier );
	}
	
	/**
	 * This will be called once the key is pressed. It will never be called again until the key is
	 * released and pressed again.
	 *
	 * @see #addReleaseCallback(Runnable)
	 */
	public final void addPressCallback( Runnable callback ) {
		this.press_callbacks.add( callback );
	}
	
	/**
	 * This will be called once the key is released.
	 *
	 * @see #addPressCallback(Runnable)
	 */
	public final void addReleaseCallback( Runnable callback ) {
		this.release_callbacks.add( callback );
	}

	public final ITextComponent getBoundenKeyPrompt() {
		return this.key_modifier.getCombinedName( this.key_code, this.key_code::func_237520_d_ );
	}
	
	final void _activeUpdate( boolean is_down )
	{
		if ( this.is_down != is_down )
		{
			this.is_down = is_down;
			( is_down ? this.press_callbacks : this.release_callbacks ).forEach( Runnable::run );
		}
	}
	
	final void _inactiveUpdate( boolean is_down )
	{
		if ( !is_down && this.is_down )
		{
			this.is_down = false;
			this.release_callbacks.forEach( Runnable::run );
		}
	}
	
	protected void _restoreVanillaKeyBind() {
		this.vanilla_key_bind.setKeyModifierAndCode( this.key_modifier, this.key_code );
	}
	
	protected boolean _clearVanillaKeyBind()
	{
		final Input key_code = this.vanilla_key_bind.getKey();
		final KeyModifier key_modifier = this.vanilla_key_bind.getKeyModifier();
		final boolean is_key_bind_changed
			= this.key_code != key_code || this.key_modifier != key_modifier;
		if ( is_key_bind_changed ) {
			this.setKeyCodeAndModifier( key_code, key_modifier );
		}
		
		// Do not forget to clear vanilla key binding.
		this.vanilla_key_bind.setKeyModifierAndCode(
			KeyModifier.NONE, InputMappings.INPUT_INVALID );
		return is_key_bind_changed;
	}
	
	protected void _selfRegis() {
		ClientRegistry.registerKeyBinding( this.vanilla_key_bind );
	}
}
