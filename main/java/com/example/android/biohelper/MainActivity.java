package com.example.android.biohelper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.biohelper.data.DataContract;
import com.example.android.biohelper.data.DataDBHelper;
import com.example.android.biohelper.data.Packet;
import com.example.android.biohelper.data.DataContract.DataEntry;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity {

    private DataDBHelper mDBHelper;
    public final long SECONDS = 5000;

    float dummy_temp;
    float dummy_ph;
    float dummy_ec;
    float dummy_pressure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
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

    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //functions
            Packet packet = new Packet(dummy_temp, dummy_ph, dummy_ec, dummy_pressure);     // Contains dummy values for now, should be changed after method to recieve values is implemented.
            insertData(packet);             //Insert packet of data to database
                                            //And then display current data to screen
            displayData(packet.getTemp(), packet.getPH(), packet.getEC(), packet.getPressure());

            handler.postDelayed(this, SECONDS);
        }
    };

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
        float pressure = packet.getPressure();

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues newDataValues = new ContentValues();
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_DATETIME, dateTime);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_TEMP, temp);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_PH, ph);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_EC, ec);
        newDataValues.put(DataContract.DataEntry.COLUMN_DATA_PRESSURE, pressure);

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
     * @param pressure - data for current pressure
     */
    private void displayData(float temp, float ph, float ec, float pressure) {
        TextView tempTextView = (TextView) findViewById(R.id.temp_display);
        tempTextView.setText("" + temp);
        TextView phTextView = (TextView) findViewById(R.id.ph_display);
        phTextView.setText("" + ph);
        TextView ecTextView = (TextView) findViewById(R.id.ec_display);
        ecTextView.setText("" + ec);
        TextView pressureTextView = (TextView) findViewById(R.id.pres_display);
        pressureTextView.setText("" + pressure);
    }

}
