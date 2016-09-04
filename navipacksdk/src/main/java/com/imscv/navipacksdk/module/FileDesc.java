package com.imscv.navipacksdk.module;

/**
 * Created by dell on 2016/9/4.
 * 对文件描述的类
 */
public class FileDesc {
    public String filePath;
    public String fileName;

    public FileDesc(String path, String fileName) {
        this.filePath = path;
        this.fileName = fileName;
    }
}
