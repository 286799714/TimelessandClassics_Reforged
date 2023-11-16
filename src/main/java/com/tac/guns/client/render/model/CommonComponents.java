package com.tac.guns.client.render.model;

public class CommonComponents {
    //main
    public static final GunComponent BODY = new GunComponent(null, "body");                                   //gun body
    public static final GunComponent BODY_LIGHT = new GunComponent("body_light");                                   //light part of gun body

    //bolt
    public static final GunComponent BOLT = new GunComponent("bolt");                                   //bolt
    public static final GunComponent BOLT_TAIL = new GunComponent("bolt_tail");                       //the block behind a bolt; usually on RFs
    public static final GunComponent BOLT_HANDLE = new GunComponent("bolt_handle");                     //the handle on a bolt

    //bullet
    public static final GunComponent BULLET = new GunComponent("bullet");                               //bullet
    public static final GunComponent BULLET1 = new GunComponent("bullet1");                             //bullet1
    public static final GunComponent BULLET2 = new GunComponent("bullet2");                             //bullet2
    public static final GunComponent BULLET_CHAIN = new GunComponent("bullet_chain");                   //bullet chain
    public static final GunComponent BULLET_HEAD = new GunComponent("bullet_head", "bullet");                     //bullet head
    public static final GunComponent BULLET_SHELL = new GunComponent("bullet_shell", "bullet");                   //bullet shell

    //cap
    public static final GunComponent CAP = new GunComponent("cap");                                     //cap; usually on MGs

    //carry
    public static final GunComponent CARRY = new GunComponent("carry", "body");                                 //the carry on a gun; usually on m4

    //grip
    public static final GunComponent GRIP_LIGHT = new GunComponent("grip_light", "body");                       //light grip
    public static final GunComponent GRIP_TACTICAL = new GunComponent("grip_tactical", "body");                 //tactical grip

    //handle
    public static final GunComponent HANDLE = new GunComponent("handle");                               //the handle behind a gun; like m4 or mk47 or udp etc. or the handle to lift

    //laser
    public static final GunComponent LASER_BASIC = new GunComponent("laser_basic", "body");                     //basic laser beam
    public static final GunComponent LASER_BASIC_DEVICE = new GunComponent("laser_basic_device", "body");       //basic laser device
    public static final GunComponent LASER_IR = new GunComponent("laser_ir", "body");                           //ir laser beam
    public static final GunComponent LASER_IR_DEVICE = new GunComponent("laser_ir_device", "body");             //ir laser device

    //mag
    public static final GunComponent MAG = new GunComponent("mag", "mag");                                     //no extra mag level
    public static final GunComponent MAG_DRUM = new GunComponent("mag_drum", "mag");                           //drum mag
    public static final GunComponent MAG_EXTENDED = new GunComponent("mag_extended", "mag");                   //extended mag
    public static final GunComponent MAG_STANDARD = new GunComponent("mag_standard", "mag");                   //standard mag

    //muzzle
    public static final GunComponent MUZZLE_BRAKE = new GunComponent("muzzle_brake", "body");                   //brake
    public static final GunComponent MUZZLE_COMPENSATOR = new GunComponent("muzzle_compensator", "body");       //compensator
    public static final GunComponent MUZZLE_DEFAULT = new GunComponent("muzzle_default", "body");               //default muzzle; render if there is no muzzle attachment
    public static final GunComponent MUZZLE_SILENCER = new GunComponent("muzzle_silencer", "body");             //silencer

    //pump
    public static final GunComponent PUMP = new GunComponent("pump");                                   //SGs' pump

    //rail
    public static final GunComponent GRIP_RAIL_COVER = new GunComponent("grip_rail_cover", "body");           //the cover on rail when there are no attachments on the under rail
    public static final GunComponent SIDE_RAIL_COVER = new GunComponent("side_rail_cover", "body");             //the cover on rail when there are no attachments on the side rail
    public static final GunComponent TOP_RAIL_COVER = new GunComponent("top_rail_cover", "body");               //the cover on rail when there are no attachments on the top rail
    public static final GunComponent HAND_GUARD_DEFAULT = new GunComponent("hand_guard_default", "body");                   //default rail
    public static final GunComponent HAND_GUARD_EXTENDED = new GunComponent("hand_guard_extended", "body");                 //an extended on rail for rail attachments
    public static final GunComponent RAIL_EXTENDED_UNDER = new GunComponent("rail_extended_under", "body");     //an extended on rail for under-rail attachments
    public static final GunComponent RAIL_EXTENDED_SIDE = new GunComponent("rail_extended_side", "body");       //an extended on rail for side-rail attachments
    public static final GunComponent RAIL_EXTENDED_TOP = new GunComponent("rail_extended_top", "body");         //an extended on rail for top-rail attachments
    public static final GunComponent RAIL_SCOPE = new GunComponent("rail_scope", "body");                       //the rail for place the scope (like mount of ak47)

    //sight
    public static final GunComponent SIGHT = new GunComponent("sight", "body");                                 //iron sight( For pistols, usually refers to the part near the muzzle that does not move with the slide)
    public static final GunComponent SIGHT_LIGHT = new GunComponent("sight_light", "body");                     //light part of the iron sight
    public static final GunComponent SIGHT_FOLDED = new GunComponent("sight_folded", "body");                   //folded sight, render when install a scope
    public static final GunComponent SIGHT_FOLDED_LIGHT = new GunComponent("sight_folded_light", "body");       //light part of folded sight

    //slide(pistol)
    public static final GunComponent SLIDE = new GunComponent("slide", "slide");                                 //pistol slide
    public static final GunComponent SLIDE_LIGHT = new GunComponent("slide_light", "slide");                     //the light part move with slide

    //stock
    public static final GunComponent STOCK_DEFAULT = new GunComponent("stock_default", "body");                 //default stock; render if there is no stock attachment
    public static final GunComponent STOCK_LIGHT = new GunComponent("stock_light", "body");                     //light stock
    public static final GunComponent STOCK_TACTICAL = new GunComponent("stock_tactical", "body");               //tactical stock
    public static final GunComponent STOCK_HEAVY = new GunComponent("stock_heavy", "body");            //heavy stock

    //*********************//
    //Belows are components//
    //under namespace "tac"//
    //*********************//
    //crossbow
    public static final GunComponent BEND_L = new GunComponent("tac", "bend_l", "bend_l");
    public static final GunComponent BEND_R = new GunComponent("tac", "bend_r", "bend_r");
    public static final GunComponent BONE_L = new GunComponent("tac", "bone_l", "bone_l");
    public static final GunComponent BONE_R = new GunComponent("tac", "bone_r", "bone_r");
    public static final GunComponent STRING_L_MAIN = new GunComponent("tac", "string_l_main", "string_l_main");
    public static final GunComponent STRING_R_MAIN = new GunComponent("tac", "string_r_main", "string_r_main");
    public static final GunComponent STRING_L_MOVE = new GunComponent("tac", "string_l_move", "string_l_move");
    public static final GunComponent STRING_R_MOVE = new GunComponent("tac", "string_r_move", "string_r_move");
    public static final GunComponent WHEEL_L = new GunComponent("tac", "wheel_l", "wheel_l");
    public static final GunComponent WHEEL_R = new GunComponent("tac", "wheel_r", "wheel_r");

    //barrel
    public static final GunComponent BARREL = new GunComponent("tac", "barrel", "body");                               //out length barrel for adjust the position
    public static final GunComponent BARREL_EXTENDED = new GunComponent("tac", "barrel_extended", "body");
    public static final GunComponent BARREL_STANDARD = new GunComponent("tac", "barrel_standard", "body");
    public static final GunComponent CLUMSYYY = new GunComponent("tac", "clumsyyy", "body");
    public static final GunComponent NEKOOO = new GunComponent("tac", "nekooo", "body");
    public static final GunComponent LOADER = new GunComponent("tac", "loader", "loader");
    public static final GunComponent ROTATE = new GunComponent("tac", "rotate", "mag");
    public static final GunComponent SCOPE_DEFAULT = new GunComponent("tac", "scope_default", "body");
    public static final GunComponent LIGHT = new GunComponent("tac", "light", "body");
    public static final GunComponent SAFETY = new GunComponent("tac", "safety", "body");
    public static final GunComponent BIPOD = new GunComponent("tac", "bipod", "body");                                 //the tactical bipod
    public static final GunComponent BULLET_CHAIN_COVER = new GunComponent("tac", "bullet_chain_cover", "iron");       //the clip on bullet chain (like m249)
    public static final GunComponent HAMMER = new GunComponent("tac", "hammer", "body");                               //the hammer to fire
    public static final GunComponent HANDLE_EXTRA = new GunComponent("tac", "handle_extra", "handle_extra");                               //extra handle
    public static final GunComponent RELEASE = new GunComponent("tac", "release", "release");                             //release mag clip
    public static final GunComponent ROCKET = new GunComponent("tac", "rocket", "rocket");                               //rpg7 rocket
    public static final GunComponent STOCK_FOLDED = new GunComponent("tac", "stock_folded", "body");                   //default folded stock; render if there is no stock attachment
    public static final GunComponent SLIDE_EXTENDED = new GunComponent("tac", "slide_extended", "slide");               //long pistol slide
    public static final GunComponent SLIDE_EXTENDED_LIGHT = new GunComponent("tac", "slide_extended_light", "slide");   //the light part move with slide
    public static final GunComponent PULL = new GunComponent("tac", "pull", "pull");                                   //something in barrel connect to bolt handle
}
