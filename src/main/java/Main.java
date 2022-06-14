import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            Client client = new Client("Vasya");

            new Thread(null, server::start, "server").start();
            new Thread(null, client::start, "client").start();
//            Thread.sleep(5000);
            new Thread(null, client::start, "client2").start();
            new Thread(null, client::start, "client3").start();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
