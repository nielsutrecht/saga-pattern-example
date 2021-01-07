package nl.codelines.example.sagapattern.saga;

public class SagaException extends Exception {
    public SagaException() { }

    public SagaException(String message) {
        super(message);
    }

    public SagaException(String message, Exception cause) {
        super(message, cause);
    }
}
