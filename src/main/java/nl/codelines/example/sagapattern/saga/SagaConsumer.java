package nl.codelines.example.sagapattern.saga;

public interface SagaConsumer<T> {
    void success(Saga<T> saga);
    void failure(Saga<T> saga, Exception e);
}
