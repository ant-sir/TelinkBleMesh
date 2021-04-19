package com.telink.ble.mesh.model;

import java.io.Serializable;

public class TimerInfo implements Serializable, Comparable{
    public int id;
    public int hour;
    public int mim;
    public int sec;

    @Override
    public int compareTo(Object o) {
        return id - ((TimerInfo)o).id;
    }
}
