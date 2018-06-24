package netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by taoshiliu on 2018/6/24.
 */
public class NioClient {

    private Selector selector;

    public NioClient init(String serverIp,int port) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        selector = Selector.open();

        socketChannel.connect(new InetSocketAddress(serverIp,port));

        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        return this;
    }

    public void listen() throws IOException {
        System.out.println("客户端启动");

        while(true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while(iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                iterator.remove();

                if(selectionKey.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    if(socketChannel.isConnectionPending()) {
                        socketChannel.finishConnect();
                    }

                    socketChannel.configureBlocking(false);

                    socketChannel.write(ByteBuffer.wrap(new String("send message to server.").getBytes()));

                    socketChannel.register(selector,SelectionKey.OP_READ);

                    System.out.println("客户端连接成功");
                }else if(selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    socketChannel.read(byteBuffer);
                    byte[] data = byteBuffer.array();
                    String message = new String(data);

                    System.out.println("recevie message from server:, size:" + byteBuffer.position() + " msg: " + message);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioClient().init("127.0.0.1",9981).listen();
    }
}
