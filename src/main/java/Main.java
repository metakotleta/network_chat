import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();

            new Thread(null, server::connect, "server").start();
            Thread.sleep(5000);
//            Client client = new Client("Vasya");
//            new Thread(null, client::start, "client").start();
//            new Thread(null, client::start, "client2").start();
//            new Thread(null, client::start, "client3").start();


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
