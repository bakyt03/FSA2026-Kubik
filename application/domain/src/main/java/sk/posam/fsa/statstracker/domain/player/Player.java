package sk.posam.fsa.statstracker.domain.player;

import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.predicate.HasNonEmptyNamePredicate;
import sk.posam.fsa.statstracker.domain.predicate.HasNonEmptyNicknamePredicate;

public class Player {
    private Long id;
    private String name;
    private String nickname;
    private String keycloakId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public void validateForCreation() {
        if (!HasNonEmptyNamePredicate.INSTANCE.test(this)) {
            throw new StatsTrackerException(StatsTrackerException.Type.VALIDATION,
                    "Player name must not be null or empty");
        }
        if (!HasNonEmptyNicknamePredicate.INSTANCE.test(this)) {
            throw new StatsTrackerException(StatsTrackerException.Type.VALIDATION,
                    "Player nickname must not be null or empty");
        }
    }
}
