import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static final String ADRESS = "192.168.31.103";
    public static final int PORT = 46555;
    final ServerSocketChannel serverSocketChannel;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private ByteBuffer buffer;


    public Server() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(ADRESS, PORT));
        buffer = ByteBuffer.allocate(2048);
    }

    public void run() {
        try {
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        SocketChannel channel = serverSocketChannel.accept();
                        channel.configureBlocking(false);
                        byte[] byteMessage = objectMapper.writeValueAsBytes(new Message("GOD", "Welcome to chat"));
                        channel.write(ByteBuffer.wrap(byteMessage));
                        channel.register(selector, SelectionKey.OP_READ);
                        iter.remove();
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        try {
                            client.read(buffer);
                            for (SelectionKey allKey : selector.keys()) {
                                if (allKey.interestOps() == SelectionKey.OP_READ) {
                                    SocketChannel writeChannel = (SocketChannel) allKey.channel();
                                    writeChannel.write(ByteBuffer.wrap(buffer.array()));
                                }
                            }
                            buffer.clear();
                            iter.remove();
                        } catch (SocketException e) {
                            client.close();
                        }
                    }
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}