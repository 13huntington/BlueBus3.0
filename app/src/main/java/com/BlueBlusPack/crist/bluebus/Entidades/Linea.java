package com.BlueBlusPack.crist.bluebus.Entidades;

import java.util.ArrayList;

/**
 * Created by Agu on 26/11/2016.
 */

public class Linea {
    private int id;
    private int numero;
    private ArrayList<Unidad> unidades;


    public Linea(int id, int numero, ArrayList<Unidad> unidades) {
        this.id = id;
        this.numero = numero;
        this.unidades = unidades;
    }

    public Linea(int numero, ArrayList<Unidad> unidades)
    {
        this.numero = numero;
        this.unidades = unidades;
    }

    public Linea(int numero)
    {
        this.numero = numero;
        unidades = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean addUnidad(Unidad unidad)
    {
        return unidades.add(unidad);
    }

    public boolean quitarUnidad(Unidad unidad)
    {
        return unidades.remove(unidad);
    }

    public boolean contains(Unidad unidad)
    {
        return unidades.contains(unidad);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder("Linea: ");
        sb.append(numero);
        if(!unidades.isEmpty()){
            sb.append(" - Unidades: ");
            for (Unidad unidad : unidades)
            {
                sb.append(unidad);
                sb.append(";");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj instanceof Linea){
            Linea otro = (Linea)obj;
            if(otro.getNumero() == this.getNumero()){
                return  true;
            }
        }
        return false;
    }
}
