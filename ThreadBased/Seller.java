import java.util.Random;

public class Seller extends Thread {
    private String sellerName;
    private StockExchange exchange;

    public Seller(String name, StockExchange exchange) {
        this.sellerName = name;
        this.exchange = exchange;
    }

    public String getSellerName() { return this.sellerName; }
    public void setSellerName(String name) { this.sellerName = name; }

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
                double askPrice = Math.round((marketPrice + (random.nextDouble() * 4 - 2)) * 100.0) / 100.0;

                Order order = new Order(symbol, OrderType.SELL, askPrice, quantity);
                exchange.placeOrder(sellerName, order);
            }
        } catch (InterruptedException e) {
            System.out.println("Seller " + sellerName + " was interrupted.");
        }
    }
}