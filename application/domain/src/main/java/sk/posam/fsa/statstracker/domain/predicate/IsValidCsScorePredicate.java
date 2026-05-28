package sk.posam.fsa.statstracker.domain.predicate;

import java.util.function.BiPredicate;

/**
 * Validates a CS2 match score.
 *
 * <ul>
 * <li>Regulation: winner reaches 13, loser ≤ 11 (e.g. 13-0 through 13-11)</li>
 * <li>Overtime n: winner = 13 + 3n, loser in [12 + 3(n-1), 11 + 3n]
 * (e.g. 16-12..14, 19-15..17, 22-18..20, …)</li>
 * </ul>
 */
public class IsValidCsScorePredicate implements BiPredicate<Integer, Integer> {

    public static final IsValidCsScorePredicate INSTANCE = new IsValidCsScorePredicate();

    private IsValidCsScorePredicate() {
    }

    @Override
    public boolean test(Integer s1, Integer s2) {
        int max = Math.max(s1, s2);
        int min = Math.min(s1, s2);

        // Regulation: first team to 13, loser must have ≤ 11
        if (max == 13 && min <= 11)
            return true;

        // Overtime n (n=1 → 16/12-14, n=2 → 19/15-17, n=3 → 22/18-20, …)
        for (int n = 1; n <= 20; n++) {
            int win = 13 + 3 * n;
            int loMin = 12 + 3 * (n - 1);
            int loMax = win - 2;
            if (max == win && min >= loMin && min <= loMax)
                return true;
        }
        return false;
    }
}
