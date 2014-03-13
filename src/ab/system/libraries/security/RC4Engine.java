package ab.system.libraries.security;

class KeyParameter
{

    private byte[] key;

    public KeyParameter(byte[] key)
    {
        this(key, 0, key.length);
    }

    public KeyParameter(byte[] key, int keyoff, int keyLen)
    {
        this.key = new byte[keyLen];
        System.arraycopy(key, keyoff, this.key, 0, keyLen);
    }

    public byte[] getKey()
    {
        return key;
    }
}

public class RC4Engine
{

    private final static int STATE_LENGTH = 256;
    private byte[] engineState = null, workingKey = null;
    private int x = 0, y = 0;
    public char[] kDigits =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd'
    };

    RC4Engine()
    {
        System.gc();
    }

    public void init(boolean forEncryption, KeyParameter params)
    {
        if(params instanceof KeyParameter)
        {
            workingKey = ((KeyParameter) params).getKey();
            setKey(workingKey);
            return;
        }

        throw new IllegalArgumentException("invalid parameter passed to RC4 init" + params.getClass().getName());
    }

    public void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
    {
        if((inOff + len) > in.length)
        {
            throw new RuntimeException("output buffer too short");
        }
        if((outOff + len) > out.length)
        {
            throw new RuntimeException("out put buffer too short");
        }
        for(int i = 0; i < len; i++)
        {
            x = (x + 1) & 0xff;
            y = (engineState[x] + y) & 0xff;
            //swap
            byte tmp = engineState[x];
            engineState[x] = engineState[y];
            engineState[y] = tmp;
            //xor
            out[i + outOff] = (byte) (in[i + inOff] ^ engineState[(engineState[x] + engineState[y]) & 0xff]);
        }

    }

    public String bytesToHex(byte[] raw)
    {
        int length = raw.length;
        char[] hex = new char[length * 2];
        for(int i = 0; i < length; i++)
        {
            int value = (raw[i] + 256) % 256;
            int highIndex = value >> 4;
            int lowIndex = value & 0x0f;
            hex[i * 2 + 0] = kDigits[highIndex];
            hex[i * 2 + 1] = kDigits[lowIndex];
        }
        return (new String(hex)).toString();
    }

    public void reset()
    {
        setKey(workingKey);
    }

    //private implementation
    private void setKey(byte[] keyBytes)
    {
        workingKey = keyBytes;
        x = 0;
        y = 0;
        if(engineState == null)
        {
            engineState = new byte[STATE_LENGTH];
        }

        //reset the state of the engine
        for(int i = 0; i < STATE_LENGTH; i++)
        {
            engineState[i] = (byte) i;
        }

        int i1 = 0;
        int i2 = 0;
        for(int i = 0; i < STATE_LENGTH; i++)
        {
            i2 = ((keyBytes[i1] & 0xff) + engineState[i] + i2) & 0xff;
            //do the byte swap inline
            byte tmp = engineState[i];
            engineState[i] = engineState[i2];
            engineState[i2] = tmp;
            i1 = (i1 + 1) % keyBytes.length;
        }
    }
}
