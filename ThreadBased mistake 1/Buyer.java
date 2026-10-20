import java.util.Random;

public class Buyer extends Thread {
    private String buyerName;
    private StockExchange exchange;

    public Buyer(String name, StockExchange exchange) {
        this.buyerName = name;
        this.exchange = exchange;
    }

    public String getBuyerName() { return this.buyerName; }
    public void setBuyerName(String name) { this.buyerName = name; }

    @Override
    public void run() {
        Random random = new Random();
        Symbol[] symbols = Symbol.values();
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(random.nextInt(2000) + 1000);

                Symbol symbol = symbols[random.nextInt(symbols.length)];

                double marketPrice = (exchange.getBestBid(symbol) + exchange.getBestAsk(symbol)) / 2.0;
                int quantity = random.nextInt(10) + 1;
                double bidPrice = Math.round((marketPrice + (random.nextDouble() * 4 - 2)) * 100.0) / 100.0;

                Order order = new Order(symbol, OrderType.BUY, bidPrice, quantity);
                exchange.placeOrder(buyerName, order);
            }
        } catch (InterruptedException e) {
            System.out.println("Buyer " + buyerName + " was interrupted.");
        }
    }
}