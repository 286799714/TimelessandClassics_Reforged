package com.tac.guns.item.transition.grenades;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.entity.specifics.LightGrenadeEntity;
import com.tac.guns.item.GrenadeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class LightGrenadeItem extends GrenadeItem
{
    private float power;
    private float radius;
    public LightGrenadeItem(Item.Properties properties, int maxCookTime, float power, float radius, float speed)
    {
        super(properties, maxCookTime, power, radius, speed);
        this.power = power;
        this.radius = radius;
    }

    public ThrowableGrenadeEntity create(Level world, LivingEntity entity, int timeLeft)
    {
        return new LightGrenadeEntity(world, entity, timeLeft, this.power, this.radius);
    }

    public boolean canCook()
    {
        return true;
    }

    protected void onThrown(Level world, ThrowableGrenadeEntity entity)
    {
    }
}
