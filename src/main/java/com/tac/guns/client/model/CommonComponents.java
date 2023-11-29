package com.tac.guns.client.model;

public class CommonComponents {
    private CommonComponents(){}

    //main
    public static final String BODY = "body";                                   //gun body
    public static final String BODY_ILLUMINATED = "body_illuminated";                                   //illuminated part of gun body

    //bolt
    public static final String BOLT = "bolt";                                   //bolt
    public static final String BOLT_TAIL = "bolt_tail";                         //the block behind a bolt; usually on RFs
    public static final String BOLT_HANDLE = "bolt_handle";                     //the handle on a bolt

    //bullet
    public static final String BULLET = "bullet";                               //bullet
    public static final String BULLET1 = "bullet1";                             //bullet1
    public static final String BULLET2 = "bullet2";                             //bullet2
    public static final String BULLET_CHAIN = "bullet_chain";                   //bullet chain
    public static final String BULLET_HEAD = "bullet_head";                     //bullet head
    public static final String BULLET_SHELL = "bullet_shell";                   //bullet shell

    //cap
    public static final String CAP = "cap";                                     //cap; usually on MGs

    //carry
    public static final String CARRY = "carry";                                 //the carry on a gun; usually on m4

    //grip
    public static final String GRIP_LIGHT = "grip_light";                       //light grip
    public static final String GRIP_TACTICAL = "grip_tactical";                 //tactical grip

    //handle
    public static final String HANDLE = "handle";                               //the handle behind a gun; like m4 or mk47 or udp etc. or the handle to lift

    //laser
    public static final String LASER_BASIC = "laser_basic";                     //basic laser beam
    public static final String LASER_BASIC_DEVICE = "laser_basic_device";       //basic laser device
    public static final String LASER_IR = "laser_ir";                           //ir laser beam
    public static final String LASER_IR_DEVICE = "laser_ir_device";             //ir laser device

    //mag
    public static final String MAG = "mag";                                     //a single mag, no extra mag
    public static final String MAG_EXTENDED = "mag_extended";                   //extended mag
    public static final String MAG_STANDARD = "mag_standard";                   //standard mag

    //muzzle
    public static final String MUZZLE_BRAKE = "muzzle_brake";                   //brake
    public static final String MUZZLE_COMPENSATOR = "muzzle_compensator";       //compensator
    public static final String MUZZLE_DEFAULT = "muzzle_default";               //default muzzle; render if there is no muzzle attachment
    public static final String MUZZLE_SILENCER = "muzzle_silencer";             //silencer

    //pump
    public static final String PUMP = "pump";                                   //SGs' pump

    //rail
    public static final String GRIP_RAIL_COVER = "grip_rail_cover";           //the cover on rail when there are no attachments on the under rail
    public static final String SIDE_RAIL_COVER = "side_rail_cover";             //the cover on rail when there are no attachments on the side rail
    public static final String TOP_RAIL_COVER = "top_rail_cover";               //the cover on rail when there are no attachments on the top rail
    public static final String HAND_GUARD_DEFAULT = "hand_guard_default";                   //default rail
    public static final String HAND_GUARD_EXTENDED = "hand_guard_extended";                 //an extended on rail for rail attachments
    public static final String RAIL_EXTENDED_UNDER = "rail_extended_under";     //an extended on rail for under-rail attachments
    public static final String RAIL_EXTENDED_SIDE = "rail_extended_side";       //an extended on rail for side-rail attachments
    public static final String RAIL_EXTENDED_TOP = "rail_extended_top";         //an extended on rail for top-rail attachments
    public static final String RAIL_SCOPE = "rail_scope";                       //the rail for placing the scope (like mount of ak47)

    //sight
    public static final String SIGHT = "sight";                                 //iron sight( For pistols, usually refers to the part near the muzzle that does not move with the slide)
    public static final String SIGHT_ILLUMINATED = "sight_illuminated";                     //light part of the iron sight
    public static final String SIGHT_FOLDED = "sight_folded";                   //folded sight, render when install a scope
    public static final String SIGHT_FOLDED_ILLUMINATED = "sight_folded_illuminated";       //light part of folded sight

    //slide(pistol)
    public static final String SLIDE = "slide";                                 //pistol slide
    public static final String SLIDE_ILLUMINATED = "slide_illuminated";                     //the light part move with slide

    //stock
    public static final String STOCK_DEFAULT = "stock_default";                 //default stock; render if there is no stock attachment
    public static final String STOCK_LIGHT = "stock_light";                     //light stock
    public static final String STOCK_TACTICAL = "stock_tactical";               //tactical stock
    public static final String STOCK_HEAVY = "stock_heavy";            //heavy stock
}
