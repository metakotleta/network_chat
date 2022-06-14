import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static final String ADRESS = "127.0.0.1";
    public static final int PORT = 46555;
    final SocketChannel socketChannel;
    private String name;

    public Client(String name) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(ADRESS, PORT));
        this.name = name;
    }

    public void start() {
        try {
            Scanner scanner = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocate(2 << 10);
     //          System.out.println("Enter message:");
     //          String input = scanner.nextLine();
            socketChannel.write(ByteBuffer.wrap(Thread.currentThread().getName().getBytes(StandardCharsets.UTF_8)));
            while (true) {
                int count = socketChannel.read(buffer);
                if (count == -1) break;
                String msg = new String(buffer.array(), 0, count, StandardCharsets.UTF_8);
                System.out.println(Thread.currentThread().getName() + ": " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
