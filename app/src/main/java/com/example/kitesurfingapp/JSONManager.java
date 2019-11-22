package com.example.kitesurfingapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Managerul nostru JSON se va ocupa de parsarea unui String intr-un obiect/array
//json si apoi intr-un obiect sau o lista de tip KitePlace
public class JSONManager {

    public List<KitePlace> JSONArrayToObjArray (String payload) throws JSONException {

        JSONObject jsonResp = new JSONObject(payload);
        JSONArray jsonArr = jsonResp.getJSONArray("result");
        List<KitePlace> places = new ArrayList<>();

        for(int i = 0; i < jsonArr.length(); i++) {

            JSONObject jsonObj = jsonArr.getJSONObject(i);
            String id = jsonObj.getString("id");
            String name = jsonObj.getString("name");
            String country = jsonObj.getString("country");
            String whenToGo = jsonObj.getString("whenToGo");
            boolean isFavorite = jsonObj.getBoolean("isFavorite");
            places.add(new KitePlace(id, name, country, whenToGo, isFavorite));
        }
        return places;
    }

    public KitePlace JSONObjToObj(String payload) throws JSONException {

        JSONObject jsonResp = new JSONObject(payload);
        JSONObject jsonObj = jsonResp.getJSONObject("result");

        String id = jsonObj.getString("id");
        String name = jsonObj.getString("name");
        double latitute = jsonObj.getDouble("latitude");
        double longitude = jsonObj.getDouble("longitude");
        double windProbability = jsonObj.getDouble("windProbability");
        String country = jsonObj.getString("country");
        String whenToGo = jsonObj.getString("whenToGo");
        boolean isFavorite = jsonObj.getBoolean("isFavorite");

        return new KitePlace(id, name, latitute, longitude, windProbability,
                country, whenToGo, isFavorite);
    }
}
