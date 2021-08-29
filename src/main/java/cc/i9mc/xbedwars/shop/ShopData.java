package cc.i9mc.xbedwars.shop;

import cc.i9mc.xbedwars.shop.type.ItemType;

import java.util.List;

public interface ShopData {
    ItemType getMainShopItem();

    List<ItemType> getShopItems();
}
