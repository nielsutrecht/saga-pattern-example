package nl.codelines.example.sagapattern.order;

import nl.codelines.example.sagapattern.saga.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
public class OrderSimulator implements SagaResult<Order> {
    private final Logger log = LoggerFactory.getLogger(OrderSimulator.class);
    private final SagaFactory<Order> sagaFactory;
    private final SagaStore<Order> sagaStore;

    private final Random random = new Random();

    public OrderSimulator(SagaFactory<Order> sagaFactory, SagaStore<Order> sagaStore) {
        this.sagaFactory = sagaFactory;
        this.sagaStore = sagaStore;
    }

    @Scheduled(fixedDelay = 1000)
    public void simulateOfferRequest() {

        var items = List.of("Red Socks", "Green Socks", "Blue Socks");
        var customerId = String.format("%08d", random.nextInt(1000000));
        var item = items.get(random.nextInt(items.size()));

        var saga = (OrderSaga)sagaFactory.create(null);
        var order = saga.subject();
        order.setCustomerId(customerId);
        order.setRequestedItem(item);
        order.setRequestedQuantity(10 + random.nextInt(30));

        log.info("[{}] Starting for customer: {}, item: {}, qtty: {}",
            saga.id(),
            order.getCustomerId(),
            order.getRequestedItem(),
            order.getRequestedQuantity());

        saga.apply();
    }

    @Override
    public void success(Saga<Order> saga) {
        log.info("[{}] Completed Order for Customer {}", saga.id(), saga.subject().getCustomerId());
        sagaStore.delete(saga);
    }

    @Override
    public void failure(Saga<Order> saga, Exception e) {
        log.info("[{}] Failed Order for Customer {} with Exception: '{}'", saga.id(), saga.subject().getCustomerId(), e.getMessage());
        sagaStore.delete(saga);
    }
}
