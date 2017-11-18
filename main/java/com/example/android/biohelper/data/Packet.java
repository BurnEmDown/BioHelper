package com.example.android.biohelper.data;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * This class holds packets of data, including information about:
 * temperature, ph, ec, pressure, and the time the data was collected.
 * Created by USER on 03/09/2017.
 */

public class Packet {

    DateTime dt = new DateTime();
    DateTime dtIsrael = dt.withZone(DateTimeZone.forID("Asia/Jerusalem"));

    public DateTime m_dateTime;
    public float m_temperature;
    public float m_ph;
    public float m_ec;

    public Packet(float temperature, float ph, float ec)
    {

        this.m_dateTime = dt;
        this.m_temperature = temperature;
        this.m_ph = ph;
        this.m_ec = ec;
    }

    public long getTime()
    {
        return this.m_dateTime.getMillis();
    }

    public float getTemp()
    {
        return this.m_temperature;
    }

    public float getPH()
    {
        return this.m_ph;
    }

    public float getEC()
    {
        return this.m_ec;
    }



}
