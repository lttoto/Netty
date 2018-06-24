package netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by taoshiliu on 2018/6/24.
 */
public class NioServer {

    private Selector selector;

    public NioServer init(int port) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return this;
    }

    public void listen() throws IOException {
        System.out.println("服务器端启动成功");

        while(true) {
            selector.select();

            Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
            while(iterable.hasNext()) {
                SelectionKey selectionKey = iterable.next();

                iterable.remove();

                if(selectionKey.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();

                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);

                    socketChannel.write(ByteBuffer.wrap(new String("send message to client").getBytes()));

                    socketChannel.register(selector,SelectionKey.OP_READ);

                    System.out.println("客户端请求连接实践");
                }else if(selectionKey.isReadable()) {

                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(25);
                    int read = socketChannel.read(byteBuffer);
                    byte[] data = byteBuffer.array();
                    String message = new String(data);

                    System.out.println("receive message from client, size:" + byteBuffer.position() + " msg: " + message);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer().init(9981).listen();
    }
}
