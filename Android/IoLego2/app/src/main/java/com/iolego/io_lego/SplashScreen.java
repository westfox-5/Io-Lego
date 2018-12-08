package com.iolego.io_lego;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG= "SPLASH_ACTIVITY";

    private BluetoothConnection bt;
    private Dialog reconnectDialog;
    private TextView btConnTXT;
    private ProgressBar bar;
    private Handler barHandler;

    private Runnable hide_bar = new Runnable() {
        @Override
        public void run() {
            bar.setVisibility(View.GONE);
        }
    };

    private Runnable show_bar = new Runnable() {
        @Override
        public void run() {
            bar.setIndeterminate(true);
            bar.setVisibility(View.VISIBLE);
        }
    };




    private ServiceConnection btService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothConnection.BluetoothBinder binder = (BluetoothConnection.BluetoothBinder)iBinder;

            bt = binder.getService();

            barHandler.post( show_bar );

            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            connect();
                        }
                    }, 500);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);

        btConnTXT = findViewById(R.id.connectTXT);
        bar = findViewById(R.id.progressBar);

        barHandler = new Handler();

        reconnectDialog = createDialog();

        Intent btIntet = new Intent(this, BluetoothConnection.class);
        startService(btIntet);
        bindService(btIntet, btService, Context.BIND_AUTO_CREATE);


    }

    private void connect() {
        if(reconnectDialog.isShowing()) reconnectDialog.dismiss();
        int ris;
        try {
            ris = bt.connect();
        } catch (IOException e) {
            ris =-1;
        }
        switch(ris) {

            case 0: // connesso
                Log.d(TAG,"Connection established");
                btConnTXT.post(new Runnable() {
                    @Override
                    public void run() {
                        btConnTXT.setText(getResources().getString(R.string.bt_connected));
                    }
                });

                barHandler.removeCallbacks(show_bar);
                barHandler.post(hide_bar);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), Main.class);
                        startActivity(i);

                        SplashScreen.this.finish();
                        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);

                    }
                }, 1000);

                break;
            case 1: // retry
            case -1:
                Log.d(TAG, "Reconnecting");

                barHandler.removeCallbacks(show_bar);
                barHandler.post(hide_bar);

                reconnectDialog.show();
                break;
            case 2: // bt non acceso
                btConnTXT.post(new Runnable() {
                    @Override
                    public void run() {
                        btConnTXT.setText(getResources().getString(R.string.bt_not_enable));
                    }
                });

                barHandler.removeCallbacks(show_bar);
                barHandler.post(hide_bar);

                enableBluetooth();
                break;
        }
    }

    private AlertDialog createDialog() {
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
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        barHandler.removeCallbacks(hide_bar);
                        barHandler.post(show_bar);
                        connect();
                    }
                });
            }
        });
        build.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        return build.create();
    }

    private void enableBluetooth() {
        try {
            Thread.sleep(500);
        }catch(Exception e) {
            e.printStackTrace();
        }
        Intent enabelBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enabelBT,0 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            Toast.makeText(this, getResources().getString(R.string.bt_enable), Toast.LENGTH_SHORT).show();
            btConnTXT.setText(R.string.bt_pairing);
            reconnectDialog.show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.bt_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if(btService !=null) {
            unbindService(btService);
        }
        super.onDestroy();
    }
}
