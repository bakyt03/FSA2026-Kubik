package sk.posam.fsa.statstracker.domain.predicate;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class HasNoDuplicatesPredicate<T> implements Predicate<List<T>> {

    @SuppressWarnings("rawtypes")
    public static final HasNoDuplicatesPredicate INSTANCE = new HasNoDuplicatesPredicate();

    private HasNoDuplicatesPredicate() {
    }

    @SuppressWarnings("unchecked")
    public static <T> HasNoDuplicatesPredicate<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean test(List<T> list) {
        return list != null && new HashSet<>(list).size() == list.size();
    }
}

