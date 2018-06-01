package com.site;


import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrensyDataLoader {
    private static final String NEW_LINE = "\n";


   static JsonObject getData(String CURRENCYLAYER_API) {
        try {
           URL url = new URL(CURRENCYLAYER_API);
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
           StringBuilder Data = new StringBuilder(1024);
           String tempVariable;

            while ((tempVariable = reader.readLine()) != null) {
                Data.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            JsonParser CurrencyParser = new JsonParser();
            JsonObject main = CurrencyParser.parse(Data.toString()).getAsJsonObject();
            return main;

        } catch (Exception e) {
            return null;
        }
    }
}
