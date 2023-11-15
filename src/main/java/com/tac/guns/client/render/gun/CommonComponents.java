package com.tac.guns.client.render.gun;

public class CommonComponents {
    public static final GunModelComponent CLUMSYYY = new GunModelComponent("clumsyyy");
    public static final GunModelComponent NEKOOO = new GunModelComponent("nekooo");
    public static final GunModelComponent BODY_LIGHT = new GunModelComponent("body_light");
    public static final GunModelComponent LOADER = new GunModelComponent("loader");
    public static final GunModelComponent ROTATE = new GunModelComponent("rotate");
    public static final GunModelComponent SCOPE_DEFAULT = new GunModelComponent("scope_default");
    public static final GunModelComponent LIGHT = new GunModelComponent("light");
    public static final GunModelComponent SAFETY = new GunModelComponent("safety");

    //crossbow
    public static final GunModelComponent BEND_L = new GunModelComponent("bend_l");
    public static final GunModelComponent BEND_R = new GunModelComponent("bend_r");
    public static final GunModelComponent BONE_L = new GunModelComponent("bone_l");
    public static final GunModelComponent BONE_R = new GunModelComponent("bone_r");
    public static final GunModelComponent STRING_L_MAIN = new GunModelComponent("string_l_main");
    public static final GunModelComponent STRING_R_MAIN = new GunModelComponent("string_r_main");
    public static final GunModelComponent STRING_L_MOVE = new GunModelComponent("string_l_move");
    public static final GunModelComponent STRING_R_MOVE = new GunModelComponent("string_r_move");
    public static final GunModelComponent WHEEL_L = new GunModelComponent("wheel_l");
    public static final GunModelComponent WHEEL_R = new GunModelComponent("wheel_r");

    //main
    public static final GunModelComponent BODY = new GunModelComponent("main");                                   //static main part

    //barrel
    public static final GunModelComponent BARREL = new GunModelComponent("barrel");                               //out length barrel for adjust the position
    public static final GunModelComponent BARREL_EXTENDED = new GunModelComponent("barrel_extended");             //long barrel
    public static final GunModelComponent BARREL_SHORT = new GunModelComponent("barrel_short");                   //short barrel
    public static final GunModelComponent BARREL_STANDARD = new GunModelComponent("barrel_standard");             //standard barrel

    //bipod
    public static final GunModelComponent BIPOD = new GunModelComponent("bipod");                                 //the tactical bipod

    //bolt
    public static final GunModelComponent BOLT = new GunModelComponent("bolt");                                   //bolt
    public static final GunModelComponent BOLT_EXTRA = new GunModelComponent("bolt_extra");                       //the block behind a bolt; usually on RFs
    public static final GunModelComponent BOLT_HANDLE = new GunModelComponent("bolt_handle");                     //the handle on a bolt

    //bullet
    public static final GunModelComponent BULLET = new GunModelComponent("bullet");                               //bullet
    public static final GunModelComponent BULLET1 = new GunModelComponent("bullet1");                             //bullet1
    public static final GunModelComponent BULLET2 = new GunModelComponent("bullet2");                             //bullet2
    public static final GunModelComponent BULLET_CHAIN = new GunModelComponent("bullet_chain");                   //bullet chain
    public static final GunModelComponent BULLET_CHAIN_COVER = new GunModelComponent("bullet_chain_cover");       //the clip on bullet chain (like m249)
    public static final GunModelComponent BULLET_HEAD = new GunModelComponent("bullet_head");                     //bullet head
    public static final GunModelComponent BULLET_SHELL = new GunModelComponent("bullet_shell");                   //bullet shell

    //cap
    public static final GunModelComponent CAP = new GunModelComponent("cap");                                     //cap; usually on MGs

    //carry
    public static final GunModelComponent CARRY = new GunModelComponent("carry");                                 //the carry on a gun; usually on m4

    //grip
    public static final GunModelComponent GRIP_LIGHT = new GunModelComponent("grip_light");                       //light grip
    public static final GunModelComponent GRIP_TACTICAL = new GunModelComponent("grip_tactical");                 //tactical grip

    //hammer
    public static final GunModelComponent HAMMER = new GunModelComponent("hammer");                               //the hammer to fire

    //hand guard
    public static final GunModelComponent HAND_GUARD_DEFAULT = new GunModelComponent("hand_guard_default");       //default hand guard
    public static final GunModelComponent HAND_GUARD_EXTENDED = new GunModelComponent("hand_guard_extended");     //hand guard for long barrel
    public static final GunModelComponent HAND_GUARD_SHORT = new GunModelComponent("hand_guard_short");           //hand guard for short barrel
    public static final GunModelComponent HAND_GUARD_STANDARD = new GunModelComponent("hand_guard_standard");     //hand guard for standard barrel
    public static final GunModelComponent HAND_GUARD_TACTICAL = new GunModelComponent("hand_guard_tactical");     //tactical hand guard

    //handle
    public static final GunModelComponent HANDLE = new GunModelComponent("handle");                               //the handle behind a gun; like m4 or mk47 or udp etc. or the handle to lift
    public static final GunModelComponent HANDLE_EXTRA = new GunModelComponent("handle_extra");                               //extra handle

    //side rail
    public static final GunModelComponent LASER_BASIC = new GunModelComponent("laser_basic");                     //basic laser beam
    public static final GunModelComponent LASER_BASIC_DEVICE = new GunModelComponent("laser_basic_device");       //basic laser device
    public static final GunModelComponent LASER_IR = new GunModelComponent("laser_ir");                           //ir laser beam
    public static final GunModelComponent LASER_IR_DEVICE = new GunModelComponent("laser_ir_device");             //ir laser device

    //mag
    public static final GunModelComponent MAG = new GunModelComponent("mag");                                     //no extra mag level
    public static final GunModelComponent MAG_DRUM = new GunModelComponent("mag_drum");                           //drum mag
    public static final GunModelComponent MAG_EXTENDED = new GunModelComponent("mag_extended");                   //extended mag
    public static final GunModelComponent MAG_STANDARD = new GunModelComponent("mag_standard");                   //standard mag

    //muzzle
    public static final GunModelComponent MUZZLE_BRAKE = new GunModelComponent("muzzle_brake");                   //brake
    public static final GunModelComponent MUZZLE_COMPENSATOR = new GunModelComponent("muzzle_compensator");       //compensator
    public static final GunModelComponent MUZZLE_DEFAULT = new GunModelComponent("muzzle_default");               //default muzzle; render if there is no muzzle attachment
    public static final GunModelComponent MUZZLE_SILENCER = new GunModelComponent("muzzle_silencer");             //silencer

    //pull
    public static final GunModelComponent PULL = new GunModelComponent("pull");                                   //something in barrel connect to bolt handle

    //pump
    public static final GunModelComponent PUMP = new GunModelComponent("pump");                                   //SGs' pump

    //rail
    public static final GunModelComponent RAIL_COVER = new GunModelComponent("rail_cover");                       //the cover on rail when there are no attachments; all or side
    public static final GunModelComponent RAIL_COVER_UNDER = new GunModelComponent("rail_cover_under");           //the cover on rail when there are no attachments under the rail
    public static final GunModelComponent RAIL_COVER_SIDE = new GunModelComponent("rail_cover_side");             //the cover on rail when there are no attachments beside the rail
    public static final GunModelComponent RAIL_COVER_TOP = new GunModelComponent("rail_cover_top");               //the cover on rail when there are no attachments on the top of the rail
    public static final GunModelComponent RAIL_DEFAULT = new GunModelComponent("rail_default");                   //default rail
    public static final GunModelComponent RAIL_EXTENDED = new GunModelComponent("rail_extended");                 //an extended on rail for rail attachments
    public static final GunModelComponent RAIL_EXTENDED_UNDER = new GunModelComponent("rail_extended_under");     //an extended on rail for under-rail attachments
    public static final GunModelComponent RAIL_EXTENDED_SIDE = new GunModelComponent("rail_extended_side");       //an extended on rail for side-rail attachments
    public static final GunModelComponent RAIL_EXTENDED_TOP = new GunModelComponent("rail_extended_top");         //an extended on rail for top-rail attachments
    public static final GunModelComponent RAIL_SCOPE = new GunModelComponent("rail_scope");                       //the rail for place the scope (like mount of ak47)

    //release
    public static final GunModelComponent RELEASE = new GunModelComponent("release");                             //release mag clip

    //rocket
    public static final GunModelComponent ROCKET = new GunModelComponent("rocket");                               //rpg7 rocket

    //sight
    public static final GunModelComponent SIGHT = new GunModelComponent("sight");                                 //sight / sight unfolded
    public static final GunModelComponent SIGHT_FOLDED = new GunModelComponent("sight_folded");                   //folded sight
    public static final GunModelComponent SIGHT_FOLDED_LIGHT = new GunModelComponent("sight_folded_light");       //light on folded sight
    public static final GunModelComponent SIGHT_LIGHT = new GunModelComponent("sight_light");                     //light on sight or not move part on pistol

    //slide
    public static final GunModelComponent SLIDE = new GunModelComponent("slide");                                 //pistol slide
    public static final GunModelComponent SLIDE_EXTENDED = new GunModelComponent("slide_extended");               //long pistol slide
    public static final GunModelComponent SLIDE_LIGHT = new GunModelComponent("slide_light");                     //the light part move with slide
    public static final GunModelComponent SLIDE_EXTENDED_LIGHT = new GunModelComponent("slide_extended_light");   //the light part move with slide

    //stock
    public static final GunModelComponent STOCK_DEFAULT = new GunModelComponent("stock_default");                 //default stock; render if there is no stock attachment
    public static final GunModelComponent STOCK_FOLDED = new GunModelComponent("stock_folded");                   //default folded stock; render if there is no stock attachment
    public static final GunModelComponent STOCK_LIGHT = new GunModelComponent("stock_light");                     //light stock
    public static final GunModelComponent STOCK_TACTICAL = new GunModelComponent("stock_tactical");               //tactical stock
    public static final GunModelComponent STOCK_HEAVY = new GunModelComponent("stock_heavy");            //heavy stock
}
