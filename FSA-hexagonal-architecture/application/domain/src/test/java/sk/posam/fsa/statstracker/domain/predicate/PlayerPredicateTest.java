package sk.posam.fsa.statstracker.domain.predicate;

import org.junit.jupiter.api.Test;
import sk.posam.fsa.statstracker.domain.Player;

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

    // --- helpers ---

    private Player player(String name, String nickname) {
        Player p = new Player();
        p.setName(name);
        p.setNickname(nickname);
        return p;
    }
}
