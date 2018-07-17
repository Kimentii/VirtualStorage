package com.kimentii.virtualstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class Robot {
    private static final String TAG = Robot.class.getSimpleName();
    public static final String ROBOT_MESSAGES_FILTER = "com.kimentii.virtualstorage.BOT_MESSAGE";

    private char[][] mMap;
    private final int mMapHeight;
    private final int mMapWidth;

    private Context mContext;
    private GlobalConfigurations mGlobalConfigurations;
    private int mMyLocationX = -1;
    private int mMyLocationY = -1;
    private int mId;

    public Robot(Context context, final char[][] map, int id) {
        mId = id;
        mMap = new char[map.length][map[0].length];
        mMapHeight = mMap.length;
        mMapWidth = mMap[0].length;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                mMap[i][j] = map[i][j];
            }
        }
        mContext = context;
        BotBroadcastReceiver botBroadcastReceiver = new BotBroadcastReceiver();
        context.registerReceiver(botBroadcastReceiver, new IntentFilter(ROBOT_MESSAGES_FILTER));
        mGlobalConfigurations = GlobalConfigurations.getInstance(context);
    }

    public void update() {
        if (mMyLocationX == -1 && mMyLocationY == -1) {
            int startX = mGlobalConfigurations.getStartX();
            int startY = mGlobalConfigurations.getStartY();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isFreeCell(startX + i, startY + j)) {
                        mMyLocationX = startX + i;
                        mMyLocationY = startY + j;
                        Intent intent = new Intent();
                        intent.setAction(ROBOT_MESSAGES_FILTER);
                        intent.putExtra("x", mMyLocationX);
                        intent.putExtra("y", mMyLocationY);
                        mContext.sendBroadcast(intent);
                        return;
                    }
                }
            }
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isFreeCell(mMyLocationX + i, mMyLocationY + j)) {
                        mMap[mMyLocationY][mMyLocationX] = ' ';
                        mMyLocationX = mMyLocationX + i;
                        mMyLocationY = mMyLocationY + j;
                        Intent intent = new Intent();
                        intent.setAction(ROBOT_MESSAGES_FILTER);
                        intent.putExtra("x", mMyLocationX);
                        intent.putExtra("y", mMyLocationY);
                        mContext.sendBroadcast(intent);
                        return;
                    }
                }
            }
        }
    }

    private boolean isFreeCell(int x, int y) {
        Log.d(TAG, "isFreeCell: x= " + x + " y= " + y);
        if (x >= mMapWidth || y >= mMapHeight
                || x < 0 || y < 0) {
            return false;
        }
        if (mMap[y][x] != GlobalConfigurations.SYMBOL_FREE_SPACE) {
            return false;
        }
        return true;
    }

    class BotBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int x = intent.getExtras().getInt("x");
            int y = intent.getExtras().getInt("y");
            mMap[y][x] = GlobalConfigurations.SYMBOL_ROBOT;
        }
    }
}
