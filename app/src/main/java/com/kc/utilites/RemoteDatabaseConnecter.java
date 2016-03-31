package com.kc.utilites; // 17 Feb, 11:28 AM

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class RemoteDatabaseConnecter {

    public String rawData;
    String            url;
    String            method;
    HttpURLConnection huc;
    private String TAG = this.getClass().getSimpleName();

    public RemoteDatabaseConnecter(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public RemoteDatabaseConnecter connect(String requestBody) {

        // todo check for internet connection
        // todo setup connection timeout

        try {
            huc = (HttpURLConnection) new URL(url).openConnection();
            huc.setReadTimeout(15000);
            huc.setConnectTimeout(15000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            huc.setRequestMethod(method);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        huc.setDoInput(true);
        if (method.equals("POST")) {
            huc.setDoOutput(true);
        }

        if (requestBody != null) {

            huc.setFixedLengthStreamingMode(requestBody.getBytes().length);

            OutputStream out = null;
            try {
                out = huc.getOutputStream();
                out.write(requestBody.getBytes());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            huc.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) {

                StringBuilder sb = new StringBuilder();
                InputStream is = huc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String data;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }
                is.close();
                br.close();

                rawData = sb.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.i(TAG, getServerResponse() + "\n");
        Log.v(TAG, "server data:\n" + rawData + "\n");

        huc.disconnect();

        return this;

    }

    public String getRawData() {
        return rawData;
    }

    public String getServerResponse() {
        try {
            return "Response for " + url + "\ncode: " + huc.getResponseCode() + ", message: " + huc.getResponseMessage();
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
    }

    public JSONObject getJSONObject(String dataSource, String dataName, int idx) throws JSONException {
        JSONArray ja = new JSONObject(dataSource).getJSONArray(dataName);
        return ja.getJSONObject(idx);
    }

    public JSONObject getJSONObject(String dataName) throws JSONException {
        return getJSONObject(rawData, dataName, 0);
    }

    public JSONObject getJSONObject(String dataName, int idx) throws JSONException {
        return getJSONObject(rawData, dataName, idx);
    }

    public JSONObject getJSONObject() {
        try {
            return new JSONObject(rawData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return getServerResponse() + "\nData from server:\n" + rawData;
    }
}
