package me.johnnyboy.capturezonesgamemode.handlers;

import me.johnnyboy.capturezonesgamemode.Main;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;


public class GameQueueHandler {

    private HashMap<World, GameQueue> queueHandler = new HashMap<World, GameQueue>();
    private Main plugin;

    public GameQueueHandler(Main instance) {
        plugin = instance;
    }

    public void createNewGameQueue(World world, GameQueue queue) {
        if (queueHandler.get(world) == null) {
            queueHandler.put(world, queue);
        }
    }

    public boolean gameQueueActive(World world) {
        if (queueHandler.get(world) == null) {
            return false;
        } else {
            return true;
        }
    }

    public GameQueue getQueue(World world) {
        return queueHandler.get(world);
    }

    public void removeQueue(World world) {
        queueHandler.remove(world);
        return;
    }
}
