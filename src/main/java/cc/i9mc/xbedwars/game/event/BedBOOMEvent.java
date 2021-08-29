package cc.i9mc.xbedwars.game.event;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GameTeam;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BedBOOMEvent extends GameEvent {
    public BedBOOMEvent() {
        super("床自毁", 360, 5);
    }

    @Override
    public void excute(Game game) {
        XBedwars.getInstance().mainThreadRunnable(() -> {
            for (GameTeam gameTeam : game.getGameTeams()) {
                if (gameTeam.isBedDestroy()) continue;
                gameTeam.getBedHead().setType(Material.AIR);
                gameTeam.getBedFeet().setType(Material.AIR);
                gameTeam.setBedDestroy(true);
            }
        });

        game.broadcastSound(Sound.ENDERDRAGON_GROWL, 1, 1);
        game.broadcastTitle(10, 20, 10, "§c§l床自毁", "§e所有队伍床消失");
    }
}
