package netty.bio.server;

import netty.bio.server.Server;

/**
 * Created by taoshiliu on 2018/6/23.
 */
public class ServerBoot {

    private static final int PORT = 8000;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
