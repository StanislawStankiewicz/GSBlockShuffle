package me.stahu.gsblockshuffle.view.cli;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class MessageBuilder {

    private final LocalizationManager localizationManager;

    public TextComponent buildBlockAssignmentMessage (Block block) {
        String blockName = ChatColor.GOLD +
                block.names().get(0)
                    .replace("_", " ");
        String template = localizationManager.getMessage("block_assignment");
        String messageText = String.format(template, blockName);
        return new TextComponent(messageText);
    }

    public TextComponent buildGameStartMessage() {
        return new TextComponent(localizationManager.getMessage("game_start"));
    }

    public TextComponent buildEndGameMessage() {
        return new TextComponent(localizationManager.getMessage("game_end"));
    }

    public TextComponent buildEndScoresMessage(Set<Team> teams) {
        TextComponent endMessage = new TextComponent(localizationManager.getMessage("end_scores"));
        int currentPlace = 0;
        int previousPlace = -1;
        int place;
        int previousScore = -1;

        List<Team> sortedTeams = teams.stream()
                .sorted((team1, team2) -> Integer.compare(team2.getScore(), team1.getScore()))  // Sort in descending order
                .toList();

        for (Team team : sortedTeams) {
            currentPlace++;
            place = team.getScore() == previousScore ? previousPlace : currentPlace;
            previousScore = team.getScore();
            previousPlace = place;
            ChatColor rankColor = switch (currentPlace) {
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.GRAY;
                case 3 -> ChatColor.RED;
                default -> ChatColor.DARK_GRAY;
            };
            endMessage.addExtra("\n    " + rankColor + place + ChatColor.RESET + ". " + team.getName() + ": " + team.getScore());
        }
        return endMessage;
    }

    public TextComponent buildBlockFoundMessage(Player player, Block block) {
        String messageText = String.format(localizationManager.getMessage("block_found"),
                player.getDisplayName(),
                ChatColor.GOLD + block.names().get(0).replace("_", " "));
        return new TextComponent(messageText);
    }
}
