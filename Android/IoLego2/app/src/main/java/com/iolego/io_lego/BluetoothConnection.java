package com.iolego.io_lego;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;

public class BluetoothConnection extends Service {
    private static final String TAG= "BLUETOOTH_ACTIVITY";
    private static String MAC_ADDRESS = null;
    private static final String ID_LEGO = "EV3";
    private final IBinder bluetoothBinder = new BluetoothBinder();

    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothSocket socket;
    private InputStreamReader inputReader;


    public class BluetoothBinder extends Binder {
        public BluetoothConnection getService() { return BluetoothConnection.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bluetoothBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MAC_ADDRESS = null;
        return super.onUnbind(intent);
    }

    int connect() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {/*ritorna true se il bluetooth è attivo sul telefono*/
                if(MAC_ADDRESS != null) {

                    socket.connect();
                    Log.w(TAG, "Device connected. MAC " + MAC_ADDRESS);
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();

                    inputReader = new InputStreamReader(inputStream);


                    return 0;

                }else {
                    Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                    BluetoothDevice device = searchDevice(bondedDevices);/*ritorna un bluetooth device*/
                    if (device != null) {
                        ParcelUuid[] uuids = device.getUuids();

                        this.socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        Log.w(TAG, "Device connected. MAC " + MAC_ADDRESS);
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();

                        inputReader = new InputStreamReader(inputStream);

                        return 0;


                    } else {
                        Log.e(TAG, "No appropriate paired devices.");
                    }
                }
            } else {
                Log.e(TAG, "Bluetooth is disabled.");
                return 2;
            }
        }
        return 3;
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

    public void send(String str) throws IOException {
        Log.d("stringa", str);
        outputStream.write(str.getBytes(Charset.forName("UTF-8")));
        outputStream.write("\0".getBytes());
    }

    String read() throws IOException {
        int bytesRead;
        char[] buffer = new char[256];

        bytesRead = inputReader.read(buffer, 0, 4);
        return new String(buffer, 0, bytesRead);
    }


}
