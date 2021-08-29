package cc.i9mc.xbedwars.listeners;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final Game game = XBedwars.getInstance().getGame();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        game.removePlayers(GamePlayer.get(event.getPlayer().getUniqueId()));
    }
}
