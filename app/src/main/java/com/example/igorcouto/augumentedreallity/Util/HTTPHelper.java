package com.example.igorcouto.augumentedreallity.Util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HTTPHelper {

    private final String CHARSET = "UTF-8";
    //private final String URL = "http://localhost:4444/";
    private final String URL = "https://api-localizar-ufjf-igorcouto.c9users.io/";
    private final int TIMEOUT_MILLIS = 30000;


    public void getPlaces(){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String response = doGet("places");
                    JSONArray places = new JSONArray(response);
                    PlacesHelper.getInstance().SetPlaces(places);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private String doGet(String path) throws IOException {
        //return doGet(url, null, "UTF-8");
        URL url = new URL(URL + path);
        HttpURLConnection connection = null;
        String response;

        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MILLIS);
            connection.setReadTimeout(TIMEOUT_MILLIS);
            connection.connect();

            InputStream in;

            int status = connection.getResponseCode();
            if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }

            byte[] bytes = toBytes(in);
            response = new String(bytes, CHARSET);
            in.close();

        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) connection.disconnect();
        }

        return response;
    }

    private byte[] toBytes(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0)
                baos.write(buffer, 0, len);
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                baos.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray parserJSON(String JSONResponse) throws JSONException {
        final String LONGITUDE_TAG = "lng";
        final String LATITUDE_TAG  = "lat";
        final String NAME_TAG      = "name";
        final String INFO_TAG      = "info";
        final String AREA_TAG      = "arera";

        List<String> result = new ArrayList<>();

        JSONObject responseObject = new JSONObject(JSONResponse);

        JSONArray places = responseObject.getJSONArray("locations");

        for (int idx = 0; idx < places.length(); idx++) {

            JSONObject place = (JSONObject) places.get(idx);

            result.add(NAME_TAG + ":"
                    + place.get(NAME_TAG) + ","
                    + LATITUDE_TAG + ":"
                    + place.getString(LATITUDE_TAG) + ","
                    + LONGITUDE_TAG + ":"
                    + place.get(LONGITUDE_TAG)
                    + INFO_TAG + ":"
                    + place.get(INFO_TAG));
        }
        return places;
    }
}
