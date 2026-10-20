import java.util.ArrayList;
import java.util.List;

public class OrderBook {
    private static final List<Order> buyOrders = new ArrayList<>();
    private static final List<Order> sellOrders = new ArrayList<>();

    public static synchronized void addOrder(Order newOrder) {
        System.out.println("Processing order: " + newOrder);

        if (newOrder.getType() == OrderType.BUY) {
            matchBuyOrder(newOrder);
        } else {
            matchSellOrder(newOrder);
        }
    }

    private static void matchBuyOrder(Order buy) {

        sellOrders.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));

        for (int i = 0; i < sellOrders.size(); i++) {
            Order sell = sellOrders.get(i);

            if (sell.getSymbol() == buy.getSymbol() && buy.getPrice() >= sell.getPrice()) {
                int matchedQuantity = Math.min(buy.getQuantity(), sell.getQuantity());

                System.out.println("\n>>> MATCH FOUND! <<< " + matchedQuantity + " shares of " + buy.getSymbol() + 
                                   " at price " + sell.getPrice()+"\n");

                buy.setQuantity(buy.getQuantity() - matchedQuantity);
                sell.setQuantity(sell.getQuantity() - matchedQuantity);

                if (sell.getQuantity() == 0) {
                    sellOrders.remove(i);
                    i--;
                }

                if (buy.getQuantity() == 0) {
                    return; 
                }
            }
        }

        if (buy.getQuantity() > 0) {
            buyOrders.add(buy);
            System.out.println("Buy order added to book (unmatched part): " + buy);
        }
    }

    private static void matchSellOrder(Order sell) {

        buyOrders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));

        for (int i = 0; i < buyOrders.size(); i++) {
            Order buy = buyOrders.get(i);

            if (buy.getSymbol() == sell.getSymbol() && sell.getPrice() <= buy.getPrice()) {
                int matchedQuantity = Math.min(sell.getQuantity(), buy.getQuantity());

                System.out.println("\n>>> MATCH FOUND! <<< " + matchedQuantity + " shares of " + sell.getSymbol() + 
                                   " at price " + buy.getPrice()+"\n");

                sell.setQuantity(sell.getQuantity() - matchedQuantity);
                buy.setQuantity(buy.getQuantity() - matchedQuantity);

                if (buy.getQuantity() == 0) {
                    buyOrders.remove(i);
                    i--;
                }

                if (sell.getQuantity() == 0) {
                    return;
                }
            }
        }

        if (sell.getQuantity() > 0) {
            sellOrders.add(sell);
            System.out.println("Sell order added to book (unmatched part): " + sell);
        }
    }
}