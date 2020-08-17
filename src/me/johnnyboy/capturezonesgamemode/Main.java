package me.johnnyboy.capturezonesgamemode;

import me.johnnyboy.capturezonesgamemode.commands.CaptureZonesCommand;
import me.johnnyboy.capturezonesgamemode.events.PlayerLeave;
import me.johnnyboy.capturezonesgamemode.handlers.GameQueueHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JavaPlugin {

    public static String PLUGIN_PREFIX;
    private HashMap<String, Object> defaults;
    private HashMap<String, Object> scoreboardDefaults;
    private FileConfiguration configCommands;
    private FileConfiguration scoreboardConfig;
    private File scoreboardConfigFile;
    private File configCommandsFile;

    private GameQueueHandler gameQueueHandler;

    private void initCommandConfig() {
        configCommandsFile = new File(getDataFolder(), "capturezones_global_config.yml");
        configCommands = YamlConfiguration.loadConfiguration(configCommandsFile);
        if (!(configCommandsFile.exists())) {
            defaults = new HashMap<String, Object>();

            ArrayList<String> usage = new ArrayList<String>();
            usage.add("&8&l==============================");
            usage.add("&r&6&lCaptureZones Commands");
            usage.add("&r&6/capturezones help - displays this panel");
            usage.add("&r&6/capturezones joingame - joins a new game");
            usage.add("&r&6/capturezones reload - reloads the config");
            usage.add("&8&l==============================");

            defaults.put("capturezones.scoreboard.title", "&l&6» CaptureZones «");

            defaults.put("capturezones.command.usage", usage);
            defaults.put("capturezones.command.prefix", "&8&l[&6CaptureZones&8]&r");
            defaults.put("capturezones.command.reload", "{prefix} &aReloaded config.");

            defaults.put("capturezones.queue.maxplayers", 8);
            defaults.put("capturezones.queue.minplayers", 2);
            defaults.put("capturezones.queue.notenoughplayers", "{prefix} &cTime extended due to not enough players.");
            defaults.put("capturezones.queue.queuetime", 60);
            defaults.put("capturezones.queue.queuejoin", "{prefix} &aJoined game queue!");
            defaults.put("capturezones.queue.alreadyinqueue", "{prefix} &cError you are already in the queue!");
            defaults.put("capturezones.queue.gamefull", "{prefix} &cError game queue is full.");
            defaults.put("capturezones.queue.queueleave", "{prefix} &cLeft the game queue.");

            configCommands.addDefaults(defaults);
            configCommands.options().copyDefaults(true);
        }

        try {
            configCommands.save(configCommandsFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error, failed to load config; disabling plugin.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void initScoreboardConfig() {
        scoreboardConfigFile = new File(getDataFolder(), "scoreboard_settings.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardConfigFile);
    }

    @Override
    public void onEnable() {
        initCommandConfig();
        initScoreboardConfig();
        gameQueueHandler = new GameQueueHandler(this);
        getCommand("capturezones").setExecutor(new CaptureZonesCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);

        PLUGIN_PREFIX = configCommands.getString("capturezones.command.prefix");
    }

    public FileConfiguration getCommandConfig() {
        return configCommands;
    }

    public GameQueueHandler getGameQueueHandler() {
        return gameQueueHandler;
    }

    @Override
    public void reloadConfig() {
        configCommands = YamlConfiguration.loadConfiguration(configCommandsFile);
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardConfigFile);

        try {
            configCommands.save(configCommandsFile);
            scoreboardConfig.save(scoreboardConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getScoreboardConfig() {
        return scoreboardConfig;
    }

}

