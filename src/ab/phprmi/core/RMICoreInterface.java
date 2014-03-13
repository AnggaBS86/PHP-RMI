package ab.phprmi.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface dari RMI core
 * @author Angga BS
 */
public interface RMICoreInterface extends Remote
{

    public String getResultClientProcess(String nama) throws RemoteException;

    public String getResultServerProcess(String nama) throws RemoteException;
}
