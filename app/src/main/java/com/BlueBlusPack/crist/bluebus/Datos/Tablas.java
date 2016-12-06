package com.BlueBlusPack.crist.bluebus.Datos;

import android.provider.BaseColumns;

/**
 * Created by Agu on 28/11/2016.
 */

public final class Tablas {

    public static class Corredor implements BaseColumns{
        public static String NOMBRETABLA = "Corredor";
        public static String ID = "nroCorredor";
    }

    public static class Linea implements BaseColumns{
        public static String NOMBRETABLA = "Linea";
        public static String ID = "nroLinea";
        public static String PKCORREDOR = "nroCorredor";
    }

    public static class Unidad implements BaseColumns{
        public static String NOMBRETABLA = "Unidad";
        public static String MAC = "mac";
        public static String LINEA = "nroLinea";
    }
}
