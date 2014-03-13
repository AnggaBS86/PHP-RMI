/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ab.phprmi.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Mz AnggaDunK
 */
public class ScriptMgr
{

    public static void main(String[] args)
    {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("Javascript");
        try
        {
            engine.eval("");
        }
        catch(ScriptException ex)
        {
            ex.printStackTrace();
        }
    }
}
