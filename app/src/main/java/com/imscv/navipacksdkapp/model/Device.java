package com.imscv.navipacksdkapp.model;

import java.io.Serializable;

/**
 * Created by Kyle on 6/27/16.
 */
public class Device implements Serializable {
    public String fileName;
    public int param;
    public int id;

    public Device(String fileName, int param, int id) {
        this.fileName = fileName;
        this.param = param;
        this.id = id;
    }
}
