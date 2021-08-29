package cc.i9mc.xbedwars.game.event;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import cc.i9mc.xbedwars.game.GamePlayer;
import cc.i9mc.xbedwars.game.GameTeam;
import cc.i9mc.xbedwars.game.timer.CompassRunnable;
import cc.i9mc.xbedwars.game.timer.GeneratorRunnable;
import cc.i9mc.xbedwars.utils.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StartEvent extends GameEvent {
    public StartEvent() {
        super("开始游戏", 5, 0);
    }

    public void excuteRunnbale(Game game, int seconds) {
        game.broadcastSound(SoundUtil.get("CLICK", "UI_BUTTON_CLICK"), 1f, 1f);
        game.broadcastTitle(1, 20, 1, "§c§l游戏即将开始", "§e§l" + seconds);
    }

    public void excute(Game game) {
        game.getEventManager().registerRunnable("团队升级", (s, c) -> GamePlayer.getOnlinePlayers().forEach(player -> {
            if (!player.isSpectator()) {

                for (GameTeam gameTeam : game.getGameTeams()) {
                    if (!player.getPlayer().getLocation().getWorld().equals(gameTeam.getSpawn().getWorld())) {
                        continue;
                    }

                    if (gameTeam.getManicMiner() > 0) {
                        XBedwars.getInstance().mainThreadRunnable(() -> gameTeam.getAlivePlayers().forEach((player1 -> player1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, gameTeam.getManicMiner())))));
                    }

                    if (gameTeam.isInTeam(player)) {
                        if (player.getPlayer().getLocation().distance(gameTeam.getSpawn()) <= 7 && gameTeam.isHealPool()) {
                            XBedwars.getInstance().mainThreadRunnable(() -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
                        }

                        continue;
                    }

                    if (player.getPlayer().getLocation().distance(gameTeam.getSpawn()) <= 20 && !gameTeam.isDead()) {
                        if (gameTeam.isTrap()) {
                            gameTeam.setTrap(false);

                            XBedwars.getInstance().mainThreadRunnable(() -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1)));
                            XBedwars.getInstance().mainThreadRunnable(() -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1)));

                            XBedwars.getInstance().mainThreadRunnable(() -> gameTeam.getAlivePlayers().forEach((player1 -> {
                                player1.sendTitle(0, 20, 0, "§c§l陷阱触发！", null);
                                player1.playSound(Sound.ENDERMAN_TELEPORT, 1, 1);
                            })));
                        }

                        if (gameTeam.isMiner()) {
                            XBedwars.getInstance().mainThreadRunnable(() -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0)));
                            gameTeam.setMiner(false);
                        }
                    }
                }
            }
        }));
        new GeneratorRunnable(game).start();
        new CompassRunnable().start();
    }
}
