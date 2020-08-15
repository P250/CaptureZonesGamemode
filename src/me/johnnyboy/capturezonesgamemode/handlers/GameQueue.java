package me.johnnyboy.capturezonesgamemode.handlers;

import me.johnnyboy.capturezonesgamemode.Main;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class GameQueue extends BukkitRunnable {

    private HashMap<UUID, Player> players = new HashMap<UUID, Player>();

    private Main plugin;
    private FileConfiguration config;
    private GameQueueHandler gameQueueHandler;
    private World world;
    private int playerCount = 0;
    private int maxPlayers;
    private int minPlayers;
    private int seconds;

    public GameQueue(Main instance, World world) {
        plugin = instance;
        config = plugin.getCommandConfig();
        this.world = world;
        gameQueueHandler = plugin.getGameQueueHandler();

        minPlayers = config.getInt("capturezones.queue.minplayers");
        maxPlayers = config.getInt("capturezones.queue.maxplayers");

        seconds = config.getInt("capturezones.queue.queuetime");
    }

    @Override
    public void run() {
        if (seconds == 0) {
            if (players.size() < minPlayers) {
                for (Player pl : players.values()) {
                    String error = config.getString("capturezones.queue.notenoughplayers");
                    pl.sendMessage(cc(error.replace("{prefix}", Main.PLUGIN_PREFIX)));
                }
            }
            // start game
            gameQueueHandler.removeQueue(world);
            this.cancel();
        }
        seconds -= 1;
        boolean exitLoop = false;
        for (Player pl : players.values()) {
            switch(seconds) {
                case 30:
                    pl.sendMessage(cc("&eGame starting in " + seconds + " seconds."));
                    break;
                case 20:
                    pl.sendMessage(cc("&eGame starting in " + seconds + " seconds."));
                    break;
                case 10:
                    pl.sendMessage(cc("&eGame starting in " + seconds + " seconds."));
                    break;
                case 1:
                    pl.sendMessage(cc("&eGame starting in " + seconds + " second."));
                    exitLoop = true;
                    break;
            }

            if (exitLoop) {
                break;
            }

            if (isBetween(seconds, 1, 5)) {
                pl.sendMessage(cc("&eGame starting in " + seconds + " seconds."));
            }
        }
    }

    public boolean updatePlayerCount(UUID uuid, Player pl) {
        if (playerCount > maxPlayers) {
            return false;
        }
        playerCount += 1;
        players.put(uuid, pl);

        return true;
    }

    public boolean isPlayerInQueue(UUID uuid) {
        if (players.get(uuid) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void cancelQueue() {
        cancel();
    }

    private String cc(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private boolean isBetween(int num, int lower, int upper) {
        return lower <= num && num <= upper;
    }


}
