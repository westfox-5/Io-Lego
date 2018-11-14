package com.iolego.io_lego;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BTConnectionActivity";
    private static String MAC_ADDRESS = null;
    private static final String ID_LEGO = "EV3";

    private Button b;

    private int map[][];
    private int x,y;

    public Dialog colorChooseDialog, reconnectDialog;

    private BluetoothSocket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private Button send;


    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map=new int[5][5];

        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                map[i][j]=0;
            }
        }

        colorChooseDialog=new Dialog(this);
        reconnectDialog=new Dialog(this);

        /*
        createDialog();
        */


        /*
        connect();

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

    private boolean connect() {

        if(reconnectDialog.isShowing()) reconnectDialog.dismiss();

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {

            if (blueAdapter.isEnabled()) {/*ritorna true se il bluetooth è attivo sul telefono*/

                if(MAC_ADDRESS != null) {
                    try {
                        socket.connect();
                        Log.w(TAG, "Device connected. MAC " + MAC_ADDRESS);
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                    } catch(IOException e) {
                        createDialog();
                        reconnectDialog.show();
                    }
                }else {

                    Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                    BluetoothDevice device = searchDevice(bondedDevices);/*ritorna un bluetooth device*/
                    if (device != null) {
                        ParcelUuid[] uuids = device.getUuids();
                        try {
                            socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            Log.w(TAG, "Device connected. MAC " + MAC_ADDRESS);
                            outputStream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                        } catch (IOException e) {
                            //reconnectDialog.show();
                            createDialog();
                            reconnectDialog.show();
                        }

                        return true;

                    } else {
                        Log.e(TAG, "No appropriate paired devices.");
                    }
                }
            } else {
                Log.e(TAG, "Bluetooth is disabled.");
                enableBluetooth();
            }
        }
        return false;
    }

    public void reConnect(View view) {

        connect();
    }

    private void enableBluetooth() {
        try {
            Thread.sleep(500);
        }catch(Exception e) {
            e.printStackTrace();
        }
        Intent enabelBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enabelBT);

        createDialog();
        reconnectDialog.show();


    }



    public void chooseColor(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        b=(Button)view;

        String id=getResources().getResourceEntryName(view.getId());

        Log.v("id",id);

        x=id.charAt(1)-'0';
        y=id.charAt(2)-'0';

        colorChooseDialog.setContentView(R.layout.choose_color);
        colorChooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        colorChooseDialog.show();
    }

    public void setColor(View view){
        String id=getResources().getResourceEntryName(view.getId());

        switch(id){
            case "yellow":
                b.setBackgroundColor(getResources().getColor(R.color.yellow));
                Log.d("color","yellow in "+x+y);
                map[x][y]=1;
                break;

            case "blue":
                b.setBackgroundColor(getResources().getColor(R.color.blue));
                Log.d("color","blue in "+x+y);
                map[x][y]=2;
                break;

            case "green":
                b.setBackgroundColor(getResources().getColor(R.color.green));
                Log.d("color","green in "+x+y);

                map[x][y]=3;
                break;
            case "red":
                b.setBackgroundColor(getResources().getColor(R.color.red));
                Log.d("color", "red in "+x+y);
                map[x][y]=4;
                break;

            case "undo":
                b.setBackgroundResource(android.R.drawable.btn_default);
                Log.d("color", "undo in "+x+y);
                map[x][y]=0;
                break;
        }

        colorChooseDialog.hide();
    }


    public void exit(View view) {
        System.exit(0);
    }

    public void createDialog() {
        AlertDialog.Builder build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            build = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            build = new AlertDialog.Builder(this);
        }
        build.setCancelable(false);
        build.setTitle(R.string.alert_reconnect_title);
        build.setMessage(R.string.alert_reconnect_text);

        build.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connect();
            }
        });
        build.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        reconnectDialog = build.create();
    }


    private void startSearch(){
        String stuff;
    }

}
