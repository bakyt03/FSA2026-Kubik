package sk.posam.fsa.statstracker.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

    @Test
    void validateForCreationSucceedsWithValidNameAndNickname() {
        Player player = player("Lukas", "l0ki");

        assertDoesNotThrow(player::validateForCreation);
    }

    @Test
    void validateForCreationFailsWhenNameIsNull() {
        Player player = player(null, "l0ki");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                player::validateForCreation);

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    @Test
    void validateForCreationFailsWhenNameIsEmpty() {
        Player player = player("", "l0ki");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                player::validateForCreation);

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    @Test
    void validateForCreationFailsWhenNicknameIsNull() {
        Player player = player("Lukas", null);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                player::validateForCreation);

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    @Test
    void validateForCreationFailsWhenNicknameIsEmpty() {
        Player player = player("Lukas", "");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                player::validateForCreation);

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    private Player player(String name, String nickname) {
        Player p = new Player();
        p.setName(name);
        p.setNickname(nickname);
        return p;
    }
}
