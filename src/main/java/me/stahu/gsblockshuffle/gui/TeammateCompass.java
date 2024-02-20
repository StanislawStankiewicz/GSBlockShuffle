package me.stahu.gsblockshuffle.gui;

import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TeammateCompass {

    public Map<String, BossBar> compassBars = new HashMap<>();

    private static String compassBarString = "--------.--------<--------^-------->";

    private TeamsManager teamsManager;

    public TeammateCompass(TeamsManager teamsManager) {
        this.teamsManager = teamsManager;
    }

    public void createCompassBars() {
        //create a bossbar for everyone
        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
            if (compassBars.get(serverPlayer.getName()) == null) {
                compassBars.put(serverPlayer.getName(), Bukkit.createBossBar("Compass", BarColor.WHITE, BarStyle.SOLID));
                BossBar compassBar = compassBars.get(serverPlayer.getName());

                compassBar.addPlayer(serverPlayer);
                compassBar.setProgress(0.5);
                compassBar.setVisible(true);
            }
        }
    }

    public void clearCompassBars() {
        for (BossBar compassBar : compassBars.values()) {
            compassBar.removeAll();
        }
        compassBars.clear();
    }

    public void updateCompass(Player player){
        BossBar compassBar = compassBars.get(player.getName());
        if (compassBar == null) {
            return;
        }
        Vector direction = player.getLocation().getDirection();
        //make the direction a vector in the xy plane
        direction.setY(0);
        direction.normalize();
        //get the angle between the direction and the x-axis
        double angle = direction.angle(new Vector(1, 0, 0)) * direction.getZ() / Math.abs(direction.getZ()) + Math.PI;
        compassBar.setTitle(addColors(getCompassString(player, angle+0.174532925)));
    }

    private int getCompassOffset(double angle) {
        return (int) (compassBarString.length() * angle / (2 * Math.PI));
    }

    private String getCompassString(Player player, double angle) {
        int offset = getCompassOffset(angle);
        String compassBarStringWithTeammates = placeTeammatesInCompass(player, compassBarString);
        return compassBarStringWithTeammates.substring(offset, compassBarStringWithTeammates.length()) + compassBarStringWithTeammates.substring(0, offset);
    }

    private String placeTeammatesInCompass(Player player, String compassBarString) {
        boolean useFirstLetterOfTeammates = true;
        for (Team team : teamsManager.getSortedTeams()) {
            System.out.println(team.getName()+": ");
            System.out.println(team.getEntries());
            System.out.println("compassbars: "+compassBars.keySet());
            if (team.hasEntry(player.getName())) {
                if (team.getEntries().size() == 1) {
                    return compassBarString;
                }else if (team.getEntries().size()==2){
                    useFirstLetterOfTeammates = false;
                }
                for (String teammate : team.getEntries()) {
                    if (!teammate.equals(player.getName())) {
                        Player teammatePlayer = Bukkit.getPlayer(teammate);
                        if (teammatePlayer != null) {
                            Vector direction = teammatePlayer.getLocation().toVector().subtract(player.getLocation().toVector());
                            direction.setY(0);
                            direction.normalize();
                            double angle = direction.angle(new Vector(1, 0, 0)) * direction.getZ() / Math.abs(direction.getZ());
                            int offset = getCompassOffset(angle);
                            offset = (offset + compassBarString.length()-1) % compassBarString.length();
                            if (offset < compassBarString.length()) {
                                compassBarString = compassBarString.substring(0, offset) + (useFirstLetterOfTeammates ? teammate.substring(0,1):"☻")  + compassBarString.substring(offset, compassBarString.length());
                            }
                        }
                    }
                }
            }
        }
        return compassBarString;
    }

    private String addColors(String compassBarString) {
        //add white before every letter and gray before every - if the letter is N add red color ☻ green
        StringBuilder coloredCompassBar = new StringBuilder();
        for (char c : compassBarString.toCharArray()) {
            if (c == '>') {
                coloredCompassBar.append(ChatColor.WHITE).append("E");
            } else if (c == '.') {
                coloredCompassBar.append(ChatColor.WHITE).append("S");
            } else if (c == '<') {
                coloredCompassBar.append(ChatColor.WHITE).append("W");
            } else if (c == '^') {
                coloredCompassBar.append(ChatColor.RED).append("N");
            } else if (c == '-') {
                coloredCompassBar.append(ChatColor.GRAY).append(c);
            } else if (c == '☻') {
                coloredCompassBar.append(ChatColor.GREEN).append(c);
            } else {
                coloredCompassBar.append(ChatColor.GREEN).append(ChatColor.BOLD).append(c);
            }
        }
        return coloredCompassBar.toString();
    }
}
