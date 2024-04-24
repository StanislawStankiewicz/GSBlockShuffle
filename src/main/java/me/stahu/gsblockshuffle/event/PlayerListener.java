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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final GameStateManager gameStateManager;
    private final TeamsManager teamsManager;
    private final TeammateCompass teammateCompass;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;

    public PlayerListener(YamlConfiguration settings, GSBlockShuffle plugin, GameStateManager gameStateManager, TeamsManager teamsManager, TeammateCompass teammateCompass) {
        this.gameStateManager = gameStateManager;
        this.teamsManager = teamsManager;
        this.teammateCompass = teammateCompass;
        this.settings = settings;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        gameStateManager.handlePlayerMove(event.getPlayer());

        //update the compass for everyone
        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
            teammateCompass.updateCompass(serverPlayer);
        }
    }

    // TODO Handle leaving players
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
//        teamsManager.removePlayerFromTeamAfterLeave(player);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
//        teamsManager.reAddPlayerToTeamAfterLeave(player);
        teamsManager.setScoreboard();
        teamsManager.setShowScoreboard(true);
        gameStateManager.bossBarTimer.AddPlayersToBossBar();

        if (settings.getBoolean("showTeamCompass")) {
            plugin.teammateCompass.createCompassBars();
        } else {
            plugin.teammateCompass.clearCompassBars();
        }
    }

    //Disable PvP
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        if (!settings.getBoolean("disablePvP")) {
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
}
