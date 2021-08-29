package cc.i9mc.xbedwars.commands;

import cc.i9mc.xbedwars.XBedwars;
import cc.i9mc.xbedwars.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player) || commandSender.getName().equals("SuperPi")) {
            Game game = XBedwars.getInstance().getGame();

            game.setForceStart(true);
            game.start();
        }
        return false;
    }
}
