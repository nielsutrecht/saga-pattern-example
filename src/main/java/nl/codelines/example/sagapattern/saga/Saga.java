package nl.codelines.example.sagapattern.saga;

import java.util.List;

public interface Saga<T> {
    Object id();
    T subject();
    void subject(T subject);
    List<SagaStep<T>> steps();
    SagaConsumer<T> consumer();

    default boolean completed() {
        return steps().stream().allMatch(SagaStep::completed);
    }

    default void apply() {
        apply(subject());
    }

    void apply(T t);

    default void compensate() {
        steps().stream().filter(SagaStep::completed).forEach(SagaStep::compensate);
    }
}
