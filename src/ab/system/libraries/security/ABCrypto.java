package ab.system.libraries.security;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * ABCrypto
 * The name of Algoritm is getMBS32
 * @author Angga Bayu Sejati
 * @email anggabs@rocketmail.com
 * @function Generate key kriptografi using mix of CRC32, MD5, SHA And My Algoritm
 * @name The name of Algoritm is getMBS32
 */
public class ABCrypto
{

    private String signature;
    private long lamanya = 0;
    private String result;

    public ABCrypto()
    {
    }

    /**
     *
     * @param Untuk menghitung nilai CRC32
     * @return String
     */
    public final String getCRC32(String str)
    {
        String string = new String(str);
        String crc2 = null;
        long value = 0;
        try
        {
            byte buffer2[] = string.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer2);
            CheckedInputStream cis = new CheckedInputStream(bais, new CRC32());
            byte readBuffer[] = new byte[5];
            while(cis.read(readBuffer) >= 0)
            {
                value = cis.getChecksum().getValue();

            }
            String raw = "00000000" + Long.toHexString(value);
            String crc = raw.substring(raw.length() - 8);
            crc2 = crc.toUpperCase();
        }
        catch(Exception e)
        {
            System.out.println("Ada eksepsi : " + e);
            return "";
        }
        return "" + crc2;
    }

    /**
     *
     * @param Untuk menghitung nilai MD5
     * @return String
     */
    public final String getMD5(String s)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes(), 0, s.length());
            signature = new BigInteger(1, md5.digest()).toString(16);
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     *
     * @param Untuk menghitung nilai SHA
     * @return String
     */
    public final String getSHA(String s)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("SHA");
            md5.update(s.getBytes(), 0, s.length());
            signature = new BigInteger(1, md5.digest()).toString(16);
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return signature;
    }

    public final String getRC4Encode(String result)
    {
        return new ABSecurity().enkodeRC4(result);
    }

    public final String getRC4Decode(String result)
    {
        return new ABSecurity().dekodeRC4(result);
    }

    /**
     * Merupakan suatu checksum baru dengan cara menggabungkan
     * teknik CRC32, MD5, SHA dan RC4 dan Algoritma hash satu arah karangan sendiri
     * author Angga Bayu Sejati
     * email anggabs@rocketmail.com
     * @since 2011
     * @param key String yang akan dienkripsi
     * @return String hasil hash 1 arah
     */
    public String getMBS32(String key)
    {
        long now = System.currentTimeMillis();
        String tmp = "";
        System.out.println("panjang " + key.length());
        for(int i = 0; i < key.length(); i++)
        {
            tmp = "" + this.getSHA("" + key.charAt(i));
        }
        String a1 = this.getSHA(tmp);
        String a2 = this.getMD5(a1);
        String a4 = this.getMD5(a2);
        ab.system.libraries.security.ABSecurity rc4 = new ab.system.libraries.security.ABSecurity();
        result = rc4.enkodeRC4(a4);
        result = this.getSHA(result);
        lamanya = System.currentTimeMillis() - now;
        return result;
    }

    public static void main(String[] args)
    {
        String string = "okesss";
        ABSecurity ab = new ABSecurity();
        string = ab.dekodeRC4(string);
        System.out.println(string);
        //System.out.println(ab.enkodeRC4(string));
    }
}
