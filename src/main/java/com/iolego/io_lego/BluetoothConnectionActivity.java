package com.iolego.io_lego;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothConnectionActivity extends AppCompatActivity {
    private static final String TAG = "BTConnectionActivity";
    private static String MAC_ADDRESS = null;
    private static final String ID_LEGO = "EV3";

    private BluetoothSocket socket;

    private InputStream inputStream;
    private OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);


        /*
        try {

            connect();

        } catch (IOException e){
            reConnect();
        }
        */

    }


    private BluetoothDevice searchDevice(Set<BluetoothDevice> devices) {
        for (BluetoothDevice device : devices) {
            String deviceName = device.getName();
            if (deviceName.equals(ID_LEGO)) {
                MAC_ADDRESS = device.getAddress();
                return device;
            }
        }
        return null;
    }

    private boolean connect() throws IOException {

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {

            if (blueAdapter.isEnabled()) {/*ritorna true se il bluetooth è attivo sul telefono*/
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                BluetoothDevice device = searchDevice(bondedDevices);/*ritorna un bluetooth device*/
                if (device != null) {
                    ParcelUuid[] uuids = device.getUuids();
                    socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();

                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                    Log.d(TAG, "Device connected. MAC " + MAC_ADDRESS);
                    return true;
                } else {
                    Log.e(TAG, "No appropriate paired devices.");
                }
            } else {
                Log.e(TAG, "Bluetooth is disabled.");
            }
        }
        return false;
    }

    private void reConnect() {
        AlertDialog.Builder build = new AlertDialog.Builder(this.getApplicationContext());
        build.setCancelable(false);
        build.setTitle(R.string.alert_reconnect_title);
        build.setMessage(R.string.alert_reconnect_text);
        build.create().show();
        build.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    connect();
                } catch (IOException e) {
                    reConnect();
                }
            }
        });
        build.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        build.create().show();

    }


    public void chooseColor(View view) {

        Log.d("errrore","dio merda");
        AlertDialog.Builder build = new AlertDialog.Builder(this.getApplicationContext());
        build.setTitle(getString(R.string.alert_colorChoose_title));
        AlertDialog dial=build.create();
        dial.show();

    }
}
    /*
    public void chooseColor(View view) {
        Log.d("errore","dio porco dio cane bastard");
        AlertDialog.Builder build = new AlertDialog.Builder(this.getApplicationContext());
        build.setCancelable(true);
        build.setTitle("Title");

        Log.d("errore","dio porco dio cane bastard");
        build.create().show();
    }
    }
       /* build.setItems(new CharSequence[]
                        {"button 1", "button 2", "button 3", "button 4"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                Toast.makeText(context, "clicked 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(context, "clicked 2", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context, "clicked 3", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(context, "clicked 4", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        build.create().show();

    }
}*/
