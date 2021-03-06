package nl.codelines.example.sagapattern.order;

import nl.codelines.example.sagapattern.examples.CustomerService;
import nl.codelines.example.sagapattern.examples.StockService;
import nl.codelines.example.sagapattern.saga.SagaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class OrderSagaConfig {
    @Autowired
    private SagaFactory<Order> sagaFactory;

    @Autowired
    private ApplicationContext context;

    @Bean
    public SagaFactory<Order> sagaFactory() {
        return new SagaFactory<>(OrderSaga::new);
    }

    @PostConstruct
    public void wireOrderSagaFactory() {
        var customerService = context.getBean(CustomerService.class);
        var stockService = context.getBean(StockService.class);
        var simulator = context.getBean(OrderSimulator.class);
        sagaFactory.add(customerService::checkCreditStep);
        sagaFactory.add(customerService::checkCreditCompleteStep);
        sagaFactory.add(stockService::checkStockStep);
        sagaFactory.add(stockService::checkStockCompleteStep);
        sagaFactory.setResultHandler(simulator);
    }
}
