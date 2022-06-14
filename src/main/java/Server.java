import sun.nio.ch.SelectionKeyImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static final String ADRESS = "127.0.0.1";
    public static final int PORT = 46555;
    final ServerSocketChannel serverSocketChannel;

    public Server() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(ADRESS, PORT));
    }

    public void start() {
        try {
            Selector selector = Selector.open();
            while (true) {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                int count = selector.select();
                if (count != 0) {
                    System.out.println(count);
                    Set keys = selector.selectedKeys();
                    Iterator iter = keys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = (SelectionKey) iter.next();
                        if(key.isAcceptable()) {
                            System.out.println("acc");
                            SocketChannel channel = serverSocketChannel.accept();
                            ByteBuffer buffer = ByteBuffer.allocate(2 << 11);

                            while (channel.isConnected()) {
                                int count2 = channel.read(buffer);
                                if (count2 == -1) break;

                                String msg = new String(buffer.array(), 0, count2, StandardCharsets.UTF_8);
                                channel.write((ByteBuffer) buffer.flip());

                                buffer.clear();
                            }
                        } else if(key.isConnectable()) {
                            System.out.println("conn");
                        } else if(key.isReadable()) {
                            System.out.println("read");

                        } else if(key.isWritable()) {
                            System.out.println("wr");
                        }
                    }
                }






//
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}