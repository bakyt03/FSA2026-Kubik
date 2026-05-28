package sk.posam.fsa.statstracker.domain.predicate;

import org.junit.jupiter.api.Test;
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.player.PlayerStatsSnapshot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerPredicateTest {

    // --- HasNonEmptyNamePredicate ---

    @Test
    void hasNonEmptyNameAcceptsValidName() {
        assertTrue(HasNonEmptyNamePredicate.INSTANCE.test(player("Lukas", "l0ki")));
    }

    @Test
    void hasNonEmptyNameRejectsNullName() {
        assertFalse(HasNonEmptyNamePredicate.INSTANCE.test(player(null, "l0ki")));
    }

    @Test
    void hasNonEmptyNameRejectsEmptyName() {
        assertFalse(HasNonEmptyNamePredicate.INSTANCE.test(player("", "l0ki")));
    }

    // --- HasNonEmptyNicknamePredicate ---

    @Test
    void hasNonEmptyNicknameAcceptsValidNickname() {
        assertTrue(HasNonEmptyNicknamePredicate.INSTANCE.test(player("Lukas", "l0ki")));
    }

    @Test
    void hasNonEmptyNicknameRejectsNullNickname() {
        assertFalse(HasNonEmptyNicknamePredicate.INSTANCE.test(player("Lukas", null)));
    }

    @Test
    void hasNonEmptyNicknameRejectsEmptyNickname() {
        assertFalse(HasNonEmptyNicknamePredicate.INSTANCE.test(player("Lukas", "")));
    }

    // --- IsUniqueNicknamePredicate ---

    @Test
    void isUniqueNicknameReturnsTrueWhenNicknamesAreDifferent() {
        assertTrue(IsUniqueNicknamePredicate.INSTANCE.test(player("A", "l0ki"), player("B", "m4ros")));
    }

    @Test
    void isUniqueNicknameReturnsFalseWhenNicknamesMatch() {
        assertFalse(IsUniqueNicknamePredicate.INSTANCE.test(player("A", "l0ki"), player("B", "l0ki")));
    }

    @Test
    void isUniqueNicknameIsCaseInsensitive() {
        assertFalse(IsUniqueNicknamePredicate.INSTANCE.test(player("A", "L0KI"), player("B", "l0ki")));
    }

    @Test
    void isUniqueNicknameReturnsTrueWhenEitherPlayerIsNull() {
        assertTrue(IsUniqueNicknamePredicate.INSTANCE.test(player("A", "l0ki"), null));
        assertTrue(IsUniqueNicknamePredicate.INSTANCE.test(null, player("B", "l0ki")));
    }

    // --- IsNotNullPredicate ---

    @Test
    void isNotNullReturnsTrueForNonNullValue() {
        assertTrue(IsNotNullPredicate.getInstance().test(new Object()));
    }

    @Test
    void isNotNullReturnsFalseForNull() {
        assertFalse(IsNotNullPredicate.getInstance().test(null));
    }

    // --- HasExactSizePredicate ---

    @Test
    void hasExactSizeReturnsTrueWhenSizeMatches() {
        assertTrue(HasExactSizePredicate.ofSize(3).test(List.of(1, 2, 3)));
    }

    @Test
    void hasExactSizeReturnsFalseWhenSizeDoesNotMatch() {
        assertFalse(HasExactSizePredicate.ofSize(3).test(List.of(1, 2)));
        assertFalse(HasExactSizePredicate.ofSize(3).test(List.of(1, 2, 3, 4)));
    }

    @Test
    void hasExactSizeReturnsFalseForNullList() {
        assertFalse(HasExactSizePredicate.ofSize(3).test(null));
    }

    // --- HasNoDuplicatesPredicate ---

    @Test
    void hasNoDuplicatesReturnsTrueWhenAllElementsAreUnique() {
        assertTrue(HasNoDuplicatesPredicate.getInstance().test(List.of(1L, 2L, 3L)));
    }

    @Test
    void hasNoDuplicatesReturnsFalseWhenDuplicateExists() {
        assertFalse(HasNoDuplicatesPredicate.getInstance().test(List.of(1L, 1L, 2L)));
    }

    @Test
    void hasNoDuplicatesReturnsTrueForEmptyList() {
        assertTrue(HasNoDuplicatesPredicate.getInstance().test(List.of()));
    }

    @Test
    void hasNoDuplicatesReturnsFalseForNullList() {
        assertFalse(HasNoDuplicatesPredicate.getInstance().test(null));
    }

    // --- IsValidCsScorePredicate ---

    @Test
    void isValidCsScoreAcceptsRegulationWin() {
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(13, 5));
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(13, 0));
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(13, 11));
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(0, 13));
    }

    @Test
    void isValidCsScoreRejectsImpossibleRegulationScore() {
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(13, 12)); // loser has 12, not ≤ 11
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(10, 5)); // 10 is not a valid win score
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(0, 0));
    }

    @Test
    void isValidCsScoreAcceptsOvertimeScores() {
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(16, 12)); // OT1 min loser
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(16, 14)); // OT1 max loser
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(19, 15)); // OT2 min loser
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(19, 17)); // OT2 max loser
        assertTrue(IsValidCsScorePredicate.INSTANCE.test(22, 20)); // OT3
    }

    @Test
    void isValidCsScoreRejectsInvalidOvertimeScores() {
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(16, 15)); // tie → more OT, not a finish
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(16, 11)); // loser below OT1 minimum
        assertFalse(IsValidCsScorePredicate.INSTANCE.test(15, 13)); // 15 is not a valid win score
    }

    // --- HasMatchHistoryPredicate ---

    @Test
    void hasMatchHistoryReturnsTrueWhenMatchesPlayedIsPositive() {
        assertTrue(HasMatchHistoryPredicate.INSTANCE.test(snapshot(5)));
        assertTrue(HasMatchHistoryPredicate.INSTANCE.test(snapshot(1)));
    }

    @Test
    void hasMatchHistoryReturnsFalseWhenMatchesPlayedIsZero() {
        assertFalse(HasMatchHistoryPredicate.INSTANCE.test(snapshot(0)));
    }

    // --- helpers ---

    private PlayerStatsSnapshot snapshot(int matchesPlayed) {
        PlayerStatsSnapshot s = new PlayerStatsSnapshot();
        s.setMatchesPlayed(matchesPlayed);
        return s;
    }

    private Player player(String name, String nickname) {
        Player p = new Player();
        p.setName(name);
        p.setNickname(nickname);
        return p;
    }
}
