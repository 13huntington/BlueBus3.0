package com.BlueBlusPack.crist.bluebus.Aplicacion;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.widget.Toast;
import com.BlueBlusPack.crist.bluebus.Datos.GestorDatos;

import java.util.ArrayList;
import com.BlueBlusPack.crist.bluebus.Entidades.*;

public class GestorBLE {
    private BluetoothLeScanner mBluetoothAdapter;
    private Principal activity;
    private ArrayList<LineaEsperada> lineasEsperadas;
    private final String CODIGO_IDA = "1";
    private final String CODIGO_VUELTA = "2";

    public GestorBLE() {
        BluetoothAdapter mBluetoothAdapterViejo = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter =  mBluetoothAdapterViejo.getBluetoothLeScanner();
        lineasEsperadas= new ArrayList<>();
    }


//Inicializa la busqueda del colectivo
   public void find(Principal atc , ArrayList<LineaEsperada> lineasEsperadas){
        this.lineasEsperadas = lineasEsperadas;
        scanLeDevice(true);
        activity =atc;
    }

    public void stop(){
        scanLeDevice(false);
    }

//Va capturando los dispositivos ble en rango y comprueba si es el deseado
    private void scanLeDevice(final boolean enable) {
        if (enable) {
              mBluetoothAdapter.startScan(mScanCallback);
        } else {
              mBluetoothAdapter.stopScan(mScanCallback);
        }
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result != null){
                final LineaEsperada unidadDetectada = getUnidad(result.getScanRecord().getBytes());

                if (unidadDetectada != null && lineasEsperadas.contains(unidadDetectada)){
                    String mac = result.getDevice().getAddress();
                    GestorDatos gestorDatos = new GestorDatos(activity.getApplicationContext());
                    if(gestorDatos.verificarUnidad(mac)){
                            activity.runOnUiThread(new Runnable() {
                            public void run() {
                                activity.detener();
                                Toast.makeText(activity.getApplicationContext(), "Colectivo "+unidadDetectada.toString()+" acercándose", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    };


    private LineaEsperada getUnidad(byte [] mScanRecord){
        LineaEsperada unidadDetectada = new LineaEsperada();
        if (mScanRecord != null){
            unidadDetectada.setLinea(getLinea(mScanRecord));
            unidadDetectada.setSentido(getSentido(mScanRecord));
        }
        return unidadDetectada;
    }

    private int getLinea(byte [] mScanRecord) {
        String major = String.valueOf( (mScanRecord[25] & 0xff) * 0x100 + (mScanRecord[26] & 0xff));
        return Integer.parseInt(major);
    }
    private String getSentido(byte [] mScanRecord) {
        String minor = String.valueOf( (mScanRecord[27] & 0xff) * 0x100 + (mScanRecord[28] & 0xff));
        if (minor.equals(CODIGO_IDA))
            return LineaEsperada.IDA;
        if (minor.equals(CODIGO_VUELTA))
            return  LineaEsperada.VUELTA;
        return " ";
    }


}
