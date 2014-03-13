package ab.phprmi.client.http.sun;

import ab.phprmi.client.AppClient;
import ab.phprmi.config.ResponseConstant;
import ab.phprmi.engine.PHPExecEngine;
import ab.phprmi.engine.PHPSourceParser;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * handle client http server dengan sun httpserver
 * @author Angga BS
 */
public class ClientHttpServer
{

    public static void main(String[] args) throws IOException
    {
        Thread t1 = new Thread(new RunServer());
        t1.setName("Thread jalankan");
        t1.start();
    }
}

/**
 *Hanlde executor server
 * @author Angga BS
 */
class RunServer implements Runnable
{

    ExecutorService executor;

    /**
     * void untuk menjalankan Executor
     */
    @Override
    public void run()
    {
        executor = Executors.newCachedThreadPool();
        try
        {
            InetSocketAddress addr = new InetSocketAddress(8080);
            HttpServer server = HttpServer.create(addr, 0);
            server.createContext("/", new HandleHttpClientRequest());
            server.setExecutor(executor);
            server.start();
            System.out.println("Server is listening on port 8080");
        }
        catch(IOException ex)
        {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/**
 * 
 * @author Angga BS
 */
class HandleHttpClientRequest implements HttpHandler
{

    final String cliCommand = ab.phprmi.config.Constant.PHP_INTERPRETER_FILE + " "
            + "" + ab.phprmi.config.Constant.PHP_RUN_COMMAND_OPTION;
    final String documentRoot = ab.phprmi.config.Constant.DOCUMENT_ROOT;

    /**
     * void untuk menghandle request
     * @param exchange HttpExchange
     */
    public void handle(HttpExchange exchange)
    {

        synchronized(this)
        {
            String uri = exchange.getRequestURI().toASCIIString();
            try
            {
                uri = URLDecoder.decode(uri, "UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                try
                {
                    uri = URLDecoder.decode(uri, "ISO-8859-1");
                }
                catch(UnsupportedEncodingException e1)
                {
                    Headers responseHeaders = exchange.getResponseHeaders();
                    responseHeaders = exchange.getResponseHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    OutputStream responseBody = exchange.getResponseBody();

                    try
                    {
                        responseBody.write(e1.toString().getBytes());
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(HandleHttpClientRequest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    finally
                    {
                        if(responseBody != null)
                        {
                            try
                            {
                                responseBody.close();
                            }
                            catch(IOException ex)
                            {
                                Logger.getLogger(HandleHttpClientRequest.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(responseHeaders != null)
                        {
                            responseHeaders.clear();
                        }
                        if(exchange != null)
                        {
                            exchange.close();
                        }
                    }
                }
            }

            String filePath = documentRoot + uri;
            int lastIndex = 0;
            String path = "";

            if(filePath.contains("?"))
            {
                lastIndex = filePath.lastIndexOf("?");
                path = filePath.substring(0, lastIndex).trim();
                System.out.println("" + path);
            }
            else
            {
                path = filePath;
                System.out.println("Last path " + path);
            }
            try
            {
                this.handlePHPRequest(exchange, path);
            }
            catch(IOException ex)
            {
                try
                {
                    Headers responseHeaders = exchange.getResponseHeaders();
                    responseHeaders = exchange.getResponseHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    OutputStream responseBody = exchange.getResponseBody();

                    String stackTrace = "";
                    for(int i = 1; i < ex.getStackTrace().length; i++)
                    {
                        stackTrace += ex.getStackTrace()[i] + "<br/>";
                    }

                    responseBody.write(ResponseConstant.internalServerError("IOException", stackTrace).getBytes());

                }
                catch(IOException ex1)
                {
                    ex1.printStackTrace();
                }
            }
        }
    }

    /**
     * void untuk menghandle PHP request
     * @param exchange HttpExchange
     * @param path String
     * @throws IOException 
     */
    private void handlePHPRequest(HttpExchange exchange, String path) throws IOException
    {
        String hasil = "";
        String phpHome = cliCommand;
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, 0);

        OutputStream responseBody = exchange.getResponseBody();

        System.err.println("Request waktu per " + new java.util.Date().getHours() + ":"
                + new java.util.Date().getMinutes() + ":" + new java.util.Date().getSeconds());

        File f1 = new File(path);

        if(f1.exists())
        {
            String stringResult = new PHPSourceParser().getResultRunCompiledPHPCode(path);

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

            String phpResult = engine.getResultExec(phpHome + " \" " + stringResult + " \"");
            try
            {
                hasil = new AppClient().getResultRMIClient(phpResult);
            }
            catch(RemoteException ex)
            {
                String stackTrace = "";
                for(int i = 1; i < ex.getStackTrace().length; i++)
                {
                    stackTrace += ex.getStackTrace()[i] + "<br/>";
                }

                hasil = ResponseConstant.internalServerError("Internal Server Error", stackTrace);

            }
            catch(NotBoundException ex)
            {
                String stackTrace = "";
                for(int i = 1; i < ex.getStackTrace().length; i++)
                {
                    stackTrace += ex.getStackTrace()[i] + "<br/>";
                }

                hasil = ResponseConstant.internalServerError("Internal Server Error", stackTrace);
            }
        }
        else
        {
            String notFound = ResponseConstant.notFound;
            responseBody.write(notFound.getBytes());
        }
        try
        {
            responseBody.write(hasil.getBytes());
        }
        finally
        {
            if(responseBody != null)
            {
                responseBody.flush();
                responseBody.close();
            }
            if(exchange != null)
            {
                exchange.close();
            }
        }
    }
}
