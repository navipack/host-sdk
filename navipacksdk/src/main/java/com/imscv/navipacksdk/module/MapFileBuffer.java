package com.imscv.navipacksdk.module;

/**
 * Created by dell on 2016/9/3.
 */
public class MapFileBuffer {

    /**
     * 文件名
     */
    public String mFileName;

    /**
     * 文件内容
     */
    public byte[] mFileBuf;

    /**
     * 单块内容的大小
     */
    public int mPartSize;

    /**
     * 整个文件的大小
     */
    public int mFileSize;

    /**
     * 文件的序号
     */
    public int mPartNum;

    public MapFileBuffer(String fileName, byte[] fileBuf, int partSize, int fileSize, int partNum) {
        mFileName = fileName;
        mFileBuf = fileBuf;
        mPartSize = partSize;
        mFileSize = fileSize;
        mPartNum = partNum;
    }
}
