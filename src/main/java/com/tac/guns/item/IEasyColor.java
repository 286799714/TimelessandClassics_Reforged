package com.tac.guns.item;

public interface IEasyColor {
    // Easy color is an interface marker used to define which coloring interface an item should get, scopes/other small colorable devices, use a custom 3 slot system, while guns / other items may use
    // IEasyColor should ALWAYS use the last 3 values of IAttachment.Type
}
