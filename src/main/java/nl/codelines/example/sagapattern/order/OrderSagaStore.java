package nl.codelines.example.sagapattern.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codelines.example.sagapattern.saga.AbstractSagaStore;
import nl.codelines.example.sagapattern.saga.SagaFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class OrderSagaStore extends AbstractSagaStore<Order> {
    private final Map<Object, byte[]> dataStore = new HashMap<>();
    private final ObjectMapper mapper;

    public OrderSagaStore(SagaFactory<Order> factory, ObjectMapper mapper) {
        super(factory);
        this.mapper = mapper;
    }

    @Override
    protected Optional<Order> findSubject(Object id) {
        return Optional.ofNullable(dataStore.get(id)).map(this::deserialize);
    }

    @Override
    protected void storeSubject(Object id, Order subject) {
        dataStore.put(id, serialize(subject));
    }

    @Override
    public void delete(Object id) {
        dataStore.remove(id);
    }

    private Order deserialize(byte[] bytes) {
        try {
            return mapper.readValue(bytes, Order.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serialize(Order order) {
        try {
            return mapper.writeValueAsBytes(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
