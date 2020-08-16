package me.johnnyboy.capturezonesgamemode.handlers;

import me.johnnyboy.capturezonesgamemode.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class GameQueue extends BukkitRunnable {

    private HashMap<UUID, Player> players = new HashMap<UUID, Player>();

    private Main plugin;
    private FileConfiguration config;

    private ScoreboardQueueAnimator animator;

    private int playerCount = 0;
    private int maxPlayers;
    private int minPlayers;
    private int seconds;

    public GameQueue(Main instance) {
        plugin = instance;

        config = plugin.getCommandConfig();

        minPlayers = config.getInt("capturezones.queue.minplayers");
        maxPlayers = config.getInt("capturezones.queue.maxplayers");
        seconds = config.getInt("capturezones.queue.queuetime");
;
        animator = new ScoreboardQueueAnimator(plugin, this);
        animator.runTaskTimer(plugin, 0, 1L);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            if (players.size() < minPlayers) {
                for (Player pl : players.values()) {
                    String error = config.getString("capturezones.queue.notenoughplayers");
                    pl.sendMessage(cc(error.replace("{prefix}", Main.PLUGIN_PREFIX)));

                    seconds = config.getInt("capturezones.queue.queuetime");
                }
                return;
            }
            // cancel animator
            animator.cancel();
            // start game
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
                    pl.sendMessage(cc("&eGame starting in &6" + seconds + "&r&e seconds."));
                    break;
                case 10:
                    pl.sendMessage(cc("&eGame starting in &c" + seconds + "&r&e seconds."));
                    break;
                case 1:
                    pl.playSound(pl.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1, 1);
                    pl.sendMessage(cc("&eGame starting in &c" + seconds + "&e second."));
                    exitLoop = true;
                    break;
            }

            if (exitLoop) {
                break;
            }

            if (isBetween(seconds, 1, 5)) {
                pl.playSound(pl.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1, 1);
                pl.sendMessage(cc("&eGame starting in &c" + seconds + "&e seconds."));
            }
        }
    }

    public boolean updatePlayerCount(UUID uuid, Player pl) {
        if (playerCount > maxPlayers) {
            return false;
        }
        players.put(uuid, pl);
        playerCount = players.size();

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
        for (Player pl : players.values()) {
            pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        cancel();
    }

    private String cc(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private boolean isBetween(int num, int lower, int upper) {
        return lower <= num && num <= upper;
    }


    public Collection<Player> getPlayers() {
        return players.values();
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        Player pl = Bukkit.getPlayer(uuid);
        pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void cancelAnimator() {
        animator.cancel();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getSeconds() {
        return seconds;
    }

}
