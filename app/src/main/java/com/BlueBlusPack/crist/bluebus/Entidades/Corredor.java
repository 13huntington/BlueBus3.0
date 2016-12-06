package com.BlueBlusPack.crist.bluebus.Entidades;

import com.BlueBlusPack.crist.bluebus.Entidades.Linea;

import java.util.ArrayList;

/**
 * Created by crist on 08/10/2016.
 */
public class Corredor {
    private int id;
    private final int numero;
    private ArrayList<Linea> lineas;

    public Corredor(int id, int numero, ArrayList<Linea> lineas) {
        this.id = id;
        this.numero = numero;
        this.lineas = lineas;
    }

    public Corredor(int numero, ArrayList<Linea> lineas){
        this.lineas = lineas;
        this.numero = numero;
    }

    public Corredor(int numero) {
        this.numero = numero;
    }

    public int getnombre() {
        return numero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public  ArrayList<Linea> getLineas() {
        return lineas;
    }

    public ArrayList<Integer> getNumerosDeLineas()
    {
        ArrayList<Integer> nrosLineas = new ArrayList<>();
        for (Linea linea: lineas) {
            nrosLineas.add(linea.getNumero());
        }
        return nrosLineas;
    }

    public void setLineas( ArrayList<Linea> lineas) {
        this.lineas = lineas;
    }
    
    public void addLinea(Linea linea){
        lineas.add(linea);
    }

    public String toString(){
        return "Corredor "+numero;
    }
}
