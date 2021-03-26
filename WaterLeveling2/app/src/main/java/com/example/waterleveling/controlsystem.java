package com.example.waterleveling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class controlsystem extends AppCompatActivity {

    Button change1, change2, change3, set1, set2, set3, discon, percentage;
    EditText tankheight, intill, outtill, percent;

    TextView textview4, textview6;

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Handler h;
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlsystem);

        if (btSocket == null){
            new ConnectBT().execute();
        }

        change1 = findViewById(R.id.button2);
        set1 = findViewById(R.id.button3);
        tankheight = findViewById(R.id.editTextNumberDecimal);
        change2 = findViewById(R.id.button4);
        set2 = findViewById(R.id.button5);
        intill = findViewById(R.id.editTextNumberDecimal2);
        change3 = findViewById(R.id.button6);
        set3 = findViewById(R.id.button7);
        outtill = findViewById(R.id.editTextNumberDecimal3);

        textview4 = findViewById(R.id.textView4);
        textview6 = findViewById(R.id.textView6);
        percentage = findViewById(R.id.button9);
        percent = findViewById(R.id.editTextNumberDecimal4);

        discon = findViewById(R.id.btn_disconnect);


        set1.setEnabled(false);
        set2.setEnabled(false);
        set3.setEnabled(false);
        tankheight.setEnabled(false);
        intill.setEnabled(false);
        outtill.setEnabled(false);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        if (btSocket == null){
                            new ConnectBT().execute();
                        }
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            Log.d("sbprint:", sbprint);
                            sb.delete(0, sb.length());                                      // and clear
                            String saperate[] = sbprint.split(":");
                            Log.d("saperate length", String.valueOf(saperate.length));
                            if (saperate.length == 6){
                                String inheight = saperate[0];
                                Log.d("inheight:", inheight);
                                if (set2.isEnabled() == false){
                                    intill.setText(inheight);
                                }
                                String outheight = saperate[1];
                                Log.d("outheight:", outheight);
                                if (set3.isEnabled() == false){
                                    outtill.setText(outheight);
                                }
                                String TankHeight = saperate[3];
                                Log.d("TankHeight:", TankHeight);
                                if (set1.isEnabled() == false){
                                    tankheight.setText(TankHeight);
                                }
                                String distance = saperate[2];
                                Log.d("distance:", distance);
                                textview4.setText("Water Height " + distance + " cm");
                                String Percentage = saperate[4];
                                Log.d("Percentage:", Percentage);
                                textview6.setText("Percentage Filled " + Percentage +"%");
                            }


                            //textview4.setText("Data from Arduino: " + sbprint);            // update TextView

                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };

        change1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set1.setEnabled(true);
                tankheight.setEnabled(true);
                change1.setEnabled(false);
            }
        });

        set1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height();
            }
        });

        change2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set2.setEnabled(true);
                intill.setEnabled(true);
                change2.setEnabled(false);
            }
        });

        set2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gintill();
            }
        });

        change3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set3.setEnabled(true);
                outtill.setEnabled(true);
                change3.setEnabled(false);
            }
        });

        set3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gouttill();
            }
        });

        discon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        percentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Percentage();
            }
        });

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  //Connecting to Bluetooth AsyncTask
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(controlsystem.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    String address = getIntent().getStringExtra("EXTRA_ADDRESS");
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msgtoast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msgtoast("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msgtoast(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    public void height(){
        if (btSocket != null){
                set1.setEnabled(false);
                tankheight.setEnabled(false);
                change1.setEnabled(true);
                String getheight = "Tank Height*" + tankheight.getText() + "|";
                mConnectedThread.write(getheight);
//                btSocket.getOutputStream().write(getheight.toString().getBytes());
                Log.d("Sent: ", getheight);
        }
    }

    public void gintill(){
        if (btSocket != null){
                set2.setEnabled(false);
                intill.setEnabled(false);
                change2.setEnabled(true);
                String getintill = "Height*" + intill.getText() + "|";
                mConnectedThread.write(getintill);
//                btSocket.getOutputStream().write(getintill.toString().getBytes());
                Log.d("Sent: ", getintill);
        }
    }

    public void gouttill(){
        if (btSocket != null){
                set3.setEnabled(false);
                outtill.setEnabled(false);
                change3.setEnabled(true);
                String getouttill = "Lowest*" + outtill.getText() + "|";
                mConnectedThread.write(getouttill);
//                btSocket.getOutputStream().write(getouttill.toString().getBytes());
                Log.d("Sent: ", getouttill);
        }
    }

    public void Percentage(){
        if (btSocket != null) {
            String setpercentage = "Percentage*" + percent.getText() + "|";
            mConnectedThread.write(setpercentage);
            Log.d("Sent: ", setpercentage);
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }
    }

    public void disconnect(){
        if (btSocket != null){
            try{
                btSocket.close();
            }
            catch (IOException e){
                msgtoast("Error");
            }
        }
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    if (btSocket == null){
                        new ConnectBT().execute();
                    }
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
