package nl.codelines.example.sagapattern.examples;

import nl.codelines.example.sagapattern.order.Order;
import nl.codelines.example.sagapattern.saga.Saga;
import nl.codelines.example.sagapattern.saga.SagaException;
import nl.codelines.example.sagapattern.saga.SagaStep;
import nl.codelines.example.sagapattern.saga.SagaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

@Service
public class StockService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final Deque<StockResult> queue = new ArrayDeque<>();
    private final SagaStore<Order> sagaStore;
    private final Map<String, Integer> stock = Map.of("Green Socks", 10, "Red Socks", 20, "Blue Socks", 30);

    public StockService(@Lazy SagaStore<Order> sagaStore) {
        this.sagaStore = sagaStore;
    }

    public SagaStep<Order> checkStockStep(Saga<Order> saga) {
        var service = this;
        return new SagaStep<>() {
            @Override
            public void apply(Order order) throws SagaException {
                log.info("[{}] Checking stock for customer {}, item: {}, qtty: {}",
                    saga.id(),
                    saga.subject().getCustomerId(),
                    saga.subject().getRequestedItem(),
                    saga.subject().getRequestedQuantity());

                var stock = service.stock.getOrDefault(saga.subject().getRequestedItem(), 0);
                var success = stock >= saga.subject().getRequestedQuantity();

                queue.offer(new StockResult(saga.id(), saga.subject().getCustomerId(), success));
                saga.subject().setState(Order.State.STOCK_CHECK_SENT);
                sagaStore.store(saga);
            }

            @Override
            public boolean completed() {
                return saga.subject().completed(Order.State.STOCK_CHECK_SENT);
            }

            @Override
            public void compensate() {
                log.info("[{}] Compensating stock check for customer {}", saga.id(), saga.subject().getCustomerId());
            }
        };
    }

    public SagaStep<Order> checkStockCompleteStep(Saga<Order> saga) {
        return new SagaStep<>() {
            @Override
            public void apply(Order order) throws SagaException {
                if(Math.random() < 0.25) {
                    throw new RuntimeException("Service unavailable");
                }
                if(!saga.subject().isStockAvailable()) {
                    throw new SagaException("Stock check failed for customer " + saga.subject().getCustomerId());
                } else {
                    saga.subject().setState(Order.State.STOCK_AVAILABLE);
                    saga.apply();
                }
            }

            @Override
            public boolean completed() {
                return saga.subject().completed(Order.State.STOCK_AVAILABLE);
            }

            @Override
            public void compensate() {
            }
        };
    }

    @Scheduled(fixedDelay = 100)
    public void receiveResult() {
        var todo = new ArrayList<>(queue);
        queue.clear();

        for(var result : todo) {
            log.info("[{}] Stock result for customer {}: {}", result.sagaId, result.customerId, result.success);

            var saga = sagaStore.find(result.sagaId);
            saga.subject().setStockAvailable(result.success);
            saga.apply();
        }
    }

    private static class StockResult {
        public final Object sagaId;
        public final String customerId;
        public final boolean success;

        public StockResult(Object sagaId, String customerId, boolean success) {
            this.sagaId = sagaId;
            this.customerId = customerId;
            this.success = success;
        }
    }
}
