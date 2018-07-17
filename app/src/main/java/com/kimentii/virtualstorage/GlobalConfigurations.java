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
    public static final char SYMBOL_BOX = 'B';
    public static final char SYMBOL_FREE_SPACE = ' ';
    public static final char SYMBOL_START = 'S';
    public static final char SYMBOL_END = 'E';
    public static final char SYMBOL_ROBOT = 'R';

    private static GlobalConfigurations sGlobalConfigurations;

    private char mMap[][];
    private final int mMapHeight;
    private final int mMapWidth;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

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
        mMapHeight = mMap.length;
        mMapWidth = mMap[0].length;
        for (int i = 0; i < mMapHeight; i++) {
            for (int j = 0; j < mMapWidth; j++) {
                switch (mMap[i][j]) {
                    case SYMBOL_START:
                        mStartX = j;
                        mStartY = i;
                        break;
                    case SYMBOL_END:
                        mEndX = j;
                        mEndY = i;
                        break;
                }
            }
        }
        Log.d(TAG, "GlobalConfigurations: map width= " + mMapWidth + " height= " + mMapHeight);
        Log.d(TAG, "GlobalConfigurations: start x=" + mStartX + " y=" + mStartY);
        Log.d(TAG, "GlobalConfigurations: end x=" + mEndX + " y=" + mEndY);
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

    public int getStartX() {
        return mStartX;
    }

    public int getStartY() {
        return mStartY;
    }

    public int getEndX() {
        return mEndX;
    }

    public int getEndY() {
        return mEndY;
    }

    public int getMapHeight() {
        return mMapHeight;
    }

    public int getMapWidth() {
        return mMapWidth;
    }

    private class JsonMap {
        private ArrayList<String> map;

        public ArrayList<String> getMap() {
            return map;
        }
    }
}
