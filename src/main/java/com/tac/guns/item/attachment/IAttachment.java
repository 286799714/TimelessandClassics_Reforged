package com.tac.guns.item.attachment;

import com.tac.guns.item.attachment.impl.Attachment;

import javax.annotation.Nullable;

/**
 * The base attachment interface
 * <p>
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public interface IAttachment<T extends Attachment>
{
    /**
     * @return The type of this attachment
     */
    Type getType();

    /**
     * @return The additional properties about this attachment
     */
    T getProperties();

    enum Type
    {
        SCOPE("scope", "Scope"),            // 0
        BARREL("barrel", "Barrel"),            // 1
        STOCK("stock", "Stock"),            // 2
        UNDER_BARREL("under_barrel", "Under_Barrel"),            // 3
        SIDE_RAIL("side_rail", "Side_Rail"),            // 4
        EXTENDED_MAG("extended_mag", "Extended_Mag"), // 5
        GUN_SKIN("gun_skin", "Gun_Skin"),           // 6

        IR_DEVICE("ir_device", "Ir_Device"), //7
        OLD_SCOPE("oldScope", "OldScope"),            // 8
        PISTOL_SCOPE("pistolScope", "PistolScope"),            // 9
        PISTOL_BARREL("pistolBarrel", "PistolBarrel"),            // 10

        SCOPE_RETICLE_COLOR("reticle_color", "Reticle_Color"), // Scope Attachment Type // 11
        SCOPE_BODY_COLOR("body_color", "Body_Color"),          // Scope Attachment Type // 12
        SCOPE_GLASS_COLOR("glass_color", "Glass_Color");       // Scope Attachment Type // 13

        private String translationKey;
        private String tagKey;

        Type(String translationKey, String tagKey)
        {
            this.translationKey = translationKey;
            this.tagKey = tagKey;
        }

        public String getTranslationKey()
        {
            return this.translationKey;
        }

        public String getTagKey()
        {
            return this.tagKey;
        }

        @Nullable
        public static Type byTagKey(String s)
        {
            for(Type type : values())
            {
                if(type.tagKey.equalsIgnoreCase(s))
                {
                    return type;
                }
            }
            return null;
        }
    }
}
