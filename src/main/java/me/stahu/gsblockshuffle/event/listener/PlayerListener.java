package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.api.BukkitPlayerAPI;
import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    final GameController gameController;
    final PlayerManager playerManager;

    // still uses bukkit api event
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        gameController.handlePlayerMoveEvent(
                playerManager.getPlayer(new BukkitPlayerAPI(event.getPlayer()))
                        .orElseThrow(() -> new IllegalStateException("Player not found")));
    }
}
