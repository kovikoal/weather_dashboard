package com.site;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class WeatherDataLoader {

    private static final String NEW_LINE = "\n";


    static JsonObject getData(String city, String OPEN_WEATHER_MAP_API) {
        try {
            URL url1 = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
            StringBuilder Data = new StringBuilder(1024);
            String tempVariable;
            while ((tempVariable = reader1.readLine()) != null) {
                Data.append(tempVariable).append(NEW_LINE);
            }
            reader1.close();

            JsonParser WeatherParser = new JsonParser();
            JsonObject main = WeatherParser.parse(Data.toString()).getAsJsonObject();
            return main;

        } catch (Exception e) {
            return null;
        }
    }
}
