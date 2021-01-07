package nl.codelines.example.sagapattern.order;

public class Order {
    private String customerId;
    private String requestedItem;
    private int requestedQuantity;

    private boolean creditCheckSuccess;
    private boolean stockAvailable;

    private State state = State.INITIAL;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isCreditCheckSuccess() {
        return creditCheckSuccess;
    }

    public void setCreditCheckSuccess(boolean creditCheckSuccess) {
        this.creditCheckSuccess = creditCheckSuccess;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean completed(State state) {
        return this.state.ordinal() >= state.ordinal();
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getRequestedItem() {
        return requestedItem;
    }

    public void setRequestedItem(String requestedItem) {
        this.requestedItem = requestedItem;
    }

    public boolean isStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(boolean stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public enum State {
        INITIAL,
        CREDIT_CHECK_SENT,
        CREDIT_CHECK_OKAY,
        STOCK_CHECK_SENT,
        STOCK_AVAILABLE
    }
}
