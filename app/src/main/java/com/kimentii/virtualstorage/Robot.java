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
    private Cell mAim = null;
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
        mAim = new Cell(x, y);
    }

    public boolean isNearAim() {
        if (!hasAim()) {
            return false;
        }
        if ((Math.abs(mAim.getX() - mLocationX) == 0 || Math.abs(mAim.getX() - mLocationX) == 1)
                && (Math.abs(mAim.getY() - mLocationY) == 0 || Math.abs(mAim.getY() - mLocationY) == 1)) {
            return true;
        }
        return false;
    }

    public boolean isReadyToMoveAim() {
        if (!hasAim() || !isNearAim()) {
            return false;
        }
        Cell aimNextPosition = getAimNextPosition();
        if (aimNextPosition == null) {
            return false;
        }
        int dX = -(int) Math.signum(aimNextPosition.getX() - mAim.getX());
        int dY = -(int) Math.signum(aimNextPosition.getY() - mAim.getY());
        if ((mAim.getX() + dX) == mLocationX && (mAim.getY() + dY) == mLocationY) {
            return true;
        }
       /* int dX = -(int) Math.signum(mMap.getEndX() - mAim.getX());
        int dY = -(int) Math.signum(mMap.getEndY() - mAim.getY());
        if ((mAim.getX() + dX) == mLocationX && mAim.getY() == mLocationY) {
            return true;
        }
        if (mAim.getX() == mLocationX && (mAim.getY() + dY) == mLocationY) {
            return true;
        }*/
        return false;
    }

    public Cell getAimNextPosition() {
        if (!hasAim()) {
            return null;
        }
        int dX = (int) Math.signum(mMap.getEndX() - mAim.getX());
        int dY = (int) Math.signum(mMap.getEndY() - mAim.getY());
        if (mMap.isFreeCell(mAim.getX() + dX, mAim.getY())
                || mMap.isEndCell(mAim.getX() + dX, mAim.getY())) {
            return new Cell(mAim.getX() + dX, mAim.getY());
        }
        if (mMap.isFreeCell(mAim.getX(), mAim.getY() + dY)
                || mMap.isEndCell(mAim.getX(), mAim.getY() + dY)) {
            return new Cell(mAim.getX(), mAim.getY() + dY);
        }
        return null;
    }

    public boolean hasAim() {
        return mAim != null;
    }

    public void clearAim() {
        mAim = null;
    }

    public Cell getAim() {
        return mAim;
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
