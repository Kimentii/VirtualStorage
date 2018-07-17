package com.kimentii.virtualstorage;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GlobalConfigurations {
    private static final String TAG = GlobalConfigurations.class.getSimpleName();
    private static final String sConfigurationFile = "configuration.json";

    private static GlobalConfigurations sGlobalConfigurations;

    private char mMap[][];

    private GlobalConfigurations(Context context) {
        Log.d(TAG, "GlobalConfigurations: creating");
        AssetManager am = context.getAssets();
        InputStream inputStream = null;
        StringBuilder totalStr = new StringBuilder();
        try {
            inputStream = am.open(sConfigurationFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = r.readLine()) != null) {
                totalStr.append(line).append('\n');
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonMap jsonMap = gson.fromJson(totalStr.toString(), JsonMap.class);
        mMap = new char[jsonMap.getMap().size()][];
        for (int i = 0; i < mMap.length; i++) {
            mMap[i] = jsonMap.getMap().get(i).toCharArray();
        }
//        for (int i = 0; i < mMap.length; i++) {
//            for (int j = 0; j < mMap[i].length; j++) {
//                Log.d(TAG, "GlobalConfigurations: " + mMap[i][j]);
//            }
//        }
    }

    public static GlobalConfigurations getInstance(Context context) {
        if (sGlobalConfigurations == null) {
            sGlobalConfigurations = new GlobalConfigurations(context);
        }
        return sGlobalConfigurations;
    }

    public char[][] getMap() {
        return mMap;
    }

    private class JsonMap {
        private ArrayList<String> map;

        public ArrayList<String> getMap() {
            return map;
        }
    }
}
