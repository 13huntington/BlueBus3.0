package com.BlueBlusPack.crist.bluebus.Entidades;

/**
 * Created by Agu on 6/12/2016.
 */

public class LineaEsperada {
    private int linea;
    private String sentido;

    public static final String IDA = "ida";
    public static final String VUELTA = "vuelta";

    public LineaEsperada(){

    }

    public LineaEsperada(int linea, String sentido) {
        this.linea = linea;
        this.sentido = sentido;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o instanceof LineaEsperada){
            LineaEsperada otro = (LineaEsperada)o;
            if(otro.getLinea() == this.getLinea() && otro.getSentido().equals(this.getSentido())){
                return  true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return linea + sentido.hashCode();
    }

    @Override
    public String toString() {
        return linea + " " + sentido;
    }
}
