package com.BlueBlusPack.crist.bluebus.Entidades;

/**
 * Created by Agu on 27/11/2016.
 */

public class Unidad {
    private int id;
    private int mac;

    public Unidad(int id, int mac) {
        this.id = id;
        this.mac = mac;
    }

    public Unidad(int mac) {
        this.mac = mac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMac() {
        return mac;
    }

    public void setMac(int mac) {
        this.mac = mac;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof Unidad){
            Unidad otro = (Unidad)obj;
            if(otro.getMac() == this.getMac()){
                return  true;
            }
        }
        return false;
    }
}
