package sk.posam.fsa.statstracker.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerStatsSnapshotTest {

    @Test
    void applyNeutralValuesSetsAllThreeFields() {
        PlayerStatsSnapshot snapshot = new PlayerStatsSnapshot();
        snapshot.setAvgHltvRating(0.0);
        snapshot.setAvgAdr(0.0);
        snapshot.setAvgKdRatio(0.0);

        snapshot.applyNeutralValues();

        assertEquals(1.0, snapshot.getAvgHltvRating(), 1e-9);
        assertEquals(70.0, snapshot.getAvgAdr(), 1e-9);
        assertEquals(1.0, snapshot.getAvgKdRatio(), 1e-9);
    }

    @Test
    void applyNeutralValuesOverwritesPreviousValues() {
        PlayerStatsSnapshot snapshot = new PlayerStatsSnapshot();
        snapshot.setAvgHltvRating(2.5);
        snapshot.setAvgAdr(150.0);
        snapshot.setAvgKdRatio(3.0);

        snapshot.applyNeutralValues();

        assertEquals(1.0, snapshot.getAvgHltvRating(), 1e-9);
        assertEquals(70.0, snapshot.getAvgAdr(), 1e-9);
        assertEquals(1.0, snapshot.getAvgKdRatio(), 1e-9);
    }
}
