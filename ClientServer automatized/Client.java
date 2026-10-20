import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Random;

class ServerListener extends Thread {
    private Socket socket;

    ServerListener(Socket socket) { 
        this.socket = socket; 
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;

            while ((message = br.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("\nConnection to server lost.");
        }
    }
}

public class Client {
    private static final Random random = new Random();

    public static void main(String[] args) {
        int port = 5000;
        String serverIP = "127.0.0.1";

        try (Socket socket = new Socket(serverIP, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            
            ServerListener listener = new ServerListener(socket);
            listener.start();
            
            String traderName = consoleReader.readLine();
            if (traderName == null || traderName.trim().isEmpty()) {
                traderName = "Anonymous";
            }
            
            oos.writeObject(traderName);
            oos.flush();

            System.out.println("Connected! Sending orders automatically...");

            while (true) {
                Symbol symbol = Symbol.values()[random.nextInt(Symbol.values().length)];
                OrderType type = OrderType.values()[random.nextInt(OrderType.values().length)];
                double price = random.nextDouble() * 5 + 10;
                int quantity = (random.nextInt(10) + 1) * 10;

                Order order = new Order(symbol, type, price, quantity);
                
                oos.writeObject(order);
                oos.flush();
                
                System.out.println("Order sent automatically -> Symbol: " + symbol + ", Type: " + type + ", Price: " + String.format("%.2f", price) + ", Quantity: " + quantity);

                Thread.sleep(2000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}