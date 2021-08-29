package cc.i9mc.xbedwars.listeners;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ServerListener implements Listener {

    @EventHandler
    public void onSpawnMobHub(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND && event.getEntityType() != EntityType.VILLAGER) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }
}
