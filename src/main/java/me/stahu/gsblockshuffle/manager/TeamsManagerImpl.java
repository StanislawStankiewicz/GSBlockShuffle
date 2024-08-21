package me.stahu.gsblockshuffle.manager;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

@RequiredArgsConstructor
public class TeamsManagerImpl implements TeamsManager {

    final PlayersManager playersManager;

    final Set<Team> teams;

    @Override
    public void assignDefaultTeams() {
        playersManager.getPlayers().stream()
                .filter(player -> player.getTeam() == null)
                .forEach(player -> {
                            Team team = new Team(player);
                            teams.add(team);
                            player.setTeam(team);
                        }
                );
    }
}
