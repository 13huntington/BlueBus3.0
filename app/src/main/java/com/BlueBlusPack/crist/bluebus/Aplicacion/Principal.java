package com.BlueBlusPack.crist.bluebus.Aplicacion;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.BlueBlusPack.crist.bluebus.Datos.GestorDatos;
import com.BlueBlusPack.crist.bluebus.Entidades.Corredor;
import com.BlueBlusPack.crist.bluebus.Entidades.Linea;
import com.BlueBlusPack.crist.bluebus.Entidades.LineaEsperada;
import com.BlueBlusPack.crist.bluebus.R;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected Spinner spinnerCorredor, spinnerLinea, spinnerLineasSelec;

    Button btnBuscar, btnAgregar, btnLimpiar;
    RadioButton ida, vuelta;
    Bundle savedInstanceState;
    Vibrator vibrador;
    private GestorBLE gestorBLE;
    private int REQUEST_ENABLE_BT = 1;
    private GestorDatos gestorDatos;
    private ArrayList<LineaEsperada> lineasEsperadas;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected final String TAG = "Busqueda";
    GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestorDatos = new GestorDatos(this.getApplicationContext());
        lineasEsperadas = new ArrayList<>();
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_blue_principal);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        spinnerCorredor = (Spinner) findViewById(R.id.spCorredor);
        spinnerLinea = (Spinner) findViewById(R.id.spLinea);
        spinnerLineasSelec = (Spinner) findViewById(R.id.spColectivosAgregados);
        ida = (RadioButton) findViewById(R.id.radioButtonIda);
        vuelta = (RadioButton) findViewById(R.id.radioButtonVuelta);
        addItemsOnSpinner();
        vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("La aplicacion necesita acceso a la ubicación");
            builder.setMessage("Por favor active la ubiación para realizar la búsqueda");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                public void onDismiss(DialogInterface dialog) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                }
            });
            builder.show();
        }

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            solicitarUbicacion();
        }
                }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso grantizado");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalidad limitada");
                    builder.setMessage("Si la ubicación no es activada, ésta aplicación no podrá realizar la búsqueda");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }


    public void detener() {
        gestorBLE.stop();
        vibrador.vibrate(2500);
        habilitarBotones();
        btnBuscar.setText("Buscar");
    }

    public void buscarA(View v) {
        if (btnBuscar.getText().equals("Buscar") && lineasEsperadas.size() > 0) {
            gestorBLE = new GestorBLE();
            desHabilitarBotones();
            btnBuscar.setText("Detener");
            gestorBLE.find(this, lineasEsperadas);
        } else {
            if (lineasEsperadas.size() > 0) {
                if (gestorBLE != null)
                    gestorBLE.stop();
                habilitarBotones();
                btnBuscar.setText("Buscar");
            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Agregue al menos un colectivo", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    public void agregarUnidad(View v) {
        if (spinnerLinea.getSelectedItemPosition() >= 0) {
            Linea lineaSeleccionada = (Linea) spinnerLinea.getSelectedItem();
            int nroLinea = lineaSeleccionada.getNumero();
            String sentido;
            if (ida.isChecked()) {
                sentido = LineaEsperada.IDA;
            } else {
                sentido = LineaEsperada.VUELTA;
            }
            final LineaEsperada nuevaLinea = new LineaEsperada(nroLinea, sentido);
            if (!lineasEsperadas.contains(nuevaLinea))
                lineasEsperadas.add(nuevaLinea);
            final ArrayAdapter<LineaEsperada> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lineasEsperadas);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLineasSelec.setAdapter(dataAdapter);

            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Colectivo " + nuevaLinea.toString() + " agregado", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void quitarUnidad(View v) {
        if (spinnerLineasSelec.getSelectedItemPosition() >= 0) {
            if (spinnerLineasSelec.getSelectedItem() != null && lineasEsperadas.size() > 0) {
                final LineaEsperada lineaSeleccionada = (LineaEsperada) spinnerLineasSelec.getSelectedItem();
                lineasEsperadas.remove(lineaSeleccionada);
                final ArrayAdapter<LineaEsperada> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lineasEsperadas);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLineasSelec.setAdapter(dataAdapter);

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Colectivo " + lineaSeleccionada.toString() + " quitado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void desHabilitarBotones() {
        spinnerCorredor.setEnabled(false);
        spinnerLinea.setEnabled(false);
        spinnerLineasSelec.setEnabled(false);
        ida.setEnabled(false);
        vuelta.setEnabled(false);
        btnAgregar.setEnabled(false);
        btnLimpiar.setEnabled(false);
    }

    public void habilitarBotones() {
        spinnerCorredor.setEnabled(true);
        spinnerLinea.setEnabled(true);
        ida.setEnabled(true);
        vuelta.setEnabled(true);
        spinnerLineasSelec.setEnabled(true);
        btnAgregar.setEnabled(true);
        btnLimpiar.setEnabled(true);
    }

    private void addItemsOnSpinner() {
        final List<Corredor> listaCorredores = obtenerCorredores();
        final ArrayAdapter<Corredor> dataAdapterCorredor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCorredores);
        dataAdapterCorredor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorredor.setAdapter(dataAdapterCorredor);
        spinnerLinea.setAdapter(dataAdapterCorredor);
        spinnerCorredor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                Corredor corredorSeleccionado = (Corredor) spinnerCorredor.getItemAtPosition(position);
                ArrayList<Linea> listaLineas = obtenerLineas(corredorSeleccionado.getNumero());

                final ArrayAdapter<Linea> dataAdapterLinea = new ArrayAdapter<>(spinnerCorredor.getContext(), android.R.layout.simple_spinner_item, listaLineas);
                dataAdapterLinea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLinea.setAdapter(dataAdapterLinea);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //no seleccionó nada
            }
        });
    }

    private ArrayList<Corredor> obtenerCorredores() {
        return gestorDatos.obtenerCorredores();
    }

    private ArrayList<Linea> obtenerLineas(int nroCorredor) {
        return gestorDatos.obtenerLineas(nroCorredor);
    }

    private void solicitarUbicacion() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 34992, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        mGoogleApiClient.connect();
        settingsrequest();
    }

    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Principal.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Principal Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


