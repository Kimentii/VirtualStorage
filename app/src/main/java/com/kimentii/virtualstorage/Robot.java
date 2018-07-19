package com.kimentii.virtualstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.kimentii.virtualstorage.commands.Command;

import java.util.ArrayList;

public class Robot {
    private static final String TAG = Robot.class.getSimpleName();
    public static final String ROBOTS_COMMANDS_FILTER = "com.kimentii.virtualstorage.BOT_MESSAGE";
    public static final String EXTRA_COMMAND = "com.kimentii.virtualstorage.extras.COMMAND";

    private Map mMap;
    private ArrayList<Command> mAvailableCommands;

    private Context mContext;
    private GlobalConfigurations mGlobalConfigurations;
    private RobotsCommandsReceiver mRobotsCommandsReceiver;
    private int mLocationX = -1;
    private int mLocationY = -1;
    private int mAimX = -1;
    private int mAimY = -1;
    private int mId;

    public Robot(Context context, final Map map, int id, ArrayList<Command> availableCommands) {
        mId = id;
        mMap = map.getCopy();
        mContext = context;
        mAvailableCommands = availableCommands;
        mRobotsCommandsReceiver = new RobotsCommandsReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(mRobotsCommandsReceiver,
                new IntentFilter(ROBOTS_COMMANDS_FILTER));
        mGlobalConfigurations = GlobalConfigurations.getInstance(context);
    }

    public void update() {
        for (int i = 0; i < mAvailableCommands.size(); i++) {
            mAvailableCommands.get(i).init();
            if ((mAvailableCommands.get(i).prepareCommandAndUpdateRobot(Robot.this, mMap))) {
                Intent intent = new Intent();
                intent.setAction(ROBOTS_COMMANDS_FILTER);
                intent.putExtra(EXTRA_COMMAND, mAvailableCommands.get(i));
                LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
                break;
            }
        }
    }

    public void setNewLocation(int x, int y) {
        mLocationX = x;
        mLocationY = y;
    }

    public void setAim(int x, int y) {
        mAimX = x;
        mAimY = y;
    }

    public boolean isNearAim() {
        if (mAimX == -1 || mAimY == -1) {
            return false;
        }
        if ((Math.abs(mAimX - mLocationX) == 0 || Math.abs(mAimX - mLocationX) == 1)
                && (Math.abs(mAimY - mLocationY) == 0 || Math.abs(mAimY - mLocationY) == 1)) {
            return true;
        }
        return false;
    }

    public boolean isReadyToMoveAim() {
        if (mAimX == -1 || mAimY == -1 || !isNearAim()) {
            return false;
        }
        int dX = -(int) Math.signum(mMap.getEndX() - mAimX);
        int dY = -(int) Math.signum(mMap.getEndY() - mAimY);
        if (dX == 0 || dY == 0) {
            if ((mAimX + dX) == mLocationX && (mAimY + dY) == mLocationY) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAim() {
        return mAimX != -1;
    }

    public int getAimX() {
        return mAimX;
    }

    public int getAimY() {
        return mAimY;
    }

    public int getLocationX() {
        return mLocationX;
    }

    public int getLocationY() {
        return mLocationY;
    }

    class RobotsCommandsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Command command = (Command) intent.getExtras().getSerializable(EXTRA_COMMAND);
            command.updateMap(mMap);
        }
    }
}
