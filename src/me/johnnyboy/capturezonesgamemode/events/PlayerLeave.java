package me.johnnyboy.capturezonesgamemode.events;

import me.johnnyboy.capturezonesgamemode.Main;
import me.johnnyboy.capturezonesgamemode.handlers.GameQueueHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    private Main plugin;
    private GameQueueHandler gameQueue;

    public PlayerLeave(Main instance) {
        plugin = instance;
        gameQueue = instance.getGameQueueHandler();
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player pl = e.getPlayer();

        boolean inQueue = gameQueue.isPlayerInQueue(pl.getUniqueId(), pl.getWorld());
        if (inQueue) {
            gameQueue.removePlayerFromQueue(pl.getUniqueId(), pl.getWorld());
        }
    }
}
