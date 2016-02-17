package com.kc.utilites; // 17 Feb, 11:28 AM

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteDatabaseConnecter {

    String            url;
    String            method;
    HttpURLConnection huc;
    public String rawData;

    public RemoteDatabaseConnecter(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public RemoteDatabaseConnecter connect(String requestBody, boolean debug) throws IOException {

        // todo check for internet connection
        // todo setup connection timeout

        huc = (HttpURLConnection) new URL(url).openConnection();
        huc.setRequestMethod(method);

        huc.setDoInput(true);
        if (method.equals("POST")) huc.setDoInput(true);

        if(requestBody != null){

            huc.setFixedLengthStreamingMode(requestBody.getBytes().length);

            OutputStream out = huc.getOutputStream();
            out.write(requestBody.getBytes());
            out.close();

        }

        huc.connect();

        if(huc.getResponseCode() == HttpURLConnection.HTTP_OK){

            StringBuilder sb = new StringBuilder();
            InputStream is = huc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String data;
            while((data = br.readLine()) != null){
                sb.append(data);
            }
            is.close();
            br.close();

            rawData = sb.toString();

        } else {
            debug = true;
        }

        if(debug){
            Log.d("kc", getServerResponse());
        }

        huc.disconnect();

        return this;

    }

//    public void newRequest(String method, String URL) {
//        this.url = URL;
//        huc = null;
//        this.method = method;
//        rawData = null;
//    }

    public String getRawData(){
        return rawData;
    }

    public String getServerResponse() throws IOException {
        return "code: " + huc.getResponseCode() + ", message: " + huc.getResponseMessage();
    }

    @Override
    public String toString() {
        try {
            return getServerResponse() +"\nData from server:\n"+ rawData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
