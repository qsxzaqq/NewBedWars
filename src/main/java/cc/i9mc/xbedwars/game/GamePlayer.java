package cc.i9mc.xbedwars.game;

import cc.i9mc.gameutils.utils.ActionBarUtil;
import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.gameutils.utils.board.Board;
import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.database.PlayerData;
import cc.i9mc.xbedwars.spectator.SpectatorSettings;
import cc.i9mc.xbedwars.spectator.SpectatorTarget;
import cc.i9mc.xbedwars.types.ArmorType;
import cc.i9mc.xbedwars.types.ToolType;
import cc.i9mc.xbedwars.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GamePlayer {
    private static final ConcurrentHashMap<UUID, GamePlayer> gamePlayers = new ConcurrentHashMap<>();

    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final AssistsMap assistsMap;
    @Getter
    private final PlayerData playerData;
    @Setter
    @Getter
    private String displayname;
    @Getter
    @Setter
    private Board board;
    @Getter
    private boolean spectator;
    @Getter
    @Setter
    private SpectatorTarget spectatorTarget;
    @Getter
    @Setter
    private GameTeam gameTeam;
    @Getter
    private final PlayerCompass playerCompass;
    @Getter
    private int kills;
    @Getter
    private int finalKills;
    @Getter
    @Setter
    private ArmorType armorType;
    @Getter
    @Setter
    private ToolType pickaxeType;
    @Getter
    @Setter
    private ToolType axeType;
    @Getter
    @Setter
    private boolean shear;

    public GamePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.armorType = ArmorType.DEFAULT;
        this.pickaxeType = ToolType.NONE;
        this.axeType = ToolType.NONE;

        assistsMap = new AssistsMap(this);
        playerData = new PlayerData(this);
        playerCompass = new PlayerCompass(this);
    }

    public static GamePlayer create(UUID uuid, String name) {
        GamePlayer gamePlayer = get(uuid);
        if (gamePlayer != null) {
            return gamePlayer;
        }
        gamePlayer = new GamePlayer(uuid, name);
        gamePlayers.put(uuid, gamePlayer);
        return gamePlayer;
    }

    public static GamePlayer get(UUID uuid) {
        for (GamePlayer gamePlayer : gamePlayers.values()) {
            if (gamePlayer.getUuid().equals(uuid)) {
                return gamePlayer;
            }
        }
        return null;
    }

    public static List<GamePlayer> getGamePlayers() {
        return new ArrayList<>(gamePlayers.values());
    }

    public static List<GamePlayer> getTeamPlayers() {
        List<GamePlayer> teamPlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers.values()) {
            if (gamePlayer.getGameTeam() != null) {
                teamPlayers.add(gamePlayer);
            }
        }
        return teamPlayers;
    }

    public static List<GamePlayer> getOnlinePlayers() {
        List<GamePlayer> onlinePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers.values()) {
            if (gamePlayer.isOnline()) {
                onlinePlayers.add(gamePlayer);
            }
        }
        return onlinePlayers;
    }

    public static List<GamePlayer> getSpectators() {
        List<GamePlayer> spectators = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers.values()) {
            if (gamePlayer.isSpectator()) {
                spectators.add(gamePlayer);
            }
        }
        return spectators;
    }

    public static List<GamePlayer> sortFinalKills() {
        List<GamePlayer> list = new ArrayList<>(getOnlinePlayers());
        list.sort((player1, player2) -> player2.getFinalKills() - player1.getFinalKills());
        return list;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public void sendActionBar(String message) {
        if (!isOnline()) return;
        ActionBarUtil.sendBar(getPlayer(), message);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String title, String subTitle) {
        if (!isOnline()) return;
        TitleUtil.sendTitle(getPlayer(), fadeIn, stay, fadeOut, title, subTitle);
    }

    public void sendMessage(String message) {
        if (!isOnline()) return;
        getPlayer().sendMessage(message);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        if (!isOnline()) return;
        getPlayer().playSound(getPlayer().getLocation(), sound, volume, pitch);
    }

    public void setSpectator() {
        spectator = true;
    }

    public void toSpectator(String title, String subTitle) {
        spectator = true;
        spectatorTarget = new SpectatorTarget(this, null);

        Player player = getPlayer();
        sendTitle(10, 20, 10, title, subTitle);
        getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.getPlayer().hidePlayer(player));
        player.spigot().setCollidesWithEntities(false);
        player.setGameMode(GameMode.ADVENTURE);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        SpectatorSettings spectatorSettings = SpectatorSettings.get(this);
        if (spectatorSettings.getOption(SpectatorSettings.Option.NIGHTVISION)) {
            if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
        }

        player.getInventory().setItem(0, new ItemBuilderUtil().setType(Material.COMPASS).setDisplayName("§a§l传送器§7(右键打开)").getItem());
        player.getInventory().setItem(4, new ItemBuilderUtil().setType(Material.REDSTONE_COMPARATOR).setDisplayName("§c§l旁观者设置§7(右键打开)").getItem());
        player.getInventory().setItem(7, new ItemBuilderUtil().setType(Material.PAPER).setDisplayName("§b§l快速加入§7(右键加入)").getItem());
        player.getInventory().setItem(8, new ItemBuilderUtil().setType(Material.SLIME_BALL).setDisplayName("§c§l离开游戏§7(右键离开)").getItem());

        player.setAllowFlight(true);
        Util.setFlying(player);
        player.teleport(XBedwars.getInstance().getGame().getMapData().getReSpawn().toLocation());

        if (gameTeam != null && !gameTeam.getAlivePlayers().isEmpty()) {
            spectatorTarget.setTarget(gameTeam.getAlivePlayers().get(0));
        }
    }

    public void addKills() {
        kills += 1;
    }

    public void addFinalKills() {
        finalKills += 1;
    }

    public void setLastDamage(GamePlayer damager, long time) {
        assistsMap.setLastDamage(damager, time);
    }

    public void giveInventory() {
        Player player = getPlayer();
        player.getInventory().setHelmet(new ItemBuilderUtil().setType(Material.LEATHER_HELMET).setColor(gameTeam.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
        player.getInventory().setChestplate(new ItemBuilderUtil().setType(Material.LEATHER_CHESTPLATE).setColor(gameTeam.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
        giveArmor();
        giveSword(false);
        givePickaxe(false);
        giveAxe(false);
        giveShear();

        player.updateInventory();
    }

    public void giveArmor() {
        Player player = getPlayer();

        switch (armorType) {
            case CHAINMAIL:
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.CHAINMAIL_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                break;
            case IRON:
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.IRON_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.IRON_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                break;
            case DIAMOND:
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.DIAMOND_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.DIAMOND_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                break;
            default:
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.LEATHER_LEGGINGS).setColor(gameTeam.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.LEATHER_BOOTS).setColor(gameTeam.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                break;
        }

        if (gameTeam.getReinforcedArmor() > 0) {
            for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
                player.getInventory().getArmorContents()[i].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, gameTeam.getReinforcedArmor());
            }
        }
    }

    public void giveSword(boolean remove) {
        Player player = getPlayer();

        if (remove) {
            player.getInventory().remove(Material.WOOD_SWORD);
        }
        if (gameTeam.isSharpenedSwords()) {
            player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true, true).getItem());
        } else {
            player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_SWORD).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true, true).getItem());
        }
    }

    public void givePickaxe(boolean remove) {
        Player player = getPlayer();

        switch (pickaxeType) {
            case WOOD:
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case STONE:
                if (remove) player.getInventory().remove(Material.WOOD_PICKAXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.STONE_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case IRON:
                if (remove) player.getInventory().remove(Material.STONE_PICKAXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.IRON_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case DIAMOND:
                if (remove) player.getInventory().remove(Material.IRON_PICKAXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.DIAMOND_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            default:
                break;
        }
    }

    public void giveAxe(boolean remove) {
        Player player = getPlayer();

        switch (axeType) {
            case WOOD:
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case STONE:
                if (remove) player.getInventory().remove(Material.WOOD_AXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.STONE_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case IRON:
                if (remove) player.getInventory().remove(Material.STONE_AXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.IRON_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            case DIAMOND:
                if (remove) player.getInventory().remove(Material.IRON_AXE);
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.DIAMOND_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                break;
            default:
                break;
        }
    }

    public void giveShear() {
        Player player = getPlayer();

        if (shear) {
            player.getInventory().addItem(new ItemBuilderUtil().setType(Material.SHEARS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
        }
    }

    public void clean() {
        Player player = getPlayer();
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.setExhaustion(0.0f);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0f);
        player.setFireTicks(0);

        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(new ItemStack[4]);
        inv.setContents(new ItemStack[]{});
        player.getActivePotionEffects().forEach((potionEffect -> player.removePotionEffect(potionEffect.getType())));
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GamePlayer)) {
            return false;
        }

        GamePlayer gamePlayer = (GamePlayer) obj;
        return uuid.equals(gamePlayer.getUuid());
    }

    public static class PlayerCompass {
        @Getter
        private final GamePlayer gamePlayer;
        @Getter
        private final Player player;

        public PlayerCompass(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
            this.player = gamePlayer.getPlayer();
        }

        public void sendClosestPlayer() {
            GamePlayer closestPlayer = XBedwars.getInstance().getGame().findTargetPlayer(gamePlayer);

            if (closestPlayer != null) {
                gamePlayer.sendActionBar("§f玩家 " + closestPlayer.getGameTeam().getChatColor() + closestPlayer.getDisplayname() + " §f距离您 " + ((int) closestPlayer.getPlayer().getLocation().distance(player.getLocation())) + "m");
                player.setCompassTarget(closestPlayer.getPlayer().getLocation());
            } else {
                gamePlayer.sendActionBar("§c没有目标");
            }
        }
    }
}
