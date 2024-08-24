package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssignerTestsArranger {
    public static Set<Team> arrangeTeams(int teamCount, int playerCount) {
        Team[] teams = new Team[teamCount];
        for (int i = 0; i < teamCount; i++) {
            teams[i] = Mockito.mock(Team.class);
        }

        Player[][] players = new Player[teamCount][playerCount];
        for (int i = 0; i < teamCount; i++) {
            for (int j = 0; j < playerCount; j++) {
                PlayerAPI playerApi = Mockito.mock(PlayerAPI.class);
                players[i][j] = new Player(playerApi);
            }
        }

        for (int i = 0; i < teamCount; i++) {
            for (int j = 0; j < playerCount; j++) {
                Mockito.when(teams[i].getPlayers()).thenReturn(Set.of(players[i]));
            }
        }

        return Set.of(teams);
    }

    public static List<BlockPack> arrangeBlocks(int blockCount) {
        List<BlockPack> blocks = new ArrayList<>();
        for (int i = 0; i < blockCount; i++) {
            blocks.add(new BlockPack(List.of(new Block("block" + i))));
        }
        return blocks;
    }
}