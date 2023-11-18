package com.tac.guns.client.render.model.internal;

import com.tac.guns.client.render.model.CommonComponents;
import com.tac.guns.client.render.model.GunComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TacGunComponents {
    //crossbow
    public static final GunComponent BEND_L = new GunComponent("tac", "bend_l");
    public static final GunComponent BEND_R = new GunComponent("tac", "bend_r");
    public static final GunComponent BONE_L = new GunComponent("tac", "bone_l");
    public static final GunComponent BONE_R = new GunComponent("tac", "bone_r");
    public static final GunComponent STRING_L_MAIN = new GunComponent("tac", "string_l_main");
    public static final GunComponent STRING_R_MAIN = new GunComponent("tac", "string_r_main");
    public static final GunComponent STRING_L_MOVE = new GunComponent("tac", "string_l_move");
    public static final GunComponent STRING_R_MOVE = new GunComponent("tac", "string_r_move");
    public static final GunComponent WHEEL_L = new GunComponent("tac", "wheel_l");
    public static final GunComponent WHEEL_R = new GunComponent("tac", "wheel_r");
    //barrel
    public static final GunComponent BARREL = new GunComponent("tac", "barrel");                               //out length barrel for adjust the position
    public static final GunComponent BARREL_EXTENDED = new GunComponent("tac", "barrel_extended");
    public static final GunComponent BARREL_STANDARD = new GunComponent("tac", "barrel_standard");
    public static final GunComponent CLUMSYYY = new GunComponent("tac", "clumsyyy");
    public static final GunComponent NEKOOO = new GunComponent("tac", "nekooo");
    public static final GunComponent LOADER = new GunComponent("tac", "loader");
    public static final GunComponent ROTATE = new GunComponent("tac", "rotate");
    public static final GunComponent SCOPE_DEFAULT = new GunComponent("tac", "scope_default");
    public static final GunComponent LIGHT = new GunComponent("tac", "light");
    public static final GunComponent SAFETY = new GunComponent("tac", "safety");
    public static final GunComponent BIPOD = new GunComponent("tac", "bipod");                                 //the tactical bipod
    public static final GunComponent BULLET_CHAIN_COVER = new GunComponent("tac", "bullet_chain_cover");       //the clip on bullet chain (like m249)
    public static final GunComponent HAMMER = new GunComponent("tac", "hammer");                               //the hammer to fire
    public static final GunComponent HANDLE_EXTRA = new GunComponent("tac", "handle_extra");                               //extra handle
    public static final GunComponent RELEASE = new GunComponent("tac", "release");                             //release mag clip
    public static final GunComponent ROCKET = new GunComponent("tac", "rocket");                               //rpg7 rocket
    public static final GunComponent STOCK_FOLDED = new GunComponent("tac", "stock_folded");                   //default folded stock; render if there is no stock attachment
    public static final GunComponent SLIDE_EXTENDED = new GunComponent("tac", "slide_extended");               //long pistol slide
    public static final GunComponent SLIDE_EXTENDED_LIGHT = new GunComponent("tac", "slide_extended_light");   //the light part move with slide
    public static final GunComponent PULL = new GunComponent("tac", "pull");                                   //something in barrel connect to bolt handle

    //register all these component
    static {
        Field[] fields = TacGunComponents.class.getDeclaredFields();
        for (Field field : fields) {
            if(GunComponent.class.isAssignableFrom(field.getType())){
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        GunComponent component = (GunComponent) field.get(null);
                        component.registerThis();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
