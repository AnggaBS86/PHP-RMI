package ab.phprmi.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * class untuk menghandle Execution script PHP Eksekusi script masih dilakukan
 * dengan Via Runtime
 *
 * @author Angga BS
 */
public abstract class PHPExecEngine extends AbstractExecEngine
{

    public abstract void beforeExecute(PHPExecEngine engine);

    public abstract void afterExecute(PHPExecEngine engine);

    /**
     * Mendapatkan hasil dari execution script PHP via CLI
     *
     * @param exec String
     * @return String
     */
    public String getResultExec(String exec)
    {
        beforeExecute(this);

        String result = "";
        boolean cekWindowsOSName = System.getProperty("os.name").contains("Windows");

        if(cekWindowsOSName)
        {

            Process p;
            BufferedReader input = null;
            try
            {
                p = Runtime.getRuntime().exec(exec);
                input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line = input.readLine()) != null)
                {
                    result += line;
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                if(input != null)
                {
                    try
                    {
                        input.close();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else
        {
            javax.swing.JOptionPane.showMessageDialog(null, "Maaf... Sistem Operasi yang anda gunakan"
                    + " belum didukung oleh Parser Engine", "", 0);
        }

        afterExecute(this);

        return result;
    }

    public void handle()
    {
        System.out.println("handle");
    }
}
