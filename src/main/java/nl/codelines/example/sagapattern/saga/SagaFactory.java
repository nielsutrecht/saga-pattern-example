package nl.codelines.example.sagapattern.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SagaFactory<T> {
    private final List<Function<Saga<T>, SagaStep<T>>> stepFactories = new ArrayList<>();
    private final Function<Object, AbstractSaga<T>> sagaFunction;
    private SagaConsumer<T> result;

    public SagaFactory(Function<Object, AbstractSaga<T>> sagaFunction) {
        this.sagaFunction = sagaFunction;
    }

    public Saga<T> create(Object id) {
        var saga = sagaFunction.apply(id);

        saga.steps().addAll(stepFactories.stream().map((f) -> f.apply(saga)).collect(Collectors.toList()));
        saga.consumer = result;
        return saga;
    }

    public void add(Function<Saga<T>, SagaStep<T>> stepFactory) {
        stepFactories.add(stepFactory);
    }

    public void setResultHandler(SagaConsumer<T> result) {
        this.result = result;
    }
}
