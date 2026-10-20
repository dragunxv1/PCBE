import java.util.*;
import java.util.concurrent.locks.*;

public class OrderBook {
    private final List<OrderEntry> bids = new ArrayList<>();
    private final List<OrderEntry> asks = new ArrayList<>();

    public static class OrderEntry {
        String traderId;
        Order order;
        public OrderEntry(String traderId, Order order) {
            this.traderId = traderId;
            this.order = order;
        }
    }

    public List<OrderEntry> getBids() { return bids; }
    public List<OrderEntry> getAsks() { return asks; }

    public double getBestBid() {
        return bids.isEmpty() ? 100.0 : bids.get(0).order.getPrice();
    }

    public double getBestAsk() {
        return asks.isEmpty() ? 105.0 : asks.get(0).order.getPrice();
    }
}