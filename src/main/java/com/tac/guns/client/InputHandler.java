package com.tac.guns.client;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

@OnlyIn( Dist.CLIENT )
@EventBusSubscriber( modid = Reference.MOD_ID, value = Dist.CLIENT )
public final class InputHandler
{
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	
	private static final ArrayList< KeyBind >
		GLOBAL_KEYS = new ArrayList<>(),
		NORMAL_KEYS = new ArrayList<>(),
		DEBUG_KEYS = new ArrayList<>();
	
	private static final HashMultimap< Input, KeyBind >
		GLOBAL_TABLE = HashMultimap.create(),
		NORMAL_TABLE = HashMultimap.create(),
		CTRL_TABLE = HashMultimap.create(),
		SHIFT_TABLE = HashMultimap.create(),
		ALT_TABLE = HashMultimap.create();
	
	private static final HashMap< KeyModifier, HashMultimap< Input, KeyBind > >
		MODIFIER_2_UPDATE_TABLE = new HashMap<>();
	static
	{
		MODIFIER_2_UPDATE_TABLE.put( KeyModifier.NONE, NORMAL_TABLE );
		MODIFIER_2_UPDATE_TABLE.put( KeyModifier.CONTROL, CTRL_TABLE );
		MODIFIER_2_UPDATE_TABLE.put( KeyModifier.SHIFT, SHIFT_TABLE );
		MODIFIER_2_UPDATE_TABLE.put( KeyModifier.ALT, ALT_TABLE );
	}
	
	private InputHandler() { }
	
	public static void initInputSystem()
	{
		GLOBAL_KEYS.forEach( KeyBind::selfRegis );
		NORMAL_KEYS.forEach( KeyBind::selfRegis );
		if ( Config.COMMON.development.enableTDev.get() ) {
			DEBUG_KEYS.forEach( KeyBind::selfRegis );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void onMouseInput( InputEvent.RawMouseEvent evt )
	{
		if ( Minecraft.getInstance().currentScreen == null )
		{
			final Input button = Type.MOUSE.getOrMakeInput( evt.getButton() );
			final boolean is_down = evt.getAction() == GLFW.GLFW_PRESS;
			__dispatchInput( button, is_down, evt.getMods() );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void onKeyInput( InputEvent.KeyInputEvent evt )
	{
		if ( Minecraft.getInstance().currentScreen == null )
		{
			final Input key = Type.KEYSYM.getOrMakeInput( evt.getKey() );
			final boolean is_down = evt.getAction() == GLFW.GLFW_PRESS;
			__dispatchInput( key, is_down, evt.getModifiers() );
		}
	}
	
	private static void __dispatchInput( Input input, boolean is_down, int modifier_bits )
	{
		final Consumer< KeyBind > active_updater = kb -> kb.activeUpdate( is_down );
		final Consumer< KeyBind > inactive_updater = kb -> kb.inactiveUpdate( is_down );
		
		GLOBAL_TABLE.get( input ).forEach( active_updater );
		
		final boolean none_modifier_active = modifier_bits == 0;
		NORMAL_TABLE.get( input ).forEach( none_modifier_active ? active_updater : inactive_updater );
		
		final boolean is_ctrl_active = ( modifier_bits & GLFW.GLFW_MOD_CONTROL ) != 0;
		CTRL_TABLE.get( input ).forEach( is_ctrl_active ? active_updater : inactive_updater );
		
		final boolean is_shift_active = ( modifier_bits & GLFW.GLFW_MOD_SHIFT ) != 0;
		SHIFT_TABLE.get( input ).forEach( is_shift_active ? active_updater : inactive_updater );
		
		final boolean is_alt_active = ( modifier_bits & GLFW.GLFW_MOD_ALT ) != 0;
		ALT_TABLE.get( input ).forEach( is_alt_active ? active_updater : inactive_updater );
	}
	
	private static KeyBind ori_aim_key;
	static void restoreVanillaKeyBinds()
	{
		final boolean is_aim_hold_bounden = Keys.AIM_HOLD.keyCode() != InputMappings.INPUT_INVALID;
		ori_aim_key = is_aim_hold_bounden ? Keys.AIM_HOLD : Keys.AIM_TOGGLE;
		KeyBind.REGISTRY.values().forEach( KeyBind::restoreVanillaKeyBind );
	}
	
	static void clearVanillaKeyBinds( File file )
	{
		boolean has_key_bind_changed = false;
		for ( KeyBind kb : KeyBind.REGISTRY.values() ) {
			has_key_bind_changed |= kb.clearVanillaKeyBind();
		}
		
		if ( has_key_bind_changed )
		{
			// Make sure only one aim key is bounden.
			final Input none = InputMappings.INPUT_INVALID;
			if ( Keys.AIM_HOLD.keyCode() != none && Keys.AIM_TOGGLE.keyCode() != none ) {
				ori_aim_key.setKeyCodeAndModifier( none, KeyModifier.NONE );
			}
			
			saveKeyBindsTo( file );
		}
		
		// Do not forget to update vanilla key array and hash.
		KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	static void saveKeyBindsTo( File file )
	{
		try ( FileWriter out = new FileWriter( file ) )
		{
			final HashMap< String, String > mapper = new HashMap<>();
			KeyBind.REGISTRY.values().forEach(
				kb -> mapper.put( kb.name(), kb.keyCode() + "+" + kb.keyModifier() ) );
			out.write( GSON.toJson( mapper ) );
		}
		catch ( IOException e ) {
			GunMod.LOGGER.error( "Fail write key bindings", e );
		}
		
		// Save keys will be called in two cases:
		//     1. Key binding changed by user.
		//     2. First time entering the game.
		// And for both cases it is needed to update mapping table.
		__updateMappingTable();
	}
	
	static void loadKeyBindsFrom( File file )
	{
		try ( FileReader in = new FileReader( file ) )
		{
			final JsonObject obj = GSON.fromJson( in, JsonObject.class );
			obj.entrySet().forEach( e -> {
				try
				{
					final KeyBind kb = KeyBind.REGISTRY.get( e.getKey() );
					final String[] key_code_setting = e.getValue().getAsString().split( "\\+" );
					final Input input = InputMappings.getInputByName( key_code_setting[0] );
					final KeyModifier modifier = KeyModifier.valueFromString( key_code_setting[1] );
					kb.setKeyCodeAndModifier( input, modifier );
				}
				catch ( NullPointerException e_ ) {
					GunMod.LOGGER.error( "Key bind " + e.getKey() + " do not exist" );
				}
				catch ( IllegalArgumentException e_ ) {
					GunMod.LOGGER.error( "Bad key code: " + e );
				}
			} );
		}
		catch ( IOException e ) {
			GunMod.LOGGER.error( "Fail read key bind", e );
		}
		
		__updateMappingTable();
	}
	
	static KeyBind addNormal( KeyBind kb )
	{
		NORMAL_KEYS.add( kb );
		return kb;
	}
	
	static KeyBind addGlobal( KeyBind kb )
	{
		GLOBAL_KEYS.add( kb );
		return kb;
	}
	
	static KeyBind addDebug( KeyBind kb )
	{
		DEBUG_KEYS.add( kb );
		return kb;
	}
	
	private static void __updateMappingTable()
	{
		GLOBAL_TABLE.clear();
		GLOBAL_KEYS.forEach( kb -> GLOBAL_TABLE.put( kb.keyCode(), kb ) );
		DEBUG_KEYS.forEach( kb -> GLOBAL_TABLE.put( kb.keyCode(), kb ) );
		
		MODIFIER_2_UPDATE_TABLE.values().forEach( HashMultimap::clear );
		NORMAL_KEYS.forEach( kb -> {
			final HashMultimap< Input, KeyBind > update_table
				= MODIFIER_2_UPDATE_TABLE.get( kb.keyModifier() );
			update_table.put( kb.keyCode(), kb );
		} );
	}
}
