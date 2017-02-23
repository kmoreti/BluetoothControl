package com.example.kmoreti.bluetoothcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class MainControl extends Activity {


    Button btnUp, btnDown, btnLeft, btnRight, btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectBT connectBT = null;

    private static final String CONNECTION = "connection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the mainControl
        setContentView(R.layout.activity_main_control);

        //call the widgtes
        btnUp = (Button) findViewById(R.id.buttonUp);
        btnDown = (Button) findViewById(R.id.buttonDown);
        btnLeft = (Button) findViewById(R.id.buttonLeft);
        btnRight = (Button) findViewById(R.id.buttonRight);
        btnDis = (Button) findViewById(R.id.buttonDisconnect);

        if (savedInstanceState == null) {
            connectBT = new ConnectBT();
            connectBT.execute(); //Call the class to connect
        } else {
            connectBT = (ConnectBT) savedInstanceState.getSerializable(CONNECTION);
            btSocket = connectBT.socket;
        }

        //commands to be sent to bluetooth
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnUp();      //method to turn up
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnDown();   //method to turn down
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnLeft();   //method to turn left
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnRight();   //method to turn right
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });
    }

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }

    private void turnUp() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(0);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnDown() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(1);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnLeft() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(2);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnRight() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(3);
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Disconnect();
//    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CONNECTION, connectBT);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> implements Serializable // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        BluetoothSocket socket = null;
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    socket = btSocket;
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}

