import java.util.*;
import java.util.concurrent.locks.*;

public class StockExchange {
  
    private final Map<Symbol, OrderBook> marketBooks = new EnumMap<>(Symbol.class);
    private final List<String> transactionHistory = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public StockExchange() {
        for (Symbol sym : Symbol.values()) {
            marketBooks.put(sym, new OrderBook());
        }
    }

    public void placeOrder(String traderId, Order newOrder) {
        try {
            Symbol symbol = newOrder.getSymbol();
            OrderBook book = marketBooks.get(symbol);

            System.out.println("[EXCHANGE] Order received from " + traderId + ": " + 
                newOrder.getType() + " " + newOrder.getQuantity() + " " + symbol + " @ $" + newOrder.getPrice());

            matchOrders(traderId, newOrder, book);

            if (newOrder.getQuantity() > 0) {
                OrderBook.OrderEntry entry = new OrderBook.OrderEntry(traderId, newOrder);
                if (newOrder.getType() == OrderType.BUY) {
                    book.getBids().add(entry);
                    book.getBids().sort((e1, e2) -> Double.compare(e2.order.getPrice(), e1.order.getPrice()));
                } else {
                    book.getAsks().add(entry);
                    book.getAsks().sort((e1, e2) -> Double.compare(e1.order.getPrice(), e2.order.getPrice()));
                }
            }
            printMarketState();
        } finally {
        }
    }

    private void matchOrders(String traderId, Order incoming, OrderBook book) {
        if (incoming.getType() == OrderType.BUY) {
            while (incoming.getQuantity() > 0 && !book.getAsks().isEmpty()) {
                OrderBook.OrderEntry bestAskEntry = book.getAsks().get(0);
                Order bestAsk = bestAskEntry.order;

                if (incoming.getPrice() >= bestAsk.getPrice()) {
                    int tradedQty = Math.min(incoming.getQuantity(), bestAsk.getQuantity());
                    double executionPrice = bestAsk.getPrice();

                    executeTrade(traderId, bestAskEntry.traderId, incoming.getSymbol(), tradedQty, executionPrice);

                    incoming.setQuantity(incoming.getQuantity() - tradedQty);
                    bestAsk.setQuantity(bestAsk.getQuantity() - tradedQty);

                    if (bestAsk.getQuantity() == 0) {
                        book.getAsks().remove(0);
                    }
                } else {
                    break;
                }
            }
        } else { 
            while (incoming.getQuantity() > 0 && !book.getBids().isEmpty()) {
                OrderBook.OrderEntry bestBidEntry = book.getBids().get(0);
                Order bestBid = bestBidEntry.order;

                if (incoming.getPrice() <= bestBid.getPrice()) {
                    int tradedQty = Math.min(incoming.getQuantity(), bestBid.getQuantity());
                    double executionPrice = bestBid.getPrice();

                    executeTrade(bestBidEntry.traderId, traderId, incoming.getSymbol(), tradedQty, executionPrice);

                    incoming.setQuantity(incoming.getQuantity() - tradedQty);
                    bestBid.setQuantity(bestBid.getQuantity() - tradedQty);

                    if (bestBid.getQuantity() == 0) {
                        book.getBids().remove(0);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void executeTrade(String buyer, String seller, Symbol symbol, int qty, double price) {
        String log = "TRANSACTION ON " + symbol + ": " + qty + " pcs @ $" + price + " (Buyer: " + buyer + ", Seller: " + seller + ")";
        transactionHistory.add(log);
        System.out.println(" >>> " + log);
    }

    public double getBestBid(Symbol symbol) {
        lock.lock();
        try {
            OrderBook book = marketBooks.get(symbol);
            return book != null ? book.getBestBid() : 100.0;
        } finally { 
            lock.unlock(); 
        }
    }

    public double getBestAsk(Symbol symbol) {
        lock.lock();
        try {
            OrderBook book = marketBooks.get(symbol);
            return book != null ? book.getBestAsk() : 105.0;
        } finally { 
            lock.unlock(); 
        }
    }

    public List<String> getHistory() {
        lock.lock();
        try { 
            return new ArrayList<>(transactionHistory); 
        } finally { 
            lock.unlock(); 
        }
    }

    public void printMarketState() {
        System.out.println("--- MARKET STATE ---");
        for (Map.Entry<Symbol, OrderBook> entry : marketBooks.entrySet()) {
            Symbol sym = entry.getKey();
            OrderBook b = entry.getValue();
            System.out.print("[" + sym + "] Bids: ");
            b.getBids().forEach(e -> System.out.print("[" + e.order.getQuantity() + " @ $" + e.order.getPrice() + "] "));
            System.out.print(" | Asks: ");
            b.getAsks().forEach(e -> System.out.print("[" + e.order.getQuantity() + " @ $" + e.order.getPrice() + "] "));
            System.out.println();
        }
        System.out.println("---------------------\n");
    }
}