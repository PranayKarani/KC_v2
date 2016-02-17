package com.kc.utilites;

//import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import static org.junit.Assert.assertEquals;

public class RemoteDatabaseConnecterTest {
    
//    @Test
//    public void testConnect() throws Exception {
//
//        String gcm_id = "d4Fu1ZIr2R8:APA91bFLBd2z7fH9azqq7DcO_GEa9F6Yy1J3aeRJsVeufCBuTcRSB7ZJRNsEG-fKClmbe8Px9WfVCaX-F1BrGOt7yF0M5x25p-RiZFT7E3rvijTjyoUpvVG5jp9Ix7kvD26TDXqHxkEJ";
//        String url = "http://localhost:80/web/kc/gcm_id_recheck.php?student_id=123&gcm_id=" + gcm_id;
//        String method = "GET";
//        String requestBody = null;
//        String rawData;
//
//        HttpURLConnection huc = (HttpURLConnection) new URL(url).openConnection();
//        huc.setRequestMethod(method);
//
//        huc.setDoInput(true);
//        if (method.equals("POST")) {
//            huc.setDoInput(true);
//        }
//
//        if (requestBody != null) {
//
//            huc.setFixedLengthStreamingMode(requestBody.getBytes().length);
//
//            OutputStream out = huc.getOutputStream();
//            out.write(requestBody.getBytes());
//            out.close();
//
//        }
//
//        huc.connect();
//
//
//        StringBuilder sb = new StringBuilder();
//        InputStream is = huc.getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        String data;
//        while ((data = br.readLine()) != null) {
//            sb.append(data).append("\n");
//        }
//        is.close();
//        br.close();
//
//        rawData = sb.toString();
//
//
//        assertEquals(HttpURLConnection.HTTP_OK, huc.getResponseCode());
//        assertEquals(rawData, "name_1");
//
//
//        huc.disconnect();
//
//
//    }
}
