package com.tac.guns.client.settings;

import net.minecraft.util.StringRepresentable;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunEnumOption<E extends Enum<E> & StringRepresentable>
{
    /*
    private Class<E> enumClass;
    private int ordinal = 0;
    private Function<Options, E> getter;
    private BiConsumer<Options, E> setter;
    private BiFunction<Options, GunEnumOption<E>, Component> displayNameGetter;

    public GunEnumOption(String title, Class<E> enumClass, Function<Options, E> getter, BiConsumer<Options, E> setter, BiFunction<Options, GunEnumOption<E>, Component> displayNameGetter)
    {
        super(title);
        this.enumClass = enumClass;
        this.getter = getter;
        this.setter = setter;
        this.displayNameGetter = displayNameGetter;
    }

    private void nextEnum(Options options)
    {
        this.set(options, this.getEnum(++this.ordinal));
    }

    public void set(Options options, E e)
    {
        this.setter.accept(options, e);
        this.ordinal = e.ordinal();
    }

    public E get(Options options)
    {
        E e = this.getter.apply(options);
        this.ordinal = e.ordinal();
        return e;
    }

    public AbstractWidget createButton(Options options, int x, int y, int width)
    {
        return new OptionButton(x, y, width, 20, this, this.getTitle(options), (button) -> {
            this.nextEnum(options);
            button.setMessage(this.getTitle(options));
        });
    }

    public Component getTitle(Options options)
    {
        return this.displayNameGetter.apply(options, this);
    }

    private E getEnum(int ordinal)
    {
        E[] e = this.enumClass.getEnumConstants();
        if(ordinal >= e.length)
        {
            ordinal = 0;
        }
        return e[ordinal];
    }

     */
}
