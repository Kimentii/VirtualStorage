package com.kimentii.virtualstorage.commands;

import android.util.Log;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class BornCommand extends Command {
    private static final String TAG = BornCommand.class.getSimpleName();
    private static final int COMMAND_PRIORITY = 12;

    private int mBurnX;
    private int mBurnY;

    public BornCommand() {
        super(COMMAND_PRIORITY);
    }

    @Override
    public void init() {
        Log.d(TAG, "init: ");
    }

    @Override
    public boolean prepareCommandAndUpdateRobot(Robot robot, Map map) {
        int startX = map.getStartX();
        int startY = map.getStartY();
        for (int k = 1; k < 10; k++) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -k; j <= k; j++) {
                    if (map.isFreeCell(startX + i, startY + j)) {
                        mBurnX = startX + i;
                        mBurnY = startY + j;
                        robot.setNewLocation(mBurnX, mBurnY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateMap(Map map) {
        if (mBurnX != -1) {
            map.setSymbolAt(mBurnX, mBurnY, GlobalConfigurations.SYMBOL_ROBOT);
        }
    }
}