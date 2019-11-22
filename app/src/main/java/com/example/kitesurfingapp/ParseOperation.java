package com.example.kitesurfingapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParseOperation extends AsyncTask<String, String, String> {

    private boolean tokenAvailable;
    public static String token;
    public static String jsonCountries;

    public ParseOperation() {

        this.tokenAvailable = false;
    }

    @Override
    //Executam requestul pe un thread diferit fata de cel al UI ului
    protected String doInBackground(String... params) {

        tokenAvailable = token != null;
        try {
            String response = makePostRequest(params[0], params[1], tokenAvailable);
            try {
                if(!tokenAvailable) {

                    JSONObject jsonResp = new JSONObject(response);
                    JSONObject jsonRes = jsonResp.getJSONObject("result");
                    token = jsonRes.getString("token");
                }
                else {

                    JSONObject jsonResp = new JSONObject(response);
                    jsonCountries = jsonResp.toString();
                    return jsonCountries;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Success";
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    //Executam un request de tip POST la baza de data, adaugand in
    //headerul acestuia tokenul de acces daca, conexiunea a fost
    //stabilita
    private String makePostRequest(String stringUrl, String payload,
                                   boolean tokenAvailable) throws IOException {

        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json");
        if(tokenAvailable) uc.setRequestProperty("token", token);
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }
}
