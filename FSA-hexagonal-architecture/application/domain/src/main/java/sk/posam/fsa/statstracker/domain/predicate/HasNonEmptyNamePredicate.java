package sk.posam.fsa.statstracker.domain.predicate;

import sk.posam.fsa.statstracker.domain.Player;

import java.util.function.Predicate;

public class HasNonEmptyNamePredicate implements Predicate<Player> {

    public static final HasNonEmptyNamePredicate INSTANCE = new HasNonEmptyNamePredicate();

    private HasNonEmptyNamePredicate() {
    }

    @Override
    public boolean test(Player player) {
        return player.getName() != null && !player.getName().isEmpty();
    }
}

