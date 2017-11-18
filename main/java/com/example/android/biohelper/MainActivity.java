package com.example.android.biohelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.biohelper.data.DataContract;
import com.example.android.biohelper.data.DataDBHelper;
import com.example.android.biohelper.data.Packet;
import com.example.android.biohelper.data.DataContract.DataEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import java.util.Set;

import static com.example.android.biohelper.R.id.temp;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    public static final int MESSAGE_READ = 0;

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private Handler mHandler;

    private DataDBHelper mDBHelper;
    public final long SECONDS = 5000;

    float dummy_temp;
    float dummy_ph;
    float dummy_ec;

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(mReceiver, filter);

        // Setup FAB to open GraphActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        mDBHelper = new DataDBHelper(this);

        //displayDatabaseInfo();
        if(mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        else
        {
            ReceivePacketTask task = new ReceivePacketTask();
            task.execute();
        }

    }

    private class ReceivePacketTask extends AsyncTask< Void, Void , Void > {
        String tempString;

        protected  void onPreExecute(){

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            }

            InputStream tmpIn = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            mmInStream = tmpIn;
        }

        protected Void doInBackground(Void... voids) {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    tempString = readMsg.toString();


                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }

            }


            Packet packet = new Packet(dummy_temp, dummy_ph, dummy_ec);     // Contains dummy values for now, should be changed after method to recieve values is implemented.
            insertData(packet);             //Insert packet of data to database
            //And then display current data to screen
            displayData(packet.getTemp(), packet.getPH(), packet.getEC());

            return null;
        }

        protected void onPostExecute() {
            TextView tempTextView = (TextView) findViewById(R.id.tempTextView);
            tempTextView.setText(tempString);
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };
    /*
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //functions
            Packet packet = new Packet(dummy_temp, dummy_ph, dummy_ec);     // Contains dummy values for now, should be changed after method to recieve values is implemented.
            insertData(packet);             //Insert packet of data to database
                                            //And then display current data to screen
            displayData(packet.getTemp(), packet.getPH(), packet.getEC());

            handler.postDelayed(this, SECONDS);
        }
    };*/

    //Start
    //handler.postDelayed(runnable, SECONDS);

    /**
     * A method that inserts a packet of data to the database
     * @param packet - the packet containing the data
     */
    private void insertData(Packet packet) {
        long dateTime = packet.getTime();
        float temp = packet.getTemp();
        float ph = packet.getPH();
        float ec = packet.getEC();

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues newDataValues = new ContentValues();
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_DATETIME, dateTime);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_TEMP, temp);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_PH, ph);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_EC, ec);

        long newRowId = db.insert(DataEntry.TABLE_NAME, null, newDataValues);

        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display data fields to screen, should be used for current data.
     * @param temp - data for current temperature
     * @param ph - data for current ph
     * @param ec - data for current ec
     */
    private void displayData(float temp, float ph, float ec) {
        TextView tempTextView = (TextView) findViewById(R.id.temp_display);
        tempTextView.setText("" + temp);
        TextView phTextView = (TextView) findViewById(R.id.ph_display);
        phTextView.setText("" + ph);
        TextView ecTextView = (TextView) findViewById(R.id.ec_display);
        ecTextView.setText("" + ec);
    }


    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public static class MyBluetoothService {
        private static final String TAG = "MY_APP_DEBUG_TAG";
        private Handler mHandler; // handler that gets info from Bluetooth service

        // Defines several constants used when transmitting messages between the
        // service and the UI.
        private interface MessageConstants {
            public final int MESSAGE_READ = 0;
            public final int MESSAGE_WRITE = 1;
            public final int MESSAGE_TOAST = 2;

            // ... (Add other message types here as needed.)
        }

        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            private byte[] mmBuffer; // mmBuffer store for the stream

            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams; using temp objects because
                // member streams are final.
                try {
                    tmpIn = socket.getInputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating input stream", e);
                }
                try {
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating output stream", e);
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                mmBuffer = new byte[1024];
                int numBytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);
                        // Send the obtained bytes to the UI activity.
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_READ, numBytes, -1,
                                mmBuffer);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);
                        break;
                    }
                }
            }

            // Call this method from the main activity to shut down the connection.
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
                }
            }
        }
    }


}
