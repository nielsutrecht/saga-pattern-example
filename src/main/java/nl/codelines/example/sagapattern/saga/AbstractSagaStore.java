package nl.codelines.example.sagapattern.saga;

import java.util.Optional;

public abstract class AbstractSagaStore<T> implements SagaStore<T> {
    private final SagaFactory<T> factory;

    public AbstractSagaStore(SagaFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Saga<T>> find(Object id) {
        var subject = findSubject(id);

        return subject.map((s) -> {
            var saga = factory.create(id);
            saga.subject(s);

            return saga;
        });
    }

    @Override
    public void store(Saga<T> saga) {
        storeSubject(saga.id(), saga.subject());
    }

    protected abstract Optional<T> findSubject(Object id);
    protected abstract void storeSubject(Object id, T subject);
}
