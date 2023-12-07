package com.tac.guns.client.model;

public class CommonComponents {
    private CommonComponents(){}
    public static final String BULLET_IN_BARREL = "bullet_in_barrel";
    public static final String BULLET_IN_MAG = "bullet_in_mag";
    public static final String BULLET_CHAIN = "bullet_chain";                   //bullet chain

    public static final String CARRY = "carry";                                 //usually on m4

    public static final String MAG_EXTENDED_1 = "mag_extended_1";                   //extended mag
    public static final String MAG_EXTENDED_2 = "mag_extended_2";                   //extended mag
    public static final String MAG_EXTENDED_3 = "mag_extended_3";                   //extended mag
    public static final String MAG_STANDARD = "mag_standard";                   //standard mag

    //muzzle
    public static final String MUZZLE_BRAKE = "muzzle_brake";                   //brake
    public static final String MUZZLE_COMPENSATOR = "muzzle_compensator";       //compensator
    public static final String MUZZLE_DEFAULT = "muzzle_default";               //default muzzle; render if there is no muzzle attachment
    public static final String MUZZLE_SILENCER = "muzzle_silencer";             //silencer

    public static final String MOUNT = "mount";                                 //the rail for placing the scope (like mount of ak47)

    public static final String SIGHT = "sight";                                 //iron sight( For pistols, usually refers to the part near the muzzle that does not move with the slide)
    public static final String SIGHT_FOLDED = "sight_folded";                   //folded sight, render when install a scope

    public static final String SLIDE = "slide";                                 //pistol slide

    //stock
    public static final String STOCK_DEFAULT = "stock_default";                 //default stock; render if there is no stock attachment
    public static final String STOCK_LIGHT = "stock_light";                     //light stock
    public static final String STOCK_TACTICAL = "stock_tactical";               //tactical stock
    public static final String STOCK_HEAVY = "stock_heavy";                     //heavy stock
}
