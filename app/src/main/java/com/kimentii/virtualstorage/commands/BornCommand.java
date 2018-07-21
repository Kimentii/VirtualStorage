package com.kimentii.virtualstorage.commands;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

import java.util.Locale;

public class BornCommand extends Command {
    private static final String TAG = BornCommand.class.getSimpleName();
    private static final int COMMAND_PRIORITY = 12;

    private Robot mRobot;
    private int mBornX;
    private int mBornY;

    public BornCommand() {
        super(COMMAND_PRIORITY);
    }

    @Override
    public void init(Robot robot) {
        mBornX = -1;
        mBornY = -1;
        mRobot = robot;

        Map map = mRobot.getMap();
        int startX = map.getStartX();
        int startY = map.getStartY();
        for (int k = 1; k < 10; k++) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -k; j <= k; j++) {
                    if (map.isFreeCell(startX + i, startY + j)) {
                        mBornX = startX + i;
                        mBornY = startY + j;
                        robot.setNewLocation(mBornX, mBornY);
                        return;
                    }
                }
            }
        }
    }


    @Override
    public boolean hasSomethingToChange() {
        return mBornX != -1;
    }

    @Override
    public void updateMap(Map map) {
        if (mBornX != -1) {
            map.setSymbolAt(mBornX, mBornY, GlobalConfigurations.SYMBOL_ROBOT);
        }
    }

    @Override
    public String toString() {
        String str = null;
        if (mRobot != null) {
            str = String.format("R%d:born at (%d,%d)", mRobot.getId(), mBornX, mBornY);
        }
        return str;
    }
}
