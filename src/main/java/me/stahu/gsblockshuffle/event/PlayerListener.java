package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;
    private final GameStateManager gameStateManager;

    public PlayerListener(YamlConfiguration settings, GSBlockShuffle plugin, GameStateManager gameStateManager) {
        this.settings = settings;
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (gameStateManager.getGameState() == 0) {
            return;
        }

        Player player = event.getPlayer();
        String playerName = player.getName();
        Location playerLocation = player.getLocation();

        if (!gameStateManager.playersWithATeam.contains(player)) {
            return;
        }
        if (gameStateManager.playerBlockMap.get(playerName) == null) {
            return;
        }

        for (String blockName : gameStateManager.playerBlockMap.get(playerName)) {
            Material playerBlock = Material.getMaterial(blockName);
            if (playerBlock == playerLocation.getBlock().getType() || playerBlock == playerLocation.getBlock().getRelative(0, -1, 0).getType()) {
                player.sendRawMessage("You are standing on the right block");
                gameStateManager.playerFoundBlock(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {

    }

    //Disable PvP
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        //TODO check if game has started and if the pvp should be disabled
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }
}