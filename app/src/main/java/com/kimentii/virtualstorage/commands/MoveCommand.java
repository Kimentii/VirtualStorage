package com.kimentii.virtualstorage.commands;

import android.content.Intent;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class MoveCommand extends Command {

    private int mFromX = -1;
    private int mFromY = -1;
    private int mToX = -1;
    private int mToY = -1;

    public MoveCommand() {
        super(1);
    }

    @Override
    public boolean prepareCommandAndUpdateRobot(Robot robot, Map map) {
        mFromX = -1;
        mFromY = -1;
        mToX = -1;
        mToY = -1;
        if (robot.getLocationX() == -1 && robot.getLocationY() == -1) {
            int startX = map.getStartX();
            int startY = map.getStartY();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (map.isFreeCell(startX + i, startY + j)) {
                        mToX = startX + i;
                        mToY = startY + j;
                        robot.setNewLocation(mToX, mToY);
                        return true;
                    }
                }
            }
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (map.isFreeCell(robot.getLocationX() + i, robot.getLocationY() + j)) {
                        mFromX = robot.getLocationX();
                        mFromY = robot.getLocationY();
                        mToX = robot.getLocationX() + i;
                        mToY = robot.getLocationY() + j;
                        robot.setNewLocation(mToX, mToY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateMap(Map map) {
        if (mFromX != -1) {
            map.setSymbolAt(mFromX, mFromY, GlobalConfigurations.SYMBOL_FREE_SPACE);
        }
        map.setSymbolAt(mToX, mToY, GlobalConfigurations.SYMBOL_ROBOT);
    }
}
