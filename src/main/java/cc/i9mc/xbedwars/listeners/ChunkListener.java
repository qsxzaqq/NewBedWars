package cc.i9mc.xbedwars.listeners;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {
    private final Game game = XBedwars.getInstance().getGame();

    @EventHandler
    public void onUnload(ChunkUnloadEvent unload) {
        if (game.getGameState() != GameState.RUNNING) {
            return;
        }

        if (!game.getMapData().chunkIsInRegion(unload.getChunk().getX(), unload.getChunk().getZ())) {
            return;
        }

        unload.setCancelled(true);
    }

}
