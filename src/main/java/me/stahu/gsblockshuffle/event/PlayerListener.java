package me.stahu.gsblockshuffle.event;

import com.google.common.collect.Sets;
import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        if (gameStateManager.getGameState() == 1) {
            Player player = event.getPlayer();
            //TODO check if the player is standing on the right block

            String playerName = player.getName();
            Location playerLocation = player.getLocation();
            for (String blockName : gameStateManager.playerBlockMap.get(playerName)) {
                Material playerBlock = Material.getMaterial(blockName);
                if (playerBlock == playerLocation.getBlock().getType() || playerBlock == playerLocation.getBlock().getRelative(0, -1, 0).getType()) {
                    player.sendRawMessage("You are standing on the right block");
                }
            }


        }
        if (gameStateManager.getGameState() == 2) {
            //TODO implement team check
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {

    }

    //Disable PvP
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        //TODO check if game has started and if the pvp should be disabled
        if (event.getDamager() instanceof Player){
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }
}