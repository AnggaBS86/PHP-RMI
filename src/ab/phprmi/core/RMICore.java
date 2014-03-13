package ab.phprmi.core;

import ab.phprmi.engine.PHPExecEngine;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * class untuk handle core RMI
 * core server process dan client proses
 * @author Angga Bayu Sejati
 */
public class RMICore extends UnicastRemoteObject implements RMICoreInterface
{

    /**
     * Constructor class, panggil superclass constructor
     * @throws RemoteException 
     */
    public RMICore() throws RemoteException
    {
        super();
    }

    /**
     * Mendapatkan hasil proses dari RMI client dan memprosesnya
     * @param nama java.lang.String
     * @return java.lang.String
     * @throws RemoteException 
     */
    @Override
    public synchronized String getResultClientProcess(String nama) throws RemoteException
    {
        String resultExec = "" + new ab.phprmi.engine.PHPExecEngine()
        {

            @Override
            public void beforeExecute(PHPExecEngine engine)
            {
            }

            @Override
            public void afterExecute(PHPExecEngine engine)
            {
            }
        }.getResultExec("" + nama);
        return resultExec;
    }

    /**
     * Mengolah proses dari rmi client, yang selanjutnya hasilnya dikirim ke client lagi
     * @param nama String
     * @return String
     * @throws RemoteException 
     */
    @Override
    public synchronized String getResultServerProcess(String nama) throws RemoteException
    {
        System.out.println("Hasil DI Server : " + nama);
        String pesanBalik = "" + nama;
        return pesanBalik;
    }
}
