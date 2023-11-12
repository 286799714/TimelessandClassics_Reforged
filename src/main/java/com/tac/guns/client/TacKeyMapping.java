package com.tac.guns.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.LinkedList;

@OnlyIn( Dist.CLIENT )
public final class TacKeyMapping extends KeyMapping
{
	private final LinkedList< Runnable > press_callbacks = new LinkedList<>();
	private boolean is_down;
	
	public TacKeyMapping(
		String description,
		IKeyConflictContext keyConflictContext,
		KeyModifier keyModifier,
		Key keyCode,
		String category
	) { super( description, keyConflictContext, keyModifier, keyCode, category ); }
	
	public void addPressCallback( Runnable callback ) {
		this.press_callbacks.add( callback );
	}

	@Override
	public boolean isDown() {
		return this.is_down;
	}

	@Override
	public void setDown( boolean is_down )
	{
		if ( is_down && !this.is_down ) {
			this.press_callbacks.forEach( Runnable::run );
		}
		
		this.is_down = is_down;
		super.setDown( is_down );
	}
	
	public static final class TacKeyBuilder
	{
		private final String description;
		private IKeyConflictContext conflict_context = GunConflictContext.IN_GAME_HOLDING_WEAPON;
		private KeyModifier modifier = KeyModifier.NONE;
		private Key key = InputConstants.UNKNOWN;
		
		public TacKeyBuilder( String description ) {
			this.description = description;
		}
		
		public TacKeyBuilder withConflictContext( IKeyConflictContext context )
		{
			this.conflict_context = context;
			return this;
		}
		
		public TacKeyBuilder withKeyboardKey( int key_code )
		{
			this.key = Type.KEYSYM.getOrCreate( key_code );
			return this;
		}
		
		public TacKeyBuilder withKeyModifier( KeyModifier modifier )
		{
			this.modifier = modifier;
			return this;
		}
		
		public TacKeyMapping buildAndRegis()
		{
			final TacKeyMapping kb = new TacKeyMapping(
				this.description,
				this.conflict_context,
				this.modifier,
				this.key,
				"key.categories.tac"
			);
			ClientRegistry.registerKeyBinding( kb );
			return kb;
		}
	}
}
