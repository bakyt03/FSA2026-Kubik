package sk.posam.fsa.statstracker.domain;

public class StatsTrackerException extends RuntimeException {

    public enum Type {
        VALIDATION,
        NOT_FOUND,
        CONFLICT,
        UNAUTHORIZED,
        FORBIDDEN
    }

    private final Type type;

    public StatsTrackerException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
