package com.idm.polygon.utilities;

/**
 * Created by adoler on 23/6/2017.
 */
import java.io.*;
import java.text.*;
import java.util.*;

public class Logger {
    protected static String defaultLogFile = "";


    public Logger() {}

    public static void write(String s) {
        write(defaultLogFile, s);
    }

    public static void write(String f, String s) {
        TimeZone tz = TimeZone.getTimeZone("EST"); // or PST, MID, etc ...
        Date now = new Date();
        DateFormat df = new SimpleDateFormat ("yyyy.mm.dd hh:mm:ss ");
        df.setTimeZone(tz);
        String currentTime = df.format(now);

        FileWriter aWriter = null;
        try {
            aWriter = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aWriter.write(currentTime + " " + s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
