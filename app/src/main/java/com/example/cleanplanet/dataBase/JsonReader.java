package com.example.cleanplanet.dataBase;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class JsonReader {
    private static String readAll(final BufferedReader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject read(final String url) throws IOException, JSONException, ExecutionException, InterruptedException {
        return new Connector().execute(url).get();
    }

    static class Connector extends AsyncTask<String, JSONObject, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... url){
            InputStream is = null;
            try {
                is = new URL(url[0]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = null;
                try {
                    jsonText = readAll(rd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
