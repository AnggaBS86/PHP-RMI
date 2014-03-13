package ab.phprmi.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * class untuk menghandle parser PHP Script
 *
 * @author Angga BS
 */
public class PHPSourceParser
{

    /**
     * Mendapatkan native PHP source (PHP Source yang asli)
     *
     * @param fileSource String
     * @return String
     */
    public synchronized String getNativePHPSource(String fileSource)
    {
        FileInputStream fIn = null;
        FileChannel fChan = null;
        long fSize;
        ByteBuffer mBuf;
        String content = "";
        try
        {
            fIn = new FileInputStream(fileSource);
            fChan = fIn.getChannel();
            fSize = fChan.size();
            mBuf = ByteBuffer.allocate((int) fSize);
            fChan.read(mBuf);
            mBuf.rewind();
            for(int i = 0; i < fSize; i++)
            {
                content += (char) mBuf.get();
            }
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        finally
        {
            try
            {
                if(fChan != null)
                {
                    fChan.close();
                }
                if(fIn != null)
                {
                    fIn.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        content = content.replace("<?php", "");
        content = content.replace('"', '\'');
        content = content.replace("'", "\'");
        content = content.replace("?>", "");
        return content;
    }

    /**
     * Mendapatkan PHP Source yang telah di filter FIlter yang dilakukan antara
     * lain : <ul> <li> Filter Annotation </li> <li> Filter script </li> </ul>
     *
     * @param fileSource String
     * @return String
     */
    public synchronized String getFilteredPHPSource(String fileSource)
    {
        FileInputStream fIn = null;
        FileChannel fChan = null;
        long fSize;
        ByteBuffer mBuf;
        String content = "";
        try
        {
            fIn = new FileInputStream(fileSource);
            fChan = fIn.getChannel();
            fSize = fChan.size();
            mBuf = ByteBuffer.allocate((int) fSize);
            fChan.read(mBuf);
            mBuf.rewind();
            for(int i = 0; i < fSize; i++)
            {
                content += (char) mBuf.get();
            }
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        finally
        {
            try
            {
                if(fChan != null)
                {
                    fChan.close();
                }
                if(fIn != null)
                {
                    fIn.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        content = content.replace("<?php", "");
        content = content.replace('"', '\'');
        content = content.replace("'", "\'");
        content = content.replace("?>", "");
        if(content.contains("@"))
        {
            int startAnno = content.indexOf("@");
            int lastAnno = content.indexOf(")") + 1;
            String anno = content.substring(startAnno, lastAnno);
            content = content.replace(anno, "");
        }

        return content;
    }

    /**
     * Mendapatkan nilai / value dari annotation method client
     *
     * @param fileSource String
     * @return String
     */
    public synchronized String getAnnotationClientMethod(String fileSource)
    {
        FileInputStream fIn = null;
        FileChannel fChan = null;
        long fSize;
        ByteBuffer mBuf;
        String content = "";
        try
        {
            fIn = new FileInputStream(fileSource);
            fChan = fIn.getChannel();
            fSize = fChan.size();
            mBuf = ByteBuffer.allocate((int) fSize);
            fChan.read(mBuf);
            mBuf.rewind();
            for(int i = 0; i < fSize; i++)
            {
                content += (char) mBuf.get();
            }
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        finally
        {
            try
            {
                if(fChan != null)
                {
                    fChan.close();
                }
                if(fIn != null)
                {
                    fIn.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        content = content.replace("<?php", "");
        content = content.replace('"', '\'');
        content = content.replace("'", "\'");
        String keyAnnotation = "@remoteClient";
        int indexOf = content.indexOf(keyAnnotation);
        content = content.substring(indexOf, content.length());
        int indexFunc = content.indexOf(" function");
        int indexCloseTagFunc = content.indexOf("}") + 1;
        content = content.substring(indexFunc, indexCloseTagFunc);

        return content;
    }

    private String getMethodName(String str)
    {
        String name;

        int indexCloseTagFunc = str.indexOf("{");
        name = str.substring(0, indexCloseTagFunc);
        name = name.replaceAll("function", "").trim();
        return name + ";";
    }

    private String getClassName(String str)
    {
        String name;

        int indexCloseTagClass = str.indexOf("{");
        name = str.substring(0, indexCloseTagClass);
        name = name.replaceAll("class", "").trim();
        name = name.replaceAll("final", "").trim();
        return name + "()";
    }

    private String getValueAnnotationClient(String parse)
    {
        String anno = "";

        int startIndex1 = parse.indexOf("@remoteClient");
        int lastIndex1 = parse.trim().indexOf("function");
        anno = parse.substring(startIndex1, lastIndex1);
        if(anno.contains("public"))
        {
            anno = anno.replace("public", "");
        }
        if(anno.contains("private"))
        {
            anno = anno.replace("private", "");
        }
        if(anno.contains("final"))
        {
            anno = anno.replace("final", "");
        }
        if(anno.contains("protected"))
        {
            anno = anno.replace("protected", "");
        }
        anno = anno.trim();
        int startServiceIdx = anno.indexOf("=") + 1;
        int lastServiceIdx = anno.lastIndexOf("'") + 1;
        anno = anno.substring(startServiceIdx, lastServiceIdx);
        anno = anno.replace("'", "");
        anno = anno.trim();
        return anno;
    }

    private String getValueAnnotationServer(String parse)
    {
        String anno = "";

        int startIndex1 = parse.indexOf("@remoteServer");
        int lastIndex1 = parse.trim().indexOf("')") + 4;
        anno = parse.substring(startIndex1, lastIndex1);
        return anno;
    }

    private boolean cekAnnotation(String fileSource)
    {
        boolean cek = false;
        if(getNativePHPSource(fileSource).contains("@"))
        {
            cek = true;
        }
        return cek;
    }

    /**
     * Mendapatkan hasil final Source PHP Code yang siap untuk di jalankan oleh
     * interpreter CGI PHP
     *
     * @param fileSource String
     * @return String
     */
    public String getResultRunCompiledPHPCode(String fileSource)
    {
        if(cekAnnotation(fileSource))
        {
            return getFilteredPHPSource(fileSource) + " $ob = new " + getClassName(getFilteredPHPSource(fileSource)) + ";$ob -> "
                    + getMethodName(getAnnotationClientMethod(fileSource));
        }
        else
        {
            return getNativePHPSource(fileSource) + " $ob = new " + getClassName(getNativePHPSource(fileSource)) + ";$ob -> "
                    + "main();";
        }
    }
}
