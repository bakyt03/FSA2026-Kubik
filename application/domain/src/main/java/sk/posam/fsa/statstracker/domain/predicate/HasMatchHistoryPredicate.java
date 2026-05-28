package sk.posam.fsa.statstracker.domain.predicate;

import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;

import java.util.function.Predicate;

/**
 * Returns {@code true} when a player has at least one recorded match.
 */
public class HasMatchHistoryPredicate implements Predicate<PlayerStatsSnapshot> {

    public static final HasMatchHistoryPredicate INSTANCE = new HasMatchHistoryPredicate();

    private HasMatchHistoryPredicate() {
    }

    @Override
    public boolean test(PlayerStatsSnapshot snapshot) {
        return snapshot.getMatchesPlayed() > 0;
    }
}
