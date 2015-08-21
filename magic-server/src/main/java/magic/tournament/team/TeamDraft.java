package magic.tournament.team;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;

import jersey.repackaged.com.google.common.collect.Iterables;
import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Pairing;
import magic.data.Player;
import magic.tournament.AbstractTournament;
import magic.tournament.TieBreakers;

public class TeamDraft extends AbstractTournament{

    private final Collection<Player> team1;
    private final Collection<Player> team2;

    public TeamDraft(String tournamentId, Collection<Player> team1, Collection<Player> team2) {
        super(tournamentId, team1.size(), Iterables.concat(team1, team2));
        if (team1.size() != team2.size()) {
            throw new IllegalArgumentException("Both teams must be the same size!");
        }
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(
            Multimap<Integer, Player> playersAtEachPointLevel,
            Optional<Map<Player, TieBreakers>> tieBreakers,
            Map<Player, Integer> pointsPerPlayer,
            Multimap<Player, Player> alreadyMatched) {
        return Sets.newTreeSet(team1.stream().map(team1Player -> {
            Player team2Player = team2.stream().filter(player-> !alreadyMatched.containsEntry(team1Player, player)).findFirst().get();
            return new Pairing(team1Player, team2Player, 0);
        }).collect(Collectors.toSet()));
    }
}
