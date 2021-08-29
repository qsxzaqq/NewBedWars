package cc.i9mc.xbedwars.listeners;

import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.database.PlayerData;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import cc.i9mc.xbedwars.game.GameState;
import cc.i9mc.xbedwars.game.GameTeam;
import cc.i9mc.xbedwars.types.ToolType;
import cc.i9mc.xbedwars.utils.SoundUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReSpawnListener implements Listener {
    private final List<UUID> noDamage = new ArrayList<>();
    private final Game game = XBedwars.getInstance().getGame();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.get(player.getUniqueId());
        GameTeam gameTeam = gamePlayer.getGameTeam();
        PlayerData playerData = gamePlayer.getPlayerData();

        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        gamePlayer.clean();

        if (gameTeam.isBedDestroy()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    TextComponent textComponent = new TextComponent("§c你凉了!想再来一局嘛? ");
                    textComponent.addExtra("§b§l点击这里!");
                    textComponent.getExtra().get(0).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/queue join qc x"));
                    player.spigot().sendMessage(textComponent);

                    event.setRespawnLocation(game.getRespawnLocation());
                    player.setVelocity(new Vector(0, 0, 0));
                    player.setFallDistance(0.0F);
                    player.teleport(game.getRespawnLocation());
                    GamePlayer.getOnlinePlayers().forEach((gamePlayer1 -> gamePlayer1.getPlayer().hidePlayer(player)));

                    gamePlayer.toSpectator("§c你凉了！", "§7你没床了");
                }
            }.runTaskLater(XBedwars.getInstance(), 1L);
            playerData.addLoses();

            if (gameTeam.isDead()) {
                game.broadcastSound(SoundUtil.get("ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT"), 10, 10);
                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
                game.broadcastMessage(" ");
                game.broadcastMessage(gameTeam.getChatColor() + gameTeam.getName() + " §c凉了! §e挖床者: " + (gameTeam.getDestroyPlayer() != null ? gameTeam.getDestroyPlayer().getDisplayname() : "null"));
                game.broadcastMessage(" ");
                game.broadcastMessage("§7▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
            }

            return;
        }

        event.setRespawnLocation(game.getRespawnLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0.0F);
        player.teleport(game.getRespawnLocation());
        player.setFlying(true);

        new BukkitRunnable() {
            int delay = 5;

            @Override
            public void run() {
                if (player.isOnline()) {
                    if (this.delay > 0) {
                        TitleUtil.sendTitle(player, 1, 20, 1, "§e§l" + delay, "§7你死了,将在稍后重生");
                        this.delay -= 1;
                        return;
                    }

                    player.setExp(0f);
                    player.setLevel(0);


                    if (gamePlayer.getPickaxeType() != ToolType.NONE) {
                        ToolType toolType = gamePlayer.getPickaxeType();
                        if (toolType == ToolType.DIAMOND) {
                            gamePlayer.setPickaxeType(ToolType.IRON);
                        } else if (toolType == ToolType.IRON) {
                            gamePlayer.setPickaxeType(ToolType.STONE);
                        } else if (toolType == ToolType.STONE) {
                            gamePlayer.setPickaxeType(ToolType.WOOD);
                        }
                    }

                    if (gamePlayer.getAxeType() != ToolType.NONE) {
                        ToolType toolType = gamePlayer.getAxeType();
                        if (toolType == ToolType.DIAMOND) {
                            gamePlayer.setAxeType(ToolType.IRON);
                        } else if (toolType == ToolType.IRON) {
                            gamePlayer.setAxeType(ToolType.STONE);
                        } else if (toolType == ToolType.STONE) {
                            gamePlayer.setAxeType(ToolType.WOOD);
                        }
                    }

                    gamePlayer.giveInventory();
                    player.showPlayer(player);
                    GamePlayer.getOnlinePlayers().forEach((gamePlayer1 -> gamePlayer1.getPlayer().showPlayer(player)));
                    player.teleport(gameTeam.getSpawn());
                    player.setGameMode(GameMode.SURVIVAL);
                    noDamage.add(player.getUniqueId());

                    Bukkit.getScheduler().runTaskLater(XBedwars.getInstance(), () -> noDamage.remove(player.getUniqueId()), 60);

                    TitleUtil.sendTitle(player, 1, 20, 1, "§a已复活！", "§7因为你的床还在所以你复活了");
                }
                cancel();
            }
        }.runTaskTimer(XBedwars.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        if (noDamage.contains(evt.getEntity().getUniqueId())) {
            evt.setCancelled(true);
        }
    }
}
