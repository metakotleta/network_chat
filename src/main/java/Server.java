import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static final String ADRESS = "192.168.31.133";
    public static final int PORT = 46555;
    final ServerSocketChannel serverSocketChannel;
    Selector chSelector = Selector.open();


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

                while (keysIter.hasNext()) {
                    SelectionKey key = keysIter.next();
                    if (key.isAcceptable()) {
                        accept(selector);
                        keysIter.remove();
                    }
                    else if (key.isReadable()) {
                        read(selector, key, buffer);
                        keysIter.remove();
                    } else {
                        System.out.println("ELSE");
                    }
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(Selector selector) throws IOException {
        SocketChannel channel = serverSocketChannel.accept();
        System.out.println("ACCEPTED");
        if (channel != null) {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }
    }

    private void read(Selector selector, SelectionKey key, ByteBuffer buffer) throws IOException {
        ((SocketChannel) key.channel()).read(buffer);
        buffer.flip();
        Iterator<SelectionKey> sKeysInner = selector.keys().iterator();
        while (sKeysInner.hasNext()) {
            SelectionKey innerKey = sKeysInner.next();
            if (innerKey.channel().getClass().getSimpleName().startsWith("SocketChannelImpl") &&
                    innerKey != key) {
                ((SocketChannel) innerKey.channel()).write(buffer);
                buffer.clear();
            }
        }
    }
}