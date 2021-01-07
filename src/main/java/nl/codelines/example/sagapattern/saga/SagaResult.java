package nl.codelines.example.sagapattern.saga;

public interface SagaResult<T> {
    void success(Saga<T> saga);
    void failure(Saga<T> saga, Exception e);
}
