/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ab.phprmi.engine;

/**
 *
 * @author Angga BS
 */
public abstract class AbstractExecEngine
{

    public abstract void beforeExecute(PHPExecEngine engine);

    public abstract void afterExecute(PHPExecEngine engine);

    public abstract String getResultExec(String exec);
}
