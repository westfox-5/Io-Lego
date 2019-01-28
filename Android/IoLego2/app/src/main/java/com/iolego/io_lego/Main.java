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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class Main extends AppCompatActivity {
    private static final String
            TAG = "IO-LEGO",
            TAG_COLOR = "COLOR",
            END_STRING = "999";

    private static final int
            ROWS = 4,
            COLS = 4;

    private static final float
            VOLTAGE_LOW = 6.4f,
            VOLTAGE_MIDDLE = 6.9f;


    private View chooseColorView;
    private ImageButton b;
    private Button btnMain, btnUndo;
    private TextView infoTxt;
    private ImageView bluetoothImage, batteryImage;
    private int map[][], x, y, checkedCells, robot_x, robot_y;
    public Dialog colorChooseDialog, terminateDialog;
    private ProgressBar bar;

    private boolean
            bluetoothBound = false,
            searching = false,
            btConnected = true;

    private BluetoothConnection bt;
    private Handler
            reconnectHandler,
            progressBarHandler,
            imageHandler,
            btHandler;

    /**
     * -------- BT reconnection -----------------------------------------
     */
    int[] BTimageArray = {R.drawable.ic_bluetooth_disabled_black, R.drawable.ic_bluetooth_disabled};
    private final Runnable changeBTImage = new Runnable() {
        int i = 0;

        public void run() {
            bluetoothImage.setImageResource(BTimageArray[i]);
            i = (i + 1) % 2;
            btHandler.postDelayed(this, 1000);
        }
    };

    private void reconnect() {

        btHandler.removeCallbacks(changeBTImage);
        btHandler.post(changeBTImage);


        new Thread(new Runnable() {
            @Override
            public void run() {
                int ris;
                try {
                    ris = bt.connect(true);
                } catch (IOException e) {
                    ris = -1;
                    btConnected = false;
                }

                switch (ris) {
                    case 0:
                        Log.d(TAG, "Connection established");

                        btConnected = true;
                        imageHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothImage.setImageDrawable(getDrawable(R.drawable.ic_bluetooth_connected));
                            }
                        });
                        btHandler.removeCallbacks(changeBTImage);

                        read();

                        break;
                    default:
                        break;
                }
            }
        }).start();
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

                                    if (message.charAt(3) == '#') {
                                        Log.d(TAG, "Received battery info: " + message);
                                        setBatteryIcon(Float.parseFloat(message.substring(0, 3)));

                                    } else if (message.charAt(3) == '&') {
                                        if (message.substring(0, 3).equals(END_STRING)) {
                                            Log.d(TAG, "Search terminated");
                                            // here the search is terminated
                                            reset();

                                        } else {
                                            Log.d(TAG, "Received cell info: " + message);
                                            setChecked(Integer.parseInt(message.substring(0, 1)),
                                                    Integer.parseInt(message.substring(1, 2)),
                                                    Integer.parseInt(message.substring(2, 3)) == 1);
                                        }
                                    }

                                    btConnected = true;
                                } catch (IOException e) {
                                    Log.e(TAG, "Bluetooth unavailable");
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

    private ServiceConnection btService = new ServiceConnection() {
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

        Intent btIntent = new Intent(this, BluetoothConnection.class);
        startService(btIntent);
        bindService(btIntent, btService, Context.BIND_AUTO_CREATE);

        /* choose color creation */
        colorChooseDialog = new Dialog(this);
        chooseColorView = getLayoutInflater().inflate(R.layout.choose_color, null);
        colorChooseDialog.setContentView(chooseColorView);
        Objects.requireNonNull(colorChooseDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        terminateDialog = createTerminateDialog();

        /* handlers*/
        reconnectHandler = new Handler();
        progressBarHandler = new Handler();
        imageHandler = new Handler();
        btHandler = new Handler();

        /* UI elements */
        bar = findViewById(R.id.prog_bar);
        bluetoothImage = findViewById(R.id.bluetoothImg);
        batteryImage = findViewById(R.id.batteryImg);
        btnMain = findViewById(R.id.startBtn);
        infoTxt = findViewById(R.id.infoTXT);
        btnUndo = chooseColorView.findViewById(R.id.undo);


        infoTxt.setText(R.string.info_place_color);
        bar.setVisibility(View.INVISIBLE);

        robot_x = 0;
        robot_y = 0;
        ImageButton b = findViewById(
                getResources().getIdentifier(String.format(Locale.ITALY, "b%d%d", robot_x, robot_y), "id", getPackageName())
        );
        b.setImageResource(R.drawable.button_robot);
        map = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            map[i] = new int[COLS];
            for (int j = 0; j < COLS; j++) {
                map[i][j] = 0;
            }
        }

        map[robot_x][robot_y] = -1;

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothBound) {
                    if (btConnected) {
                        if (searching) {
                            stopSearch();
                        } else {
                            startSearch();
                        }
                        searching = !searching;
                    } else {
                        Log.e(TAG, "Bluetooth not connected");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.bt_failed), Toast.LENGTH_SHORT).show();
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

    private void startSearch() {
        int numberOfCells = 0;
        /* initial position of robot: Rxy&*/
        String message = "R".concat(String.valueOf(robot_x))
                .concat(String.valueOf(robot_y)).concat("&");

        /* change btnMain to endSearch */
        btnMain.setText(getResources().getString(R.string.break_search));
        btnMain.setBackgroundColor(getResources().getColor(R.color.red));

        /* create the string as ROW|COL|COLOR|& */
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (map[i][j] > 0) {
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

                //* update UI *//*
                bar.setVisibility(View.VISIBLE);
                bar.setProgress(0);
                bar.setMax(numberOfCells);
                infoTxt.setBackground(null);
                infoTxt.setText(R.string.searching);

                searching = true;
                btConnected = true;
            }

        } catch (IOException e) {
            Toast.makeText(this, getResources().getText(R.string.error_send), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cannot send to EV3!");
            btConnected = false;
            searching = false;
        }
    }

    private void stopSearch() {
        /* change btnMain */
        btnMain.setText(getResources().getString(R.string.start_search));
        btnMain.setBackgroundColor(getResources().getColor(R.color.green));

        try {
            bt.send("999&");
            btConnected = true;

            bar.setVisibility(View.INVISIBLE);
            bar.setProgress(0);
            infoTxt.setBackground(ContextCompat.getDrawable(this, R.drawable.info_text));
            infoTxt.setText(R.string.search_interrupted);

            searching = false;
            reset();

        } catch (IOException e) {
            Toast.makeText(this, getResources().getText(R.string.error_send), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cannot send to EV3!");

            btConnected = false;
        }
    }

    private void reset() {
        terminateDialog.show();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String id = "b" + i + "" + j;
                ImageButton btn = findViewById(
                        getResources().getIdentifier(id, "id", getPackageName())
                );
                btn.setImageResource(R.drawable.button_disabled);
            }
        }
    }

    public void chooseColor(View view) {
        b = (ImageButton) view;

        String id = getResources().getResourceEntryName(view.getId());
        x = id.charAt(1) - '0';
        y = id.charAt(2) - '0';

        if (map[x][y] == 0) {
            btnUndo.setVisibility(View.INVISIBLE);
        } else {
            btnUndo.setVisibility(View.VISIBLE);
        }

        colorChooseDialog.show();
    }

    public void setColor(View view) {

        String id = getResources().getResourceEntryName(view.getId());

        //always can place the robot
        if (id.equals("robot")) {
            Log.d(TAG_COLOR, "Robot in " + x + y);

            // reset the previous robot cell
            map[robot_x][robot_y] = 0;
            ImageButton btn = findViewById(
                    getResources().getIdentifier("b" + robot_x + "" + robot_y, "id", getPackageName())
            );
            btn.setImageResource(R.drawable.button_disabled);


            // update robot position
            robot_x = x;
            robot_y = y;
            map[x][y] = -1;
            b.setImageResource(R.drawable.button_robot);


        } else {

            if (id.equals("undo")) {
                //cannot remove robot
                if (map[x][y] != -1) {

                    b.setImageResource(R.drawable.button_disabled);
                    Log.d(TAG_COLOR, "Undo in " + x + y);
                    map[x][y] = 0;
                } else {
                    infoTxt.setTextColor(getResources().getColor(R.color.red));
                    infoTxt.setText(R.string.info_cannot_remove_robot);
                    infoTxt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoTxt.setTextColor(getResources().getColor(R.color.white));
                            infoTxt.setText(R.string.info_place_color);
                        }
                    }, 2000);
                }

                colorChooseDialog.hide();
                return;
            }


            switch (id) {
                case "yellow":
                    b.setImageResource(R.drawable.button_yellow);
                    Log.d(TAG_COLOR, "Yellow in " + x + y);
                    map[x][y] = 1;
                    break;

                case "blue":
                    b.setImageResource(R.drawable.button_blue);
                    Log.d(TAG_COLOR, "Blue in " + x + y);
                    map[x][y] = 2;
                    break;

                case "green":
                    b.setImageResource(R.drawable.button_green);
                    Log.d(TAG_COLOR, "Green in " + x + y);
                    map[x][y] = 3;
                    break;

                case "red":
                    b.setImageResource(R.drawable.button_red);
                    Log.d(TAG_COLOR, "Red in " + x + y);
                    map[x][y] = 4;
                    break;
            }

        }

        colorChooseDialog.hide();
    }

    private void setChecked(int row, int col, final boolean correct) {
        String id = "b" + row + col;

        final ImageButton b = findViewById(getResources().getIdentifier(id, "id", getPackageName()));

        checkedCells++;

        imageHandler.post(new Runnable() {
            @Override
            public void run() {
                b.setImageResource(correct ? R.drawable.ic_check : R.drawable.ic_clear);
            }
        });

        progressBarHandler.post(new Runnable() {
            @Override
            public void run() {
                bar.setProgress(checkedCells);
            }
        });
    }

    private void setBatteryIcon(float batteryVoltage) {
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

    private AlertDialog createTerminateDialog() {
        AlertDialog.Builder build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            build = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            build = new AlertDialog.Builder(this);
        }
        build.setIcon(getDrawable(R.drawable.logo));
        build.setCancelable(false);
        build.setTitle(getString(R.string.alert_end_title));
        build.setMessage(getString(R.string.alert_end_message));
        build.setPositiveButton(getString(R.string.alert_end_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                terminateDialog.dismiss();
            }
        });
        build.setNegativeButton(getString(R.string.alert_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        return build.create();
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
                Main.this.finish();
                System.exit(0);
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
        if (btService != null) {
            unbindService(btService);
        }
        super.onDestroy();
    }

}
