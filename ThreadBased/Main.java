class Main
{
    public static void main(String[] args) {
        StockExchange exchange = new StockExchange();

        Seller seller1 = new Seller("Seller_1", exchange);
        Seller seller2 = new Seller("Seller_2", exchange);
        Buyer buyer1 = new Buyer("Buyer_1", exchange);
        Buyer buyer2 = new Buyer("Buyer_2", exchange);

        seller1.start();
        seller2.start();
        buyer1.start();
        buyer2.start();

        try {
            seller1.join();
            seller2.join();
            buyer1.join();
            buyer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== SIMULATION COMPLETED ===");
        System.out.println("Overall transaction history:");
        exchange.getHistory().forEach(System.out::println);
    }
}