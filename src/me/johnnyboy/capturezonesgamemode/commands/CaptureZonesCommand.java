package me.johnnyboy.capturezonesgamemode.commands;

import me.johnnyboy.capturezonesgamemode.Main;
import me.johnnyboy.capturezonesgamemode.handlers.GameQueue;
import me.johnnyboy.capturezonesgamemode.handlers.GameQueueHandler;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class CaptureZonesCommand implements CommandExecutor {

    private Main plugin;
    private FileConfiguration config;
    private FileConfiguration gameConfig;
    private GameQueueHandler gameQueueHandler;
    private GameQueue queue;

    public CaptureZonesCommand(Main instance) {
        plugin = instance;
        config = instance.getCommandConfig();
        gameConfig = instance.getGameConfig();
        gameQueueHandler = instance.getGameQueueHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player pl = (Player) sender;

        // HELP COMMAND
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            List<String> usage = config.getStringList("capturezones.command.usage");
            for (int i = 0; i < usage.size(); i++) {
                String line = usage.get(i);
                pl.sendMessage(cc(line));
            }
            return true;
        }

        // RELOAD CONFIG COMMAND
        if (args.length == 1 && args[0].equals("reload") || args[0].equals("reloadconfig")) {
            String message = config.getString("capturezones.command.reload");
            plugin.reloadConfig();
            sender.sendMessage(cc(message.replace("{prefix}", Main.PLUGIN_PREFIX)));
            return true;
        }

        if (args.length == 1 && args[0].equals("join") || args[0].equalsIgnoreCase("joinqueue") || args[0].equals("joingame")) {
            World world = pl.getWorld();
            boolean queueActive = gameQueueHandler.gameQueueActive(world);

            // checks if there's a queue active for the world the player is in, if not, make one.
            if (!(queueActive)) {
                queue = new GameQueue(plugin);
                queue.runTaskTimer(plugin, 0L, 20L);
                gameQueueHandler.createNewGameQueue(world, queue);
            }

            // gets the queue and checks if the player is in it already
            GameQueue gameQueue = gameQueueHandler.getQueue(world);
            boolean isPlayerInQueue = gameQueue.isPlayerInQueue(pl.getUniqueId());
            if (isPlayerInQueue) {
                String error = config.getString("capturezones.queue.alreadyinqueue");
                pl.sendMessage(cc(error.replace("{prefix}", Main.PLUGIN_PREFIX)));
                return true;
            }

            // attempts to add the player and returns false if the queue is full
            boolean addPlayer = gameQueue.updatePlayerCount(pl.getUniqueId(), pl);
            if (!(addPlayer)) {
                String error = config.getString("capturezones.queue.gamefull");
                pl.sendMessage(cc(error.replace("{prefix}", Main.PLUGIN_PREFIX)));
                return true;
            }

            String joinedQueueMessage = config.getString("capturezones.queue.queuejoin");
            pl.sendMessage(cc(joinedQueueMessage.replace("{prefix}", Main.PLUGIN_PREFIX)));
            
        }
        // leave queue/game command
        if (args.length == 1 && args[0].equals("leave") || args[0].equals("leavegame") || args[0].equals("leavequeue")) {
            boolean inQueue = gameQueueHandler.isPlayerInQueue(pl.getUniqueId(), pl.getWorld());
            if (inQueue) {
                gameQueueHandler.removePlayerFromQueue(pl.getUniqueId(), pl.getWorld());

                String success = config.getString("capturezones.queue.queueleave");
                pl.sendMessage(cc(success.replace("{prefix}", Main.PLUGIN_PREFIX)));
                return true;
            }
            return true;
            // TODO
            // boolean inGame
            // if (inGame)
        }

        if (args.length == 1 && args[0].equals("setbluespawn") || args[0].equals("setredspawn")) {
            String team = args[0];
            String teamPath = getTeamPath(team);

            double x = pl.getLocation().getX();
            double y = pl.getLocation().getY();
            double z = pl.getLocation().getZ();

            for (String key : gameConfig.getConfigurationSection(teamPath).getKeys(true)) {
                switch (key) {
                    case "x":
                        gameConfig.set(teamPath + ".x", x);
                        break;
                    case "y":
                        gameConfig.set(teamPath + ".y", y);
                        break;
                    case "z":
                        gameConfig.set(teamPath + ".z", z);
                        break;
                }
            }

            String teamName = getTeamColour(teamPath);

            String stringX = "&b" + String.valueOf(Math.round(x));
            String stringY = String.valueOf(Math.round(y));
            String stringZ = String.valueOf(Math.round(z));

            String success = config.getString("capturezones.command.set_team_spawn_point");
            pl.sendMessage(cc(success.replace("{prefix}", Main.PLUGIN_PREFIX).replace("{team}", teamName).replace("{x}", stringX).replace("{y}", stringY).replace("{z}", stringZ)));

            System.out.println("X: " + x + " Y: " + y + " Z: " + z);

            plugin.saveGameConfig();

        }

        return true;
    }

    private String getTeamPath(String str) {
        if (str.equalsIgnoreCase("setbluespawn")) {
            return "capturezones.spawnpoints.blue";
        } else if (str.equalsIgnoreCase("setredspawn")) {
            return "capturezones.spawnpoints.red";
        } else {
            return "";
        }
    }

    private String getTeamColour(String str) {
        String teamName = str.substring(str.lastIndexOf(".") + 1);
        if (teamName.equals("blue")) {
            return "&9" + teamName;
        } else if (teamName.equals("red")) {
            return "&c" + teamName;
        }
        return "&cerror, {team} unresolved";
    }

    private String cc (String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
