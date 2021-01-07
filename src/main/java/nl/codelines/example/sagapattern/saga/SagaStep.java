package nl.codelines.example.sagapattern.saga;

public interface SagaStep<T> {
    void apply(T t) throws SagaException;
    boolean completed();
    void compensate();
}
