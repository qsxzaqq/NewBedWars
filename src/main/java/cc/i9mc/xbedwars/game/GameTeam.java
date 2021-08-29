package cc.i9mc.xbedwars.game;

import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameTeam {
    private final TeamColor teamColor;
    private final Location spawn;
    private final Block bedFeet;
    private final Block bedHead;
    private final BlockFace bedFace;
    private int maxPlayers;
    private boolean unbed;
    private boolean bedDestroy;
    private GamePlayer destroyPlayer;

    private int forge;
    private int manicMiner;
    private boolean sharpenedSwords;
    private int reinforcedArmor;
    private boolean healPool;
    private boolean trap;
    private boolean miner;

    public GameTeam(TeamColor teamColor, Location location, int maxPlayers) {
        this.unbed = false;

        this.sharpenedSwords = false;
        this.reinforcedArmor = 0;
        this.manicMiner = 0;
        this.miner = false;
        this.healPool = false;
        this.trap = false;

        this.spawn = location;
        this.teamColor = teamColor;
        this.maxPlayers = maxPlayers;

        List<Block> blocks = new ArrayList<>();
        for (int x = -18; x < 18; x++) {
            for (int y = -18; y < 18; y++) {
                for (int z = -18; z < 18; z++) {
                    Block block = spawn.clone().add(x, y, z).getBlock();
                    if (block != null && block.getType() == Material.BED_BLOCK) {
                        blocks.add(block);
                    }
                }
            }
        }

        Bed bedBlock = (Bed) blocks.get(0).getState().getData();
        if (!bedBlock.isHeadOfBed()) {
            bedFeet = blocks.get(0);
            bedHead = blocks.get(1);
        } else {
            bedHead = blocks.get(0);
            bedFeet = blocks.get(1);
        }
        bedFace = ((Bed) bedHead.getState().getData()).getFacing();
    }

    public ChatColor getChatColor() {
        return teamColor.getChatColor();
    }

    public DyeColor getDyeColor() {
        return teamColor.getDyeColor();
    }

    public Color getColor() {
        return teamColor.getColor();
    }

    public List<GamePlayer> getGamePlayers() {
        List<GamePlayer> gamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : GamePlayer.getGamePlayers()) {
            if (gamePlayer.getGameTeam() == this) {
                gamePlayers.add(gamePlayer);
            }
        }

        return gamePlayers;
    }

    public List<GamePlayer> getAlivePlayers() {
        List<GamePlayer> alivePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : getGamePlayers()) {
            if (gamePlayer.isOnline() && !gamePlayer.isSpectator()) {
                alivePlayers.add(gamePlayer);
            }
        }
        return alivePlayers;
    }

    public boolean isInTeam(GamePlayer gamePlayer) {
        for (GamePlayer player : getGamePlayers()) {
            if (player.equals(gamePlayer)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTeam(GamePlayer removePlayer, GamePlayer gamePlayer) {
        for (GamePlayer player : getGamePlayers()) {
            if (player.equals(gamePlayer) && !player.equals(removePlayer)) {
                return true;
            }
        }
        return false;
    }

    public boolean addPlayer(GamePlayer gamePlayer) {
        if (isFull() || isInTeam(gamePlayer)) {
            return false;
        }
        gamePlayer.setGameTeam(this);
        return true;
    }

    public boolean isFull() {
        return getGamePlayers().size() >= maxPlayers;
    }

    public boolean isDead() {
        for (GamePlayer gamePlayer : getGamePlayers()) {
            if ((gamePlayer.isOnline()) && (!gamePlayer.isSpectator())) {
                return false;
            }
        }
        return true;
    }


    public String getName() {
        return teamColor.getName();
    }
}