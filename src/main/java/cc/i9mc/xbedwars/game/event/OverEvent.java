package cc.i9mc.xbedwars.game.event;

import cc.i9mc.xbedwars.events.BedwarsGameOverEvent;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GameOverRunnable;
import org.bukkit.Bukkit;

public class OverEvent extends GameEvent {
    public OverEvent() {
        super("游戏结束", 600, 6);
    }

    public void excute(Game game) {
        game.getEventManager().setCurrentEvent(7);
        Bukkit.getPluginManager().callEvent(new BedwarsGameOverEvent(game.getWinner()));
        new GameOverRunnable(game);
    }
}
