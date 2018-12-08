package com.iolego.io_lego;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "IO-LEGO_ACTIVITY";
    private static final String TAG_COLOR = "COLOR";

    private static final int VOLTAGE_LOW = 3;
    private static final int VOLTAGE_MIDDLE = 6;

    private int numberOfCells;
    private int checkedCells;

    private ImageButton b;
    private Button btn;
    private ImageView bluetoothImage, batteryImage;
    private View progressLayout;
    private int map[][], x, y;
    public Dialog colorChooseDialog, reconnectDialog;
    private ProgressBar bar;

    private boolean bluetoothBound = false, searching = false, btConnected = true;
    private BluetoothConnection bt;
    private Handler reconnectHandler, progressBarHandler, imageHandler, btHandler;
    int []imageArray={R.drawable.ic_bluetooth_disabled_black,R.drawable.ic_bluetooth_disabled};

    final Runnable runnable = new Runnable() {

        int i=0;
        public void run() {
            bluetoothImage.setImageResource(imageArray[i]);
            i++;
            if(i>imageArray.length-1)
            {
                i=0;
            }
            btHandler.postDelayed(this, 1000);  //for interval...
        }
    };

    private ServiceConnection btServ = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothConnection.BluetoothBinder binder = (BluetoothConnection.BluetoothBinder) iBinder;

            bluetoothBound = true;
            bt = binder.getService();

            // read from bluetooth
            read();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_refactor);

        Intent btIntet = new Intent(this, BluetoothConnection.class);
        startService(btIntet);
        bindService(btIntet, btServ, Context.BIND_AUTO_CREATE);

        /* reconnect dialog creation */
        reconnectDialog = new Dialog(this);
        reconnectDialog.setContentView(R.layout.reconnect);
        reconnectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /* choose color creation */
        colorChooseDialog = new Dialog(this);
        colorChooseDialog.setContentView(R.layout.choose_color);
        colorChooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /* handlers*/
        reconnectHandler = new Handler();
        progressBarHandler = new Handler();
        imageHandler = new Handler();
        btHandler = new Handler();

        /* UI elements */
        bar = findViewById(R.id.prog_bar);
        bluetoothImage = findViewById(R.id.bluetoothImg);
        batteryImage = findViewById(R.id.batteryImg);
        progressLayout = findViewById(R.id.relProgbar);
        progressLayout.setVisibility(View.INVISIBLE);
        btn = findViewById(R.id.startBtn);

        map = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                map[i][j] = 0;
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothBound) {
                   if(btConnected) {
                       if (searching) {
                           stopSearch();
                       } else {
                           startSearch();
                       }
                       searching= !searching;
                   } else{
                       Log.e(TAG,"BT not connected");
                    }
                }
            }
        });


        bluetoothImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reconnect();
            }
        });

    }


    public void chooseColor(View view) {
        b = (ImageButton) view;
        String id = getResources().getResourceEntryName(view.getId());
        x = id.charAt(1) - '0';
        y = id.charAt(2) - '0';
        colorChooseDialog.show();
    }

    public void setColor(View view) {
        String id = getResources().getResourceEntryName(view.getId());

        switch (id) {
            case "yellow":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                Log.d(TAG_COLOR, "Yellow in " + x + y);
                map[x][y] = 1;
                break;

            case "blue":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                Log.d(TAG_COLOR, "Blue in " + x + y);
                map[x][y] = 2;
                break;

            case "green":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                Log.d(TAG_COLOR, "Green in " + x + y);
                map[x][y] = 3;
                break;

            case "red":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                Log.d(TAG_COLOR, "Red in " + x + y);
                map[x][y] = 4;
                break;

            case "undo":
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
                Log.d(TAG_COLOR, "Undo in " + x + y);
                map[x][y] = 0;
                break;
        }
        colorChooseDialog.hide();
    }

    public void startSearch() {
        numberOfCells = 0;
        String message = "";

        /* change btn */
        btn.setText(getResources().getString(R.string.break_search));
        btn.setBackgroundColor(getResources().getColor(R.color.red));

        /* create the string as ROW|COL|COLOR|& */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; i < 5; i++) {
                if (map[i][j] != 0) {
                    message = message.concat(String.valueOf(i))
                            .concat(String.valueOf(j))
                            .concat(String.valueOf(map[i][j]) + '&');
                    numberOfCells++;
                }
            }
        }

        /* send the string to EV3 */
        try {
            if (bluetoothBound) {
                bt.send(message);

                /* update UI */
                progressLayout.setVisibility(View.VISIBLE);
                bar.setProgress(0);
                bar.setMax(numberOfCells);

                btConnected = true;
            }

        } catch (IOException e) {
            Toast.makeText(this, getResources().getText(R.string.error_send), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cannot send to EV3!");
            btConnected = false;
        }
    }

    void stopSearch() {
        /* change btn */
        btn.setText(getResources().getString(R.string.start_search));
        btn.setBackgroundColor(getResources().getColor(R.color.green));


        try {
            bt.send("999&");
            btConnected = true;
        } catch (IOException e) {
            Toast.makeText(this, getResources().getText(R.string.error_send), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cannot send to EV3!");

            btConnected = false;
        }
    }


    private void read() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            String message;
                            if (bluetoothBound) {
                                try {
                                    message = bt.read();


                                    if (message.charAt(1) == '#') {

                                        Log.d(TAG, "Received battery info: "+message);
                                        setBatteryIcon(Integer.parseInt(message.substring(0, 1)));

                                    } else if (message.charAt(3) == '&') {


                                        if (message.equals("999&")) {
                                            Log.d(TAG, "Search terminated");
                                        } else {
                                            Log.d(TAG, "Received cell info: "+message);
                                            setChecked(Integer.parseInt(message.substring(0, 1)),
                                                    Integer.parseInt(message.substring(1, 2)),
                                                    Integer.parseInt(message.substring(2, 3)) == 1);
                                        }
                                    }

                                    btConnected = true;
                                } catch (IOException e) {
                                    Log.e(TAG, "BT unavailable");
                                    btConnected = false;
                                    reconnectHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            reconnect();
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }
        ).start();

    }

    private void setChecked(int row, int col, final boolean correct) {
        String id = "b" + row + col;

        final ImageButton b = findViewById(getResources().getIdentifier(id, "id", getPackageName()));

        checkedCells++;

        imageHandler.post(new Runnable() {
            @Override
            public void run() {
                b.setBackground(getDrawable(correct ? R.drawable.ic_check : R.drawable.ic_clear));
            }
        });

        progressBarHandler.post(new Runnable() {
            @Override
            public void run() {
                bar.setProgress(checkedCells);
            }
        });
    }

    private void setBatteryIcon(int batteryVoltage) {
        if (batteryVoltage == -1) {
            batteryImage.post(new Runnable() {
                @Override
                public void run() {
                    batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_unknown));
                }
            });
        } else if (batteryVoltage < VOLTAGE_LOW) {
            batteryImage.post(new Runnable() {
                @Override
                public void run() {
                    batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_20));
                }
            });
        } else if (batteryVoltage < VOLTAGE_MIDDLE) {
            batteryImage.post(new Runnable() {
                @Override
                public void run() {
                    batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_50));

                }
            });
        } else {
            batteryImage.post(new Runnable() {
                @Override
                public void run() {
                    batteryImage.setImageDrawable(getDrawable(R.drawable.ic_battery_full));
                }
            });
        }
    }

    private void reconnect() {

        btHandler.removeCallbacks(runnable);
        btHandler.post(runnable); //for initial delay..



        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("LOL","CONNECTION??");

                int ris;
                try {
                    ris = bt.connect();
                } catch (IOException e) {
                    ris = -1;
                    btConnected = false;
                }
                switch (ris) {
                    case 0: // connesso
                        Log.d(TAG, "Connection established");

                        btConnected = true;
                        imageHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_connected));
                            }
                        });

                        read();

                        break;
                    default: break;
                } }
        }).start();

    }

    @Override
    public void onBackPressed() {

        if (isTaskRoot())
            super.onBackPressed();
        AlertDialog.Builder build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            build = new android.support.v7.app.AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            build = new android.support.v7.app.AlertDialog.Builder(this);
        }
        build.setCancelable(false);
        build.setTitle(R.string.alert_exit_title);
        build.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        build.create().show();
    }

    @Override
    protected void onDestroy() {
        if (btServ != null) {
            unbindService(btServ);
        }
        super.onDestroy();
    }
}
