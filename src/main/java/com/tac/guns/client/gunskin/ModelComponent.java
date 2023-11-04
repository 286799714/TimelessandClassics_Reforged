package com.tac.guns.client.gunskin;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public enum ModelComponent {
    //special
    CLUMSYYY("clumsyyy"),
    NEKOOO("nekooo"),
    BODY_LIGHT("body_light"),

    //main
    BODY("main"),                                   //static main part

    //barrel
    BARREL("barrel"),                               //out length barrel for adjust the position
    BARREL_EXTENDED("barrel_extended"),             //long barrel
    BARREL_SHORT("barrel_short"),                   //short barrel
    BARREL_STANDARD("barrel_standard"),             //standard barrel

    //bipod
    BIPOD("bipod"),                                 //the tactical bipod

    //bolt
    BOLT("bolt"),                                   //bolt
    BOLT_EXTRA("bolt_extra"),                       //the block behind a bolt, usually on RFs
    BOLT_HANDLE("bolt_handle"),                     //the handle on a bolt

    //bullet
    BULLET("bullet"),                               //bullet
    BULLET1("bullet1"),                             //bullet1
    BULLET2("bullet2"),                             //bullet2
    BULLET_CHAIN("bullet_chain"),                   //bullet chain
    BULLET_CHAIN_COVER("bullet_chain_cover"),       //the clip on bullet chain (like m249)
    BULLET_HEAD("bullet_head"),                     //bullet head
    BULLET_SHELL("bullet_shell"),                   //bullet shell

    //cap
    CAP("cap"),                                     //cap, usually on MGs

    //carry
    CARRY("carry"),                                 //the carry on a gun, usually on m4

    //grip
    GRIP_LIGHT("grip_light"),                       //light grip
    GRIP_TACTICAL("grip_tactical"),                 //tactical grip

    //hammer
    HAMMER("hammer"),                               //the hammer to fire

    //hand guard
    HAND_GUARD_DEFAULT("hand_guard_default"),       //default hand guard
    HAND_GUARD_EXTENDED("hand_guard_extended"),     //hand guard for long barrel
    HAND_GUARD_SHORT("hand_guard_short"),           //hand guard for short barrel
    HAND_GUARD_STANDARD("hand_guard_standard"),     //hand guard for standard barrel
    HAND_GUARD_TACTICAL("hand_guard_tactical"),     //tactical hand guard

    //handle
    HANDLE("handle"),                               //the handle behind a gun, like m4 or mk47 or udp etc. or the handle to lift
    HANDLE_EXTRA("handle_extra"),                               //extra handle

    //side rail
    LASER_BASIC("laser_basic"),                     //basic laser beam
    LASER_BASIC_DEVICE("laser_basic_device"),       //basic laser device
    LASER_IR("laser_ir"),                           //ir laser beam
    LASER_IR_DEVICE("laser_ir_device"),             //ir laser device

    //mag
    MAG("mag"),                                     //no extra mag level
    MAG_DRUM("mag_drum"),                           //drum mag
    MAG_EXTENDED("mag_extended"),                   //extended mag
    MAG_STANDARD("mag_standard"),                   //standard mag

    //muzzle
    MUZZLE_BRAKE("muzzle_brake"),                   //brake
    MUZZLE_COMPENSATOR("muzzle_compensator"),       //compensator
    MUZZLE_DEFAULT("muzzle_default"),               //default muzzle, render if there is no muzzle attachment
    MUZZLE_SILENCER("muzzle_silencer"),             //silencer

    //pull
    PULL("pull"),                                   //something in barrel connect to bolt handle

    //pump
    PUMP("pump"),                                   //SGs' pump

    //rail
    RAIL_COVER("rail_cover"),                       //the cover on rail when there are no attachments, all or side
    RAIL_COVER_UNDER("rail_cover_under"),           //the cover on rail when there are no attachments under the rail
    RAIL_COVER_SIDE("rail_cover_side"),             //the cover on rail when there are no attachments beside the rail
    RAIL_COVER_TOP("rail_cover_top"),               //the cover on rail when there are no attachments on the top of the rail
    RAIL_DEFAULT("rail_default"),                   //default rail
    RAIL_EXTENDED("rail_extended"),                 //an extended on rail for rail attachments
    RAIL_EXTENDED_UNDER("rail_extended_under"),     //an extended on rail for under-rail attachments
    RAIL_EXTENDED_SIDE("rail_extended_side"),       //an extended on rail for side-rail attachments
    RAIL_EXTENDED_TOP("rail_extended_top"),         //an extended on rail for top-rail attachments
    RAIL_SCOPE("rail_scope"),                       //the rail for place the scope (like mount of ak47)

    //release
    RELEASE("release"),                             //release mag clip

    //rocket
    ROCKET("rocket"),                               //rpg7 rocket

    //sight
    SIGHT("sight"),                                 //sight / sight unfolded
    SIGHT_FOLDED("sight_folded"),                   //folded sight
    SIGHT_FOLDED_LIGHT("sight_folded_light"),       //light on folded sight
    SIGHT_LIGHT("sight_light"),                     //light on sight or not move part on pistol

    //slide
    SLIDE("slide"),                                 //pistol slide
    SLIDE_EXTENDED("slide_extended"),               //long pistol slide
    SLIDE_LIGHT("slide_light"),                     //the light part move with slide
    SLIDE_EXTENDED_LIGHT("slide_extended_light"),   //the light part move with slide

    //stock
    STOCK_DEFAULT("stock_default"),                 //default stock, render if there is no stock attachment
    STOCK_FOLDED("stock_folded"),                   //default folded stock, render if there is no stock attachment
    STOCK_LIGHT("stock_light"),                     //light stock
    STOCK_TACTICAL("stock_tactical"),               //tactical stock
    STOCK_HEAVY("stock_heavy")                      //heavy stock
    ;
    public final String key;
    ModelComponent(String key){
        this.key = key;
    }
    /**
     * @return The default model location of the component according to the main component
     * */
    @Nullable
    public static ResourceLocation getModelLocation(ModelComponent component, String mainLocation){
        return component.getModelLocation(mainLocation);
    }
    @Nullable
    public static ResourceLocation getModelLocation(ModelComponent component, ResourceLocation mainLocation){
        return component.getModelLocation(mainLocation);
    }
    @Nullable
    public ResourceLocation getModelLocation(String mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this==BODY ? "" : "_" + this.key));
    }
    @Nullable
    public ResourceLocation getModelLocation(ResourceLocation mainLocation){
        return ResourceLocation.tryParse(mainLocation+(this==BODY ? "" : "_" + this.key));
    }
}
