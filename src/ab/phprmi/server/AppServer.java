package ab.phprmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import ab.phprmi.config.Constant;
import ab.phprmi.core.RMICore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * class untuk menghandle RMI server
 * @author Angga B.S
 */
public class AppServer
{

    public static void main(String[] a)
    {
        ExecutorService appServer = Executors.newSingleThreadExecutor();
        appServer.execute(new RunApp());
    }
}

/**
 * class untuk menghandle jalannya aplikasi
 * @author Angga BS
 */
class RunApp implements Runnable
{

    /**
     * void run
     */
    @Override
    public void run()
    {
        handle();
    }

    /**
     * void untuk menghandle jalannya aplikasi RMI server
     */
    private synchronized void handle()
    {
        try
        {
            System.out.println("Server Berjalan ");
            Registry registry = LocateRegistry.createRegistry(Constant.PORT_APPLICATION);
            registry.rebind(Constant.PHP_REGISTRY_OBJECT, new RMICore());
        }
        catch(RemoteException ex)
        {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
