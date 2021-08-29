package cc.i9mc.xbedwars.listeners;

import cc.i9mc.dansync.DanSyncBukkit;
import cc.i9mc.watchnmslreport.BukkitReport;
import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.database.PlayerData;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import cc.i9mc.xbedwars.game.GameState;
import cc.i9mc.xbedwars.game.GameTeam;
import me.zhanshi123.globalprefix.GlobalPrefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final Game game = XBedwars.getInstance().getGame();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();
        GamePlayer gamePlayer = GamePlayer.get(player.getUniqueId());
        PlayerData playerData = gamePlayer.getPlayerData();

        int level = (playerData.getKills() * 2) + (playerData.getDestroyedBeds() * 10) + (playerData.getWins() * 15);
        String globalPrefix = ChatColor.translateAlternateColorCodes('&', XBedwars.getInstance().getChat().getPlayerPrefix(player));
        String gamePrefix = GlobalPrefix.getInstance().getCacher().get(player.getName()).getPrefix();
        cc.i9mc.dansync.database.PlayerData danData = DanSyncBukkit.getInstance().getCacher().contains(player.getName(), new cc.i9mc.dansync.database.PlayerData());
        String dan = danData == null ? "" : DanSyncBukkit.getLevel(Integer.parseInt(danData.getData().getOrDefault("level", String.valueOf(0))));

        if (game.getGameState() == GameState.RUNNING && !game.getEventManager().isOver()) {
            if (gamePlayer.isSpectator()) {
                String text = "§7[旁观者]" + gamePrefix + "§f" + gamePlayer.getDisplayname() + "§7: " + message;
                if (player.hasPermission("bw.*") || BukkitReport.getInstance().getStaffs().containsKey(player.getName())) {
                    game.broadcastMessage(text);
                    return;
                }

                game.broadcastSpectatorMessage(text);
                return;
            }

            GameTeam gameTeam = gamePlayer.getGameTeam();
            boolean all = event.getMessage().startsWith("!");
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(all ? "§6[全局]" : "§9[团队]");
            stringBuilder.append("§6[").append(XBedwars.getInstance().getLevel(level)).append("✫]");
            stringBuilder.append(gamePrefix);
            stringBuilder.append("§7[").append(dan).append("§7]");
            stringBuilder.append(globalPrefix);
            stringBuilder.append(gameTeam.getChatColor()).append("[").append(gameTeam.getName()).append("]");
            stringBuilder.append(gamePlayer.getDisplayname());
            stringBuilder.append("§7: ").append(all ? message.substring(1) : message);


            if (all) {
                game.broadcastMessage(stringBuilder.toString());
            } else {
                game.broadcastTeamMessage(gameTeam, stringBuilder.toString());
            }
            return;
        }

        game.broadcastMessage("§6[" + XBedwars.getInstance().getLevel(level) + "✫]" + gamePrefix + "§7[" + dan + "§7]" + globalPrefix + "§7" + gamePlayer.getDisplayname() + ": " + message);
    }
}
