/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ab.phprmi.client.http.netty;

import ab.phprmi.engine.ExecutorEvent;

/**
 *
 * @author Mz AnggaDunK
 */
public class ExecutorEngine implements ExecutorEvent
{

    @Override
    public void executePHPEngine(ClientNettyHttpServer phpEngine)
    {
        phpEngine.run();
    }
}
