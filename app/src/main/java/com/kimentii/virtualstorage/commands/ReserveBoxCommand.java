package com.kimentii.virtualstorage.commands;

import android.util.Log;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class ReserveBoxCommand extends Command {
    private static final String TAG = ReserveBoxCommand.class.getSimpleName();
    private static final int COMMAND_PRIORITY = 10;

    private int mAimX = -1;
    private int mAimY = -1;

    public ReserveBoxCommand() {
        super(COMMAND_PRIORITY);
    }

    @Override
    public void init() {
        mAimX = -1;
        mAimY = -1;
    }

    @Override
    public boolean prepareCommandAndUpdateRobot(Robot robot, Map map) {
        if (robot.hasAim()) {
            return false;
        }
        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                if (map.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_FREE_BOX) {
                    robot.setAim(x, y);
                    mAimX = x;
                    mAimY = y;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateMap(Map map) {
        Log.d(TAG, "updateMap: " + mAimX + " " + mAimY);
        map.setSymbolAt(mAimX, mAimY, GlobalConfigurations.SYMBOL_RESERVED_BOX);
    }
}
