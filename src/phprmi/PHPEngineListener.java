/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phprmi;

import ab.phprmi.engine.PHPExecEngine;

/**
 *
 * @author Mz AnggaDunK
 */
public abstract class PHPEngineListener
{

    public abstract void phpEngine(PHPExecEngine engine);
}

class Run extends PHPEngineListener
{

    public static void main(String[] args)
    {
        PHPExecEngine engine = new PHPExecEngine()
        {

            @Override
            public void beforeExecute(PHPExecEngine engine)
            {
            }

            @Override
            public void afterExecute(PHPExecEngine engine)
            {
            }
        };
        engine.getResultExec("A");
    }

    @Override
    public void phpEngine(PHPExecEngine engine)
    {
        engine.handle();
    }
}