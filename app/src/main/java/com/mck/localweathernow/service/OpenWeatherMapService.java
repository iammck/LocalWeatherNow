package com.mck.localweathernow.service;

import com.mck.localweathernow.Constants;
import com.mck.localweathernow.model.LocationData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Michael on 7/2/2016.
 */
public class OpenWeatherMapService {
    private static final String API_ID = Constants.API_ID;
    private static final String USER_AGENT = "Mozilla/5.0";

    public static String requestCurrent(LocationData locationData) {
        if (locationData == null) return null;
        try {
            String request = String.format(Locale.US,
                    "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=%s&APPID=%s",
                    locationData.latitude, locationData.longitude, Constants.UNITS, API_ID);
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
                    "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&cnt=%d&units=%s&APPID=%s",
                    locationData.latitude, locationData.longitude,
                    Constants.NUM_PERIODS_TO_REQUEST, Constants.UNITS, API_ID);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            } // TODO HANDLE BAD RESPONSES.
            in.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
