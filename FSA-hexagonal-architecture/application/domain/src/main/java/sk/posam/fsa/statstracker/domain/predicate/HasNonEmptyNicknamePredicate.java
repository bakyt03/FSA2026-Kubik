package sk.posam.fsa.statstracker.domain.predicate;

import sk.posam.fsa.statstracker.domain.Player;

import java.util.function.Predicate;

public class HasNonEmptyNicknamePredicate implements Predicate<Player> {

    public static final HasNonEmptyNicknamePredicate INSTANCE = new HasNonEmptyNicknamePredicate();

    private HasNonEmptyNicknamePredicate() {
    }

    @Override
    public boolean test(Player player) {
        return player.getNickname() != null && !player.getNickname().isEmpty();
    }
}

