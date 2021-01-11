package nl.codelines.example.sagapattern.order;

import nl.codelines.example.sagapattern.saga.AbstractSaga;

public class OrderSaga extends AbstractSaga<Order> {

    public OrderSaga(Object key) {
        super(key);
        subject = new Order();
    }

    public void setOrder(Order order) {
        this.subject = order;
    }
}
