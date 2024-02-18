package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.team.TeamsManager;
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
    private final GameStateManager gameStateManager;
    private final TeamsManager teamsManager;

    public PlayerListener(YamlConfiguration settings, GSBlockShuffle plugin, GameStateManager gameStateManager, TeamsManager teamsManager) {
        this.gameStateManager = gameStateManager;
        this.teamsManager = teamsManager;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (gameStateManager.getGameState() == 0) {
            return;
        }

        Player player = event.getPlayer();
        String playerName = player.getName();
        Location playerLocation = player.getLocation();

        if (!teamsManager.isPlayerInAnyTeam(player)) {
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
        // TODO allow the player to comeback midgame
        Player player = event.getPlayer();
        teamsManager.removePlayerFromTeam(player, teamsManager.getPlayerTeam(player));
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