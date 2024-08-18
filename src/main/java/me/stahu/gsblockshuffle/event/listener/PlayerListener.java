package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.manager.PlayersManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    final GameController gameController;
    final PlayersManager playersManager;

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        gameController.handlePlayerMoveEvent(
                playersManager.getPlayer(event.getPlayer())
                        .orElseThrow(() -> new IllegalStateException("Player not found")));
    }
}
