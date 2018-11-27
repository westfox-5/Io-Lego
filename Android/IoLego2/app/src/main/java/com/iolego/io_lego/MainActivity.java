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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BTConnectionActivity";
    private static String MAC_ADDRESS = null;
    private static final String ID_LEGO = "EV3";


    private static final int VOLTAGE_LOW = 20;
    private static final int VOLTAGE_MIDDLE = 50;

    private static final int CHECK = 100;
    private static final int END = 255;

    private int numberOfCells;
    private int checkedCells;

    private ImageButton b;
    private ImageView bluetoothImage, batteryImage;
    private View robotPlaceBtn, progressLayout;
    private int map[][];
    private int x,y;
    public Dialog colorChooseDialog, reconnectDialog;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean robot=false;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_refactor);

        map=new int[5][5];
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                map[i][j]=0;
            }
        }


        colorChooseDialog=new Dialog(this);
        colorChooseDialog.setContentView(R.layout.choose_color);
        colorChooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        robotPlaceBtn = colorChooseDialog.findViewById(R.id.robot);
        reconnectDialog=new Dialog(this);
        bar=findViewById(R.id.prog_bar);

        bluetoothImage = findViewById(R.id.bluetoothImg);
        batteryImage = findViewById(R.id.batteryImg);
        progressLayout = findViewById(R.id.relProgbar);

        progressLayout.setVisibility(View.INVISIBLE);

        /*
        createDialog();
        */

        /*
        connect();
        */

        // batteryInfo();
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

                        bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_connected));
                    } catch(IOException e) {
                        bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_disabled));

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

                            bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_connected));
                        } catch (IOException e) {
                            bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_disabled));

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
                bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_disabled));
                enableBluetooth();
            }
        }
        return false;
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

    public void chooseColor(View view) {
        b=(ImageButton)view;
        String id=getResources().getResourceEntryName(view.getId());
        Log.v("id",id);
        x=id.charAt(1)-'0';
        y=id.charAt(2)-'0';
        colorChooseDialog.show();
    }

    public void setColor(View view){
        String id=getResources().getResourceEntryName(view.getId());

        switch(id){
            case "yellow":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                Log.d("color","yellow in "+x+y);
                map[x][y]=1;
                break;

            case "blue":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                Log.d("color","blue in "+x+y);
                map[x][y]=2;
                break;

            case "green":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                Log.d("color","green in "+x+y);
                map[x][y]=3;
                break;

            case "red":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                Log.d("color", "red in "+x+y);
                map[x][y]=4;
                break;

            case "undo":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
                Log.d("color", "undo in "+x+y);
                if(map[x][y]==-1){
                    robotPlaceBtn.setVisibility(View.VISIBLE);
                    robot=false;
                }
                map[x][y]=0;
                break;

            case "robot":
                if(!robot) {
                    b.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    Log.d("robot", "robot in" + x + y);
                    map[x][y] = -1;
                    robot=true;
                    robotPlaceBtn.setVisibility(View.GONE);
                }
                break;
        }
        colorChooseDialog.hide();

    }

    public void exit(View view) {
        System.exit(0);
    }


    public void startSearch() {
        numberOfCells = 0;
        String message = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; i < 5; i++) {
                if (map[i][j] != 0) {
                    message = message.concat(i + j + map[i][j] + "&");
                    numberOfCells++;
                }
            }
        }

        try {
            Log.d("stringa", message);
            outputStream.write(message.getBytes(Charset.forName("UTF-8")));
            outputStream.write("\0".getBytes());

            progressLayout.setVisibility(View.VISIBLE);
            bar.setProgress(0);
            bar.setMax(numberOfCells);

            startReading();

        } catch (IOException e) {
            Log.d("errore", "write error");
        }
    }

    public void startReading(){
        byte[] bytes = new byte[numberOfCells];
        Boolean end = false;
        int x,y,check;
        checkedCells = 0;

        while(!end){
            try{
                inputStream.read(bytes, 0, 3);
                if(bytes[0] == END) {
                    end = true;
                    break;
                }
                x= bytes[0];
                y= bytes[1];
                check= bytes[2];
                String id = "b" + x + y;
                ImageButton b = findViewById(getResources().getIdentifier(id,null,null));

                checkedCells ++;

                b.setImageDrawable( getDrawable(check == CHECK ? R.drawable.ic_check : R.drawable.ic_clear));
                bar.setProgress(checkedCells);

            }catch (IOException e){
                e.printStackTrace(); // AGGIUNGERE
                Toast.makeText(this, "errore di ricezione", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void batteryInfo() {
        // lunghezza byte DA MODIFICARE
        int len = 2;
        int batteryVoltage = 0;

        /*
        byte[] inputByte = new byte[len];

        try {
            inputStream.read(inputByte, 0, len);
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Errore ricezione dati", Toast.LENGTH_SHORT).show();
        }*/

       if( batteryVoltage < VOLTAGE_LOW ){
           batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_20));
       } else if( batteryVoltage < VOLTAGE_MIDDLE ){
           batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_50));
       } else {
           batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_full));
       }
    }
}
