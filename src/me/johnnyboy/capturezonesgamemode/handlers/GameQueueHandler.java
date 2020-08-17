package me.johnnyboy.capturezonesgamemode.handlers;

import me.johnnyboy.capturezonesgamemode.Main;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;


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
        getQueue(world).cancelQueue();
        queueHandler.remove(world);
        return;
    }

    public boolean isPlayerInQueue(UUID uuid, World world) {
        GameQueue queue = queueHandler.get(world);
        if (queue != null && queue.isPlayerInQueue(uuid)) {
            return true;
        }
        return false;
    }

    public void removePlayerFromQueue(UUID uuid, World world) {
        GameQueue queue = queueHandler.get(world);

        if (queue.getPlayers().size() == 1) {
            queue.removePlayer(uuid);
            queue.cancelAnimator();
            removeQueue(world);
        }
    }
}
