import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();

            new Thread(null, server::connect, "server").start();
            Thread.sleep(5000);



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
