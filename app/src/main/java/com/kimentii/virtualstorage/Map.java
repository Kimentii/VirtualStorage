package com.kimentii.virtualstorage;

import android.util.Log;

import static com.kimentii.virtualstorage.GlobalConfigurations.*;

public class Map {
    private static final String TAG = Map.class.getSimpleName();

    private char[][] mMap;
    private int mMapHeight;
    private int mMapWidth;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    Map(char[][] map) {
        mMap = map;
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
    }

    public boolean isFreeCell(int x, int y) {
        //Log.d(TAG, "isFreeCell: x= " + x + " y= " + y);
        if (x >= mMapWidth || y >= mMapHeight || x < 0 || y < 0) {
            return false;
        }
        if (mMap[y][x] != GlobalConfigurations.SYMBOL_FREE_SPACE) {
            return false;
        }
        return true;
    }

    public boolean isEndCell(int x, int y) {
        if (x >= mMapWidth || y >= mMapHeight || x < 0 || y < 0) {
            return false;
        }
        if (mMap[y][x] != GlobalConfigurations.SYMBOL_END) {
            return false;
        }
        return true;
    }

    public Map getCopy() {
        char[][] map = new char[getMapHeight()][getMapWidth()];
        for (int i = 0; i < getMapHeight(); i++) {
            for (int j = 0; j < getMapWidth(); j++) {
                map[i][j] = mMap[i][j];
            }
        }
        return new Map(map);
    }

    public char getSymbolAt(int x, int y) {
        return mMap[y][x];
    }

    public void setSymbolAt(int x, int y, char symbol) {
        mMap[y][x] = symbol;
    }

    public char[][] getMap() {
        return mMap;
    }

    public int getMapHeight() {
        return mMapHeight;
    }

    public int getMapWidth() {
        return mMapWidth;
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
}
