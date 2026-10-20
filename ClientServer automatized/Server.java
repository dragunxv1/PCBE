import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;

class ClientHandler extends Thread {
    private Socket socket;

    ClientHandler(Socket socket) { 
        this.socket = socket; 
    }

    public void run() {
        try (

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)
        ) {
            pw.println("Hello, please insert your name: ");

            String clientName = (String) ois.readObject();
            
            if (clientName != null && !clientName.trim().isEmpty()) {
                pw.println("Welcome to the trading server " + clientName + "!\n");
            } else {
                pw.println("Welcome to the trading server Anonymous!\n");
            }
            
            Object receivedObject;
            while ((receivedObject = ois.readObject()) != null) {
                if (receivedObject instanceof Order) {
                    Order order = (Order) receivedObject;
                    
                    System.out.println("Received order from [" + clientName + "] -> " + order);

                    OrderBook.addOrder(order);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected or connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Server {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Trading server started on port " + port + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler client = new ClientHandler(socket);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}