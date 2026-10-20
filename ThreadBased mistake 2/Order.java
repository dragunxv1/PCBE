
public class Order
{
    private Symbol symbol;
    private OrderType type;
    private double price;
    private int quantity;

    public Order(Symbol symbol, OrderType type, double price, int quantity)
    {
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public Symbol getSymbol() {return symbol;}
    public OrderType getType() {return type;}
    public double getPrice() {return price;}
    public int getQuantity() {return quantity;}

    public void setSymbol(Symbol symbol){this.symbol = symbol;}
    public void setType(OrderType type){this.type = type;}
    public void setPrice(double price){this.price = price;}
    public void setQuantity(int quantity){this.quantity = quantity;}

    @Override
    public String toString() {
        return "Order{" + "symbol=" + symbol + ", type=" + type + ", price=" + String.format("%.2f", price) + ", quantity=" + quantity + '}';
    }
}