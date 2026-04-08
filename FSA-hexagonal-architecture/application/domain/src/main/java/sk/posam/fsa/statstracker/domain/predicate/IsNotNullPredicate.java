package sk.posam.fsa.statstracker.domain.predicate;

import java.util.function.Predicate;

public class IsNotNullPredicate<T> implements Predicate<T> {

    @SuppressWarnings("rawtypes")
    public static final IsNotNullPredicate INSTANCE = new IsNotNullPredicate();

    @SuppressWarnings("unchecked")
    public static <T> IsNotNullPredicate<T> getInstance() {
        return INSTANCE;
    }

    private IsNotNullPredicate() {
    }

    @Override
    public boolean test(T value) {
        return value != null;
    }
}


