package nl.codelines.example.sagapattern.examples;

import nl.codelines.example.sagapattern.order.Order;
import nl.codelines.example.sagapattern.saga.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;

@Service
public class CustomerService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final Deque<CreditResult> queue = new ArrayDeque<>();
    private final SagaStore<Order> sagaStore;

    public CustomerService(@Lazy SagaStore<Order> sagaStore) {
        this.sagaStore = sagaStore;
    }

    public SagaStep<Order> checkCreditStep(Saga<Order> saga) {
        return new SagaStep<>() {
            @Override
            public void apply(Order order) throws SagaException {
                log.info("[{}] Checking credit for customer {}", saga.id(), saga.subject().getCustomerId());
                var success = Math.random() > 0.25;

                queue.offer(new CreditResult(saga.id(), saga.subject().getCustomerId(), success));
                saga.subject().setState(Order.State.CREDIT_CHECK_SENT);
                sagaStore.store(saga);
            }

            @Override
            public boolean completed() {
                return saga.subject().completed(Order.State.CREDIT_CHECK_SENT);
            }

            @Override
            public void compensate() {
                log.info("[{}] Compensating credit check for customer {}", saga.id(), saga.subject().getCustomerId());
            }
        };
    }

    public SagaStep<Order> checkCreditCompleteStep(Saga<Order> saga) {
        return new SagaStep<>() {
            @Override
            public void apply(Order order) throws SagaException {
                if(!saga.subject().isCreditCheckSuccess()) {
                    throw new SagaException("Credit check failed for customer " + saga.subject().getCustomerId());
                } else {
                    saga.subject().setState(Order.State.CREDIT_CHECK_OKAY);
                    saga.apply();
                }
            }

            @Override
            public boolean completed() {
                return saga.subject().completed(Order.State.CREDIT_CHECK_OKAY);
            }

            @Override
            public void compensate() {
            }
        };
    }

    @Scheduled(fixedDelay = 250)
    public void receiveResult() {
        var result = queue.poll();
        if(result == null) {
            return;
        }

        log.info("[{}] Credit result for customer {}: {}", result.sagaId, result.customerId, result.success);

        var saga = sagaStore.find(result.sagaId);
        saga.subject().setCreditCheckSuccess(result.success);
        saga.apply();
    }

    private static class CreditResult {
        public final Object sagaId;
        public final String customerId;
        public final boolean success;

        public CreditResult(Object sagaId, String customerId, boolean success) {
            this.sagaId = sagaId;
            this.customerId = customerId;
            this.success = success;
        }
    }
}
