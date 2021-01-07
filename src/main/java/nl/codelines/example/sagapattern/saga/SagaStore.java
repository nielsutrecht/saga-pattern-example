package nl.codelines.example.sagapattern.saga;

public interface SagaStore<T> {
    Saga<T> find(Object id);

    void store(Saga<T> saga);

    void delete(Object id);

    default void delete(Saga<T> saga) {
        delete(saga.id());
    }
}
