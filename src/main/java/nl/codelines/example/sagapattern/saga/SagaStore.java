package nl.codelines.example.sagapattern.saga;

import java.util.Optional;

public interface SagaStore<T> {
    Optional<Saga<T>> find(Object id);

    void store(Saga<T> saga);

    void delete(Object id);

    default void delete(Saga<T> saga) {
        delete(saga.id());
    }
}
