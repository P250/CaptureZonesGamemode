package me.johnnyboy.capturezonesgamemode.handlers;

import me.johnnyboy.capturezonesgamemode.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreboardQueueAnimator extends BukkitRunnable {

    private Main plugin;
    private GameQueue queue;
    private FileConfiguration scoreboardConfig;
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    private int index = 0;
    private int maxIndex;
    private int delay;
    private int interval;
    private int counter = 0;

    private int playerCount;
    private int maxPlayers;
    private int seconds;
    private Scoreboard scoreboard;

    private List<String> text;
    private String title;
    private Collection<Player> players;

    private boolean configRead = false;

    public ScoreboardQueueAnimator(Main instance, GameQueue queue) {
        playerCount = queue.getPlayers().size();
        maxPlayers = queue.getMaxPlayers();
        plugin = instance;
        this.queue = queue;
        scoreboardConfig = plugin.getScoreboardConfig();
        players = queue.getPlayers();

    }

    @Override
    public void run() {
        seconds = queue.getSeconds();
        players = queue.getPlayers();
        playerCount = players.size();
        if (configRead == false) {
            delay = scoreboardConfig.getInt("scoreboard.queue_title_animation.delay");
            interval = scoreboardConfig.getInt("scoreboard.queue_title_animation.interval");
            text = scoreboardConfig.getStringList("scoreboard.queue_title_animation.text");

            configRead = true;
            maxIndex = text.size() - 1;
            title = text.get(0);
        }
        if (index > maxIndex) {
            index = 0;
        }

        if (index == 0 && counter != delay) {
            counter += 1;
            scoreboard = createScoreboard(cc(title));
            for (Player pl : players) {
                pl.setScoreboard(scoreboard);
            }
            return;
        } else if (counter == delay) {
            counter = 0;
            index++;
        }

        if (counter != interval) {
            counter += 1;
            return;
        } else if (counter == interval) {
            counter = 0;
            String title = text.get(index);
            scoreboard = createScoreboard(cc(title));
            for (Player pl : players) {
                pl.setScoreboard(scoreboard);
            }
        }


        index++;
    }

    private String cc(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private Scoreboard createScoreboard(String title) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("CaptureZones", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score queue = objective.getScore(cc("Waiting in queue. . . "));
        queue.setScore(5);
        Score padding = objective.getScore(cc("  "));
        padding.setScore(4);
        Score players = objective.getScore(cc("Players: &a" + playerCount + "&r/&a" + maxPlayers));
        players.setScore(3);
        String secondsDateFormat = secondsToTimeFormat(seconds);
        Score time = objective.getScore(cc("Starting in " + "&a" + secondsDateFormat));
        time.setScore(2);
        Score blank0 = objective.getScore("");
        blank0.setScore(1);
        Score website = objective.getScore(cc("&ewww.google.com"));
        website.setScore(0);


        return scoreboard;
    }

    private String secondsToTimeFormat(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
