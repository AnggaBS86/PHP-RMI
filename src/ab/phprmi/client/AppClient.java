package ab.phprmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import ab.phprmi.config.Constant;
import ab.phprmi.core.RMICoreInterface;
import phprmi.ClientListener;

/**
 * class untuk menghandle RMI client
 *
 * @author Angga Bayu Sejati
 */
public class AppClient extends ClientListener
{

    /**
     * handle RMI Client, untuk mendapatkan response dari RMI server
     *
     * @param result String
     * @return String
     * @throws RemoteException
     * @throws NotBoundException
     * @throws java.rmi.ConnectException
     */
    @Override
    public String getResultRMIClient(String result) throws RemoteException, NotBoundException,
            java.rmi.ConnectException
    {
        String resultClient = "";

        Registry registry = LocateRegistry.getRegistry(Constant.SERVER_ADDR, Constant.PORT_APPLICATION);
        RMICoreInterface phpRMIUploadObject = (RMICoreInterface) registry.lookup(Constant.PHP_REGISTRY_OBJECT);
        resultClient = phpRMIUploadObject.getResultServerProcess(result);

        return resultClient;
    }
}
