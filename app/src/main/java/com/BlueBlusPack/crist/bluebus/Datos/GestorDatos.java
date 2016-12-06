package com.BlueBlusPack.crist.bluebus.Datos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import com.BlueBlusPack.crist.bluebus.Entidades.*;

/**
 * Created by Agu on 28/11/2016.
 */

public class GestorDatos {

    private DBHelper dbHelper;

    public GestorDatos(Context context) {
        dbHelper = new DBHelper(context,null);
        try {
            dbHelper.createDataBase();
        }catch (IOException exception){

        }
    }

    public ArrayList<Corredor> obtenerCorredores(){
        ArrayList<Corredor> corredores = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Tablas.Corredor.NOMBRETABLA,new String[]{Tablas.Corredor.ID},null,null,null,null,null);
        cursor.moveToFirst();
        do
        {
            int indexNroCorredor = cursor.getColumnIndex(Tablas.Corredor.ID);
            int nroCorredor = cursor.getInt(indexNroCorredor);
            Corredor corredor = new Corredor(nroCorredor);
            corredores.add(corredor);
        } while (cursor.moveToNext());
        return corredores;
    }

    public ArrayList<Linea> obtenerLineas(int idCorredor){
        ArrayList<Linea> lineas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Tablas.Linea.NOMBRETABLA,
                new String[]{Tablas.Linea.ID, Tablas.Linea.PKCORREDOR},
                Tablas.Linea.PKCORREDOR +"="+idCorredor,
                null,null,null,null);
        cursor.moveToFirst();
        do
        {
            int indexNroLinea = cursor.getColumnIndex(Tablas.Linea.ID);
            int indexPKCorredor = cursor.getColumnIndex(Tablas.Linea.PKCORREDOR);
            int nroLinea = cursor.getInt(indexNroLinea);
            int pkCorredor = cursor.getInt(indexPKCorredor);
            Linea linea = new Linea(nroLinea);
            lineas.add(linea);
        } while (cursor.moveToNext());
        return lineas;
    }

    public ArrayList<Unidad> obtenerUnidades(int idLinea){
        ArrayList<Unidad> unidades = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Tablas.Unidad.NOMBRETABLA,
                new String[]{Tablas.Unidad.MAC, Tablas.Unidad.LINEA},
                Tablas.Unidad.LINEA +"="+idLinea,
                null,null,null,null);
        cursor.moveToFirst();
        do
        {
            int indexMac = cursor.getColumnIndex(Tablas.Unidad.MAC);
            int indexLinea = cursor.getColumnIndex(Tablas.Unidad.LINEA);
            int mac = cursor.getInt(indexMac);
            int nroLinea = cursor.getInt(indexLinea);
            Unidad unidad = new Unidad(mac,nroLinea);
            unidades.add(unidad);
        } while (cursor.moveToNext());
        return unidades;
    }

    public boolean verificarUnidad(String macBuscado){
        ArrayList<Unidad> unidades = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String consulta = "SELECT mac FROM Unidad WHERE mac LIKE ?";
        Cursor cursor = db.rawQuery(consulta, new String[]{macBuscado});
        if(cursor.moveToFirst())
        {
            String i = cursor.getString(0);
            return true;

        }
        return false;
    }
}
