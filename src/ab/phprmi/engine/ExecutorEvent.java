package ab.phprmi.engine;

import ab.phprmi.client.http.netty.ClientNettyHttpServer;

/**
 *
 * @author Angga BS
 */
public interface ExecutorEvent
{

    public void executePHPEngine(ClientNettyHttpServer phpEngine);
}
