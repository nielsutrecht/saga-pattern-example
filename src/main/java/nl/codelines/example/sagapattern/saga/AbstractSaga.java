package nl.codelines.example.sagapattern.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSaga<T> implements Saga<T>  {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    protected T subject;
    protected Object id;
    protected List<SagaStep<T>> steps = new ArrayList<>();
    protected SagaResult<T> result;

    public AbstractSaga(Object id) {
        if(id == null) {
            this.id = String.format("%08X", NEXT_ID.incrementAndGet());
        } else {
            this.id = id;
        }
    }

    @Override
    public void apply(T t) {
        if (completed()) {
            if(result != null) {
                result.success(this);
            }
            return;
        }
        var step = steps().stream().filter((s) -> !s.completed()).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No steps left"));

        try {
            step.apply(t);
        } catch (Exception e) {
            compensate();
            if(result != null) {
                result.failure(this, e);
            }
        }
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public T subject() {
        return subject;
    }

    @Override
    public List<SagaStep<T>> steps() {
        return steps;
    }

    @Override
    public SagaResult<T> result() {
        return result;
    }
}
