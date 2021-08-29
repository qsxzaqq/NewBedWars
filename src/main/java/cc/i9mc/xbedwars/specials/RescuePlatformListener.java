package cc.i9mc.xbedwars.specials;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import cc.i9mc.xbedwars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RescuePlatformListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.get(player.getUniqueId());
        Game game = XBedwars.getInstance().getGame();

        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        RescuePlatform platform = new RescuePlatform();
        if (!event.getMaterial().equals(platform.getItemMaterial())) {
            return;
        }

        platform.create(gamePlayer, game);
    }

}
