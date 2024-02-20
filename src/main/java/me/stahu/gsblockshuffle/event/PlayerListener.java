package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.TeammateCompass;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
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
    private TeammateCompass teammateCompass;
    private YamlConfiguration settings;

    public PlayerListener(YamlConfiguration settings, GSBlockShuffle plugin, GameStateManager gameStateManager, TeamsManager teamsManager, TeammateCompass teammateCompass) {
        this.gameStateManager = gameStateManager;
        this.teamsManager = teamsManager;
        this.teammateCompass = teammateCompass;
        this.settings = settings;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        gameStateManager.handlePlayerMove(event.getPlayer());

        //update the compass for everyone
        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
            teammateCompass.updateCompass(serverPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        // TODO allow the player to comeback midgame
        Player player = event.getPlayer();
        teamsManager.removePlayerFromTeam(player, teamsManager.getTeam(player));
    }

    //Disable PvP
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        if (!settings.getBoolean("disablePvP")){return;}
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
