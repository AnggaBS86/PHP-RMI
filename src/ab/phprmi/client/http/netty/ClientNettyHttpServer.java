package ab.phprmi.client.http.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Netty Http Server untuk client
 *
 * @author Angga BS
 */
public class ClientNettyHttpServer
{

    private final int port;

    /**
     * constructor class ClientNettyHttpServer
     *
     * @param port int
     */
    public ClientNettyHttpServer(int port)
    {
        this.port = port;
    }

    /**
     * void run
     */
    public void run()
    {
        ExecutorService bossExecutor = Executors.newSingleThreadExecutor();
        ExecutorService workerExecutor = Executors.newSingleThreadExecutor();

        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                bossExecutor,
                workerExecutor));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    public static void main(String[] args)
    {
        int port;
        port = 8080;
        new ClientNettyHttpServer(port).run();
        System.out.println("Server berjalan pada port " + port);
    }
}
