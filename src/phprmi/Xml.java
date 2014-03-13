/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phprmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mz AnggaDunK
 */
public class Xml
{

    private String getXMLValue()
    {
        FileInputStream fis = null;
        String value = "";
        try
        {
            fis = new FileInputStream(new File("D:/xml.xml"));
            Properties prop = new Properties();
            prop.loadFromXML(fis);
            value = prop.getProperty("foo");
        }
        catch(FileNotFoundException ex)
        {
            Logger.getLogger(Xml.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(Xml.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch(IOException ex)
            {
                Logger.getLogger(Xml.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return value;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println(new Xml().getXMLValue());
    }
}
