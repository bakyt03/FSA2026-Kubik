package sk.posam.fsa.statstracker.domain.predicate;

import java.util.List;
import java.util.function.Predicate;

public class HasExactSizePredicate implements Predicate<List<?>> {

    private final int expectedSize;

    private HasExactSizePredicate(int expectedSize) {
        this.expectedSize = expectedSize;
    }

    public static HasExactSizePredicate ofSize(int size) {
        return new HasExactSizePredicate(size);
    }

    @Override
    public boolean test(List<?> list) {
        return list != null && list.size() == expectedSize;
    }
}

