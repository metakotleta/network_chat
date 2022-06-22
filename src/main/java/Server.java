import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server {

    public static final String ADRESS = "192.168.31.68";
    public static final int PORT = 46555;
    final ServerSocketChannel serverSocketChannel;
    List<User> userList = new ArrayList<>();


    public Server() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(ADRESS, PORT));
    }

    public void connect() {
        try {
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                int count = selector.select();
                if (count == 0)
                    continue;

                Set<SelectionKey> sKeys = selector.selectedKeys();
                Iterator<SelectionKey> keysIter = sKeys.iterator();
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                new Thread(this::accept).start();
//                while (keysIter.hasNext()) {
//                    SelectionKey key = keysIter.next();
//                    if (key.isAcceptable()) {
//                        accept(selector);
//                        keysIter.remove();
//                    } else if (key.isReadable()) {
//                        read(selector, key, buffer);
//                        keysIter.remove();
//                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(Selector selector) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(64);
            SocketChannel channel = serverSocketChannel.accept();
            Set<SelectionKey> sKeys = selector.selectedKeys();
            Iterator<SelectionKey> keysIter = sKeys.iterator();
            while (keysIter.hasNext()) {
                SelectionKey key = keysIter.next();
                if (key.isAcceptable()) {
                    channel.write(ByteBuffer.wrap("Enter Username:".getBytes(StandardCharsets.UTF_8)));
                    channel.read(buffer);
                    System.out.println("ACCEPTED");
                    if (channel != null) {
                        channel.configureBlocking(false);
                        SelectionKey userKey = channel.register(selector, SelectionKey.OP_READ);
                        userList.add(new User(new String(buffer.array(), buffer.position(), buffer.remaining()), key));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(Selector selector, SelectionKey key, ByteBuffer buffer) throws IOException {
        try {
            ((SocketChannel) key.channel()).read(buffer);
            buffer.flip();
            for (SelectionKey innerKey : selector.keys()) {
                if (innerKey.channel().getClass().getSimpleName().startsWith("SocketChannelImpl")) {
                    ((SocketChannel) innerKey.channel()).write(buffer);
                    buffer.clear();
                }
            }
        } catch (SocketException e) {
            System.err.println(e.getMessage());
            key.channel().close();
        }
    }
}