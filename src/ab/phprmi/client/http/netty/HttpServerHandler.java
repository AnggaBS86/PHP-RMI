package ab.phprmi.client.http.netty;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import ab.phprmi.client.AppClient;
import ab.phprmi.config.ResponseConstant;
import ab.phprmi.engine.PHPExecEngine;
import ab.phprmi.engine.PHPSourceParser;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

/**
 * <font color="blue"><b>class untuk menghandle request Http <br/> menggunakan
 * Netty framework Updated by Angga Bayu Sejati </b></font>
 *
 * @since 2012
 */
public class HttpServerHandler extends SimpleChannelUpstreamHandler
{

    final String cliCommand = ab.phprmi.config.Constant.PHP_INTERPRETER_FILE + " "
            + "" + ab.phprmi.config.Constant.PHP_RUN_COMMAND_OPTION;
    final String documentRoot = ab.phprmi.config.Constant.DOCUMENT_ROOT;

    /**
     * untuk mengambil string dari URI request browser
     *
     * @param req HttpRequest
     * @return String
     */
    private String getURIRequest(HttpRequest req)
    {
        String uri = req.getUri();
        System.out.println(documentRoot + uri);
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
            catch(UnsupportedEncodingException e2)
            {
                e2.printStackTrace();
            }
        }
        return documentRoot + uri;
    }

    /**
     * void untuk menghandle dari request client browser
     *
     * @param ctx ChannelHandlerContext
     * @param e MessageEvent
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
    {
        HttpRequest request = (HttpRequest) e.getMessage();
        HttpResponseStatus status = OK;
        String stringResult;
        String phpResult;
        File filePath = new File(getURIRequest(request));
        if(filePath.exists())
        {
            //result diambil dari request ke PHP RMI server dahulu
            stringResult = new PHPSourceParser().getResultRunCompiledPHPCode(getURIRequest(request));

            PHPExecEngine phpEngine = new PHPExecEngine()
            {

                @Override
                public void beforeExecute(PHPExecEngine engine)
                {
                    System.out.println("Before Execute");
                }

                @Override
                public void afterExecute(PHPExecEngine engine)
                {
                    System.out.println("After Execute");
                }
            };

            //hasilnya baru kemudian dikirim ke client (server PHP), dan diproses oleh
            // CLI server PHP tersebut, untuk dirender ke view browser
            phpResult = phpEngine.getResultExec(cliCommand + " \" " + stringResult + " \"");

            try
            {
                phpResult = new AppClient().getResultRMIClient(phpResult);
            }
            catch(RemoteException ex)
            {
                String stackTrace = "";
                for(int i = 1; i < ex.getStackTrace().length; i++)
                {
                    stackTrace += ex.getStackTrace()[i] + "<br/>";
                }

                phpResult = ResponseConstant.internalServerError("Internal Server Error", stackTrace);
            }
            catch(NotBoundException ex)
            {
                String stackTrace = "";
                for(int i = 1; i < ex.getStackTrace().length; i++)
                {
                    stackTrace += ex.getStackTrace()[i] + "<br/>";
                }

                phpResult = ResponseConstant.internalServerError("Internal Server Error", stackTrace);
            }
        }
        else
        {
            phpResult = ResponseConstant.notFound;
        }

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(phpResult, CharsetUtil.UTF_8));
        Channel ch = e.getChannel();
        ChannelFuture writeFuture;
        writeFuture = ch.write(response);
        writeFuture.addListener(new ChannelFutureProgressListener()
        {

            public void operationComplete(ChannelFuture future)
            {
                future.getChannel().close();
            }

            public void operationProgressed(ChannelFuture future, long amount, long current, long total)
            {
                System.out.printf(" %d / %d (+%d)%n", current, total, amount);
            }
        });

        // Decide whether to close the connection or not.
        if(!isKeepAlive(request))
        {
            // Close the connection when the whole content is written out.
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * void untuk menghandle eksepsi
     *
     * @param ctx ChannelHandlerContext
     * @param e ExceptionEvent
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if(cause instanceof TooLongFrameException)
        {
            sendErrorResponse(ctx, BAD_REQUEST);
            return;
        }

        cause.printStackTrace();
        if(ch.isConnected())
        {
            sendErrorResponse(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * void untuk mengirim response error
     *
     * @param ctx ChannelHandlerContext
     * @param status HttpResponseStatus
     */
    private static void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Error " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}