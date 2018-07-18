package com.kimentii.virtualstorage.commands;

import android.content.Intent;

import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class MoveCommand extends Command {
    private static final int COMMAND_PRIORITY = 1;

    private int mFromX = -1;
    private int mFromY = -1;
    private int mToX = -1;
    private int mToY = -1;

    public MoveCommand() {
        super(1);
    }

    @Override
    public void init() {
        mFromX = -1;
        mFromY = -1;
        mToX = -1;
        mToY = -1;
    }

    @Override
    public boolean prepareCommandAndUpdateRobot(Robot robot, Map map) {
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
            int aimX = robot.getAimX();
            int aimY = robot.getAimY();
            int dX = aimX - robot.getLocationX();
            int dY = aimY - robot.getLocationY();

            if (map.isFreeCell(robot.getLocationX() + (int) Math.signum(dX),
                    robot.getLocationY() + (int) Math.signum(dY))) {
                mToX = robot.getLocationX() + (int) Math.signum(dX);
                mToY = robot.getLocationY() + (int) Math.signum(dY);
            } else if (map.isFreeCell(robot.getLocationX(),
                    robot.getLocationY() + (int) Math.signum(dY))) {
                mToX = robot.getLocationX();
                mToY = robot.getLocationY() + (int) Math.signum(dY);
            } else if (map.isFreeCell(robot.getLocationX() + (int) Math.signum(dX),
                    robot.getLocationY())) {
                mToX = robot.getLocationX() + (int) Math.signum(dX);
                mToY = robot.getLocationY();
            }
            if (mToX != -1) {
                mFromX = robot.getLocationX();
                mFromY = robot.getLocationY();
                robot.setNewLocation(mToX, mToY);
                return true;
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
