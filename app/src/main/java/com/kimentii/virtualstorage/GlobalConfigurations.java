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
    public static final char SYMBOL_FREE_BOX = 'B';
    public static final char SYMBOL_RESERVED_BOX = 'b';
    public static final char SYMBOL_FREE_SPACE = ' ';
    public static final char SYMBOL_START = 'S';
    public static final char SYMBOL_END = 'E';
    public static final char SYMBOL_ROBOT = 'R';
    public static final char SYMBOL_BARRIER = '-';

    private static GlobalConfigurations sGlobalConfigurations;

    private Map mMap;

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
        char[][] map = new char[jsonMap.getMap().size()][];
        for (int i = 0; i < map.length; i++) {
            map[i] = jsonMap.getMap().get(i).toCharArray();
        }
        mMap = new Map(map);

        Log.d(TAG, "GlobalConfigurations: map width= " + mMap.getMapWidth() + " height= " + mMap.getMapHeight());
        Log.d(TAG, "GlobalConfigurations: start x=" + mMap.getStartX() + " y=" + mMap.getStartY());
        Log.d(TAG, "GlobalConfigurations: end x=" + mMap.getEndX() + " y=" + mMap.getStartY());
    }

    public static GlobalConfigurations getInstance(Context context) {
        if (sGlobalConfigurations == null) {
            sGlobalConfigurations = new GlobalConfigurations(context);
        }
        return sGlobalConfigurations;
    }

    public Map getMap() {
        return mMap;
    }

    private class JsonMap {
        private ArrayList<String> map;

        public ArrayList<String> getMap() {
            return map;
        }
    }
}
