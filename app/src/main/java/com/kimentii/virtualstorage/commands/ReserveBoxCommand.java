package com.kimentii.virtualstorage.commands;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class ReserveBoxCommand extends Command {
    private static final String TAG = ReserveBoxCommand.class.getSimpleName();
    private static final int COMMAND_PRIORITY = 10;

    private int mAimX = -1;
    private int mAimY = -1;
    private Robot mRobot;

    public ReserveBoxCommand() {
        super(COMMAND_PRIORITY);
    }

    @Override
    public void init(Robot robot) {
        mAimX = -1;
        mAimY = -1;
        mRobot = robot;

        Map map = mRobot.getMap();
        if (robot.hasAim()) {
            return;
        }
        if (robot.getLocationX() != -1) {
            for (int y = 0; y < map.getMapHeight(); y++) {
                for (int x = 0; x < map.getMapWidth(); x++) {
                    if (map.getSymbolAt(x, y) == GlobalConfigurations.SYMBOL_FREE_BOX) {
                        robot.setAim(x, y);
                        mAimX = x;
                        mAimY = y;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean hasSomethingToChange() {
        return mAimX != -1;
    }

    @Override
    public void updateMap(Map map) {
        map.setSymbolAt(mAimX, mAimY, GlobalConfigurations.SYMBOL_RESERVED_BOX);
    }

    @Override
    public String toString() {
        String str = null;
        if (mRobot != null) {
            str = String.format("R%d:reserve box (%d,%d)", mRobot.getId(), mAimX, mAimY);
        }
        return str;
    }
}
