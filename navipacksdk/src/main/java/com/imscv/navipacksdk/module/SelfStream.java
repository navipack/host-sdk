package com.imscv.navipacksdk.module;

import com.imscv.navipacksdk.tools.Memory;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dell on 2016/8/11.
 */
public class SelfStream {

    private byte[] data;
    private final byte[] scratch = new byte[8];
    private Lock lock = new ReentrantLock();// 锁对象

    /**
     * The number of bytes written out so far.
     */
    protected int written;

    public byte[] getBytes()
    {
        byte[] retByte = null;
        lock.lock();
        if(written > 0)
        {
            retByte = new byte[written];
            System.arraycopy(data,0,retByte,0,retByte.length);
        }
        lock.unlock();
        return retByte;

    }

    /**
     * @param maxSize the max size for writing.
     */
    public SelfStream(int maxSize) {
        data = new byte[maxSize];
        written = 0;
    }

    /**
     * 返回最大可容纳的数据量
     *
     * @return the number of bytes written to the target stream.
     */
    public final int size() {
        if (data != null) return data.length;
        else return -1;
    }

    /**
     * Writes {@code count} bytes from the byte array {@code buffer} starting at
     * {@code offset} to the target stream.
     *
     * @param buffer the buffer to write to the target stream.
     * @param offset the index of the first byte in {@code buffer} to write.
     * @param count  the number of bytes from the {@code buffer} to write.
     */
    public boolean write(byte[] buffer, int offset, int count) {
        lock.lock();
        int ret = 0;
        if (buffer == null || data == null|| written + count > data.length || offset + count > buffer.length) {
            lock.unlock();
            return false;
        }

        System.arraycopy(buffer,offset,data,written,count);
        written += count;
        lock.unlock();
        return true;
    }

    /**
     * Writes a byte to the target stream. Only the least significant byte of
     * the integer {@code oneByte} is written.
     *
     * @param oneByte the byte to write to the target stream.
     */
    public boolean write(int oneByte) {
        lock.lock();
        if(data ==null || written > data.length){
            lock.unlock();
            return false;
        }
        data[written] = (byte)oneByte;
        written++;
        lock.unlock();
        return true;
    }



    /**
     * Writes an 8-bit byte to the target stream. Only the least significant
     * byte of the integer {@code val} is written.
     *
     * @param val the byte value to write to the target stream.
     * @throws IOException if an error occurs while writing to the target stream.
     * @see DataInputStream#readByte()
     * @see DataInputStream#readUnsignedByte()
     */
    public final boolean writeByte(int val) {
        return write(val);
    }

    public final boolean writeBytes(String str) {
        if (str.length() == 0) {
            return false;
        }
        byte[] bytes = new byte[str.length()];
        for (int index = 0; index < str.length(); index++) {
            bytes[index] = (byte) str.charAt(index);
        }
        return write(bytes,0,bytes.length);

    }


    public final boolean writeDouble(double val)  {
        return writeLong(Double.doubleToLongBits(val));
    }

    public final boolean writeFloat(float val)  {
        return writeInt(Float.floatToIntBits(val));
    }

    public final boolean writeInt(int val) {
        Memory.pokeInt(scratch, 0, val, ByteOrder.LITTLE_ENDIAN);
        return write(scratch, 0, SizeOf.INT);
    }

    public final boolean writeLong(long val)  {
        Memory.pokeLong(scratch, 0, val, ByteOrder.LITTLE_ENDIAN);
        return write(scratch, 0, SizeOf.LONG);
    }

    public final boolean writeShort(int val) {
        Memory.pokeShort(scratch, 0, (short) val, ByteOrder.LITTLE_ENDIAN);
        return write(scratch, 0, SizeOf.SHORT);
    }

    private class SizeOf{
        public static final int SHORT = 2;
        public static final int INT = 4;
        public static final int LONG = 8;
    }

}
