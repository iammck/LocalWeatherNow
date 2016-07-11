package com.mck.quicktemps.service;

import android.content.Context;
import android.util.Log;

import com.mck.quicktemps.Constants;
import com.mck.quicktemps.model.LocationData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * API for connecting to OpenWeatherMap and getting data such as
 * current weather, forecasts and icons.
 * Created by Michael on 7/2/2016.
 */
public class OpenWeatherMapService {
    private static final String API_ID = Constants.API_ID;
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String TAG = "OpenWeatherMapService";

    public static String requestCurrent(LocationData locationData) {
        if (locationData == null) return null;
        try {
            String request = String.format(Locale.US,
                    "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&APPID=%s",
                    locationData.latitude, locationData.longitude, API_ID);
            Log.v(TAG, "making request with statement " + request);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            Log.v(TAG, "requestCurrent() returning");
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String requestForecast(LocationData locationData) {
        if (locationData == null) return null;
        try {
            String request = String.format( Locale.US,
                    "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&cnt=%d&APPID=%s",
                    locationData.latitude, locationData.longitude,
                    Constants.NUM_PERIODS_TO_REQUEST, API_ID);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            Log.v(TAG, "requestForecast() returning");
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getAndSaveIconFromNetwork(Context context, String iconId){
        try {
            String request = String.format( Locale.US,
                    "http://openweathermap.org/img/w/%s.png", iconId);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            if (connection.getResponseCode() == 200){
                // creating the input stream from icon
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                File file = new File(context.getFilesDir(), "icon" + iconId + ".png");
                // my local file writer, output stream
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                // until end of data, keep writing to file.
                int i;
                while ((i = in.read()) != -1){
                    out.write(i);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
