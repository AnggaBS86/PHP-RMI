package phprmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author Angga BS
 */
public abstract class ClientListener
{

    public abstract String getResultRMIClient(String result) throws RemoteException, NotBoundException,
            java.rmi.ConnectException;
}
