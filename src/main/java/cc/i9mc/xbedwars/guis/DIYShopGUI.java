package cc.i9mc.xbedwars.guis;

import cc.i9mc.gameutils.gui.CustonGUI;
import cc.i9mc.gameutils.gui.GUIAction;
import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.xbedwars.database.PlayerData;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import cc.i9mc.xbedwars.shop.ItemShopManager;
import cc.i9mc.xbedwars.shop.ShopData;
import cc.i9mc.xbedwars.shop.type.ColorType;
import cc.i9mc.xbedwars.shop.type.ItemType;
import cc.i9mc.xbedwars.shop.type.PriceCost;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by JinVan on 2020/8/2.
 */
public class DIYShopGUI extends CustonGUI {
    private final Integer[] slots = new Integer[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    public DIYShopGUI(Game game, GamePlayer gamePlayer, ItemStack itemStack, String className) {
        super(gamePlayer.getPlayer(), "§8正在添加到快捷购买菜单...", 54);
        Player player = gamePlayer.getPlayer();
        PlayerData playerData = gamePlayer.getPlayerData();

        setItem(4, new ItemBuilderUtil().setItemStack(itemStack).setLores(" ", "§e正在添加物品到快捷购买菜单..").getItem(), new GUIAction(0, () -> { }, false));

        int j = -1;
        for(String s : playerData.getShopSort()){
            j++;
            String[] strings = !s.equals("AIR") ? s.split("#") : null;

            ItemType itemType = null;
            if(strings != null && strings.length == 2){
                for(ShopData shopData1 : ItemShopManager.getShops()){
                    if(shopData1.getClass().getSimpleName().equals(strings[0])){
                        itemType = shopData1.getShopItems().get(Integer.parseInt(strings[1]) - 1);
                        break;
                    }
                }
            }

            if(strings == null || itemType == null){
                int finalJ = j;
                setItem(slots[j], new ItemBuilderUtil().setType(Material.STAINED_GLASS_PANE).setDurability(14).setDisplayName("§c空闲的槽位").setLores("§e点击设置").getItem(), new GUIAction(0, () -> {
                    playerData.getShopSort()[finalJ] = className;
                    playerData.saveShops();
                    new ItemShopGUI(player, 0, game).open();
                }, false));
                continue;
            }

            setItem(game, gamePlayer, slots[j], itemType, className);
        }
    }


    public void setItem(Game game, GamePlayer gamePlayer , int size, ItemType itemType, String className) {
        ItemBuilderUtil itemBuilderUtil = new ItemBuilderUtil();
        itemBuilderUtil.setItemStack(itemType.getItemStack().clone());
        if (itemType.getColorType() == ColorType.PICKAXE) {
            switch (gamePlayer.getPickaxeType()) {
                case STONE:
                    itemBuilderUtil.setType(Material.STONE_PICKAXE);
                    itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 20, 20));
                    break;
                case IRON:
                    itemBuilderUtil.setType(Material.IRON_PICKAXE);
                    itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 8, 24));
                    break;
                case DIAMOND:
                    itemBuilderUtil.setType(Material.DIAMOND_PICKAXE);
                    itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 12, 36));
                    break;
                default:
                    itemBuilderUtil.setType(Material.WOOD_PICKAXE);
                    itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
                    break;
            }
        } else if (itemType.getColorType() == ColorType.AXE) {
            switch (gamePlayer.getAxeType()) {
                case STONE:
                    itemBuilderUtil.setType(Material.STONE_AXE);
                    itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 20, 20));
                    break;
                case IRON:
                    itemBuilderUtil.setType(Material.IRON_AXE);
                    itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 8, 24));
                    break;
                case DIAMOND:
                    itemBuilderUtil.setType(Material.DIAMOND_AXE);
                    itemType.setPriceCost(new PriceCost(Material.GOLD_INGOT, 12, 36));
                    break;
                default:
                    itemBuilderUtil.setType(Material.WOOD_AXE);
                    itemType.setPriceCost(new PriceCost(Material.IRON_INGOT, 10, 10));
                    break;
            }
        }

        super.setItem(size, itemBuilderUtil.setDisplayName("§c" + itemType.getDisplayName()).setLores("§e点击替换").getItem(), new GUIAction(0, () -> {
            gamePlayer.getPlayerData().getShopSort()[Arrays.asList(slots).indexOf(size)] = className;
            gamePlayer.getPlayerData().saveShops();
            new ItemShopGUI(gamePlayer.getPlayer(), 0, game).open();
        }, false));
    }
}
