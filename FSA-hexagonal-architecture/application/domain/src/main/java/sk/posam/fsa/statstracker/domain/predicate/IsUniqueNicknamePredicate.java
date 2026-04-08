package sk.posam.fsa.statstracker.domain.predicate;

import sk.posam.fsa.statstracker.domain.Player;

import java.util.function.BiPredicate;

public class IsUniqueNicknamePredicate implements BiPredicate<Player, Player> {

    public static final IsUniqueNicknamePredicate INSTANCE = new IsUniqueNicknamePredicate();

    private IsUniqueNicknamePredicate() {
    }

    @Override
    public boolean test(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            return true;
        }
        String nickname1 = player1.getNickname();
        String nickname2 = player2.getNickname();
        if (nickname1 == null || nickname2 == null) {
            return true;
        }
        return !nickname1.equalsIgnoreCase(nickname2);
    }
}
