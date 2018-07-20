package com.kimentii.virtualstorage.commands;

import android.util.Log;

import com.kimentii.virtualstorage.Cell;
import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class MoveCommand extends Command {
    public static final String TAG = MoveCommand.class.getSimpleName();
    private static final int COMMAND_PRIORITY = 1;

    private int mFromX = -1;
    private int mFromY = -1;
    private int mToX = -1;
    private int mToY = -1;

    public MoveCommand() {
        super(COMMAND_PRIORITY);
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
//        Log.d(TAG, ": " + robot.getId());
        if (robot.isNearAim()) {
//            Log.d(TAG, "nearBy: " + robot.getId());
            Cell aimNextPosition = robot.getAimNextPosition();
            if (aimNextPosition != null) {
//                Log.d(TAG, "aimNextPosition: " + robot.getId());
                Cell robotShouldStayCell = robot.getRobotShouldStayPosition();
                int robotDX = (int) Math.signum(robotShouldStayCell.getX() - robot.getLocationX());
                int robotDY = (int) Math.signum(robotShouldStayCell.getY() - robot.getLocationY());
//                Log.d(TAG, "prepareCommandAndUpdateRobot: " + robot.getId() + ": (" + robotDX + "," + robotDY + ")");
                if (robotDX == 0) {
                    if (map.isFreeCell(robot.getLocationX() + 1, robot.getLocationY() + robotDY)) {
                        robotDX = 1;
                    } else {
                        robotDX = -1;
                    }
                } else if (robotDY == 0) {
                    if (map.isFreeCell(robot.getLocationX() + robotDX, robot.getLocationY() + 1)) {
//                        Log.d(TAG, "prepareCommandAndUpdateRobot: set DY to 1");
                        robotDY = 1;
                    } else {
//                        Log.d(TAG, "prepareCommandAndUpdateRobot: set DY to -1");
                        robotDY = -1;
                    }
                }
                if (map.isFreeCell(robot.getLocationX() + robotDX, robot.getLocationY() + robotDY)) {
                    mToX = robot.getLocationX() + robotDX;
                    mToY = robot.getLocationY() + robotDY;
                } else if (map.isFreeCell(robot.getLocationX(), robot.getLocationY() + robotDY)) {
                    mToX = robot.getLocationX();
                    mToY = robot.getLocationY() + robotDY;
                } else if (map.isFreeCell(robot.getLocationX() + robotDX, robot.getLocationY())) {
                    mToX = robot.getLocationX() + robotDX;
                    mToY = robot.getLocationY();
                }
                if (mToX != -1) {
                    mFromX = robot.getLocationX();
                    mFromY = robot.getLocationY();
                    robot.setNewLocation(mToX, mToY);
                    return true;
                }
            }
        }
        if (robot.hasAim()) {
            if (robot.getAimNextPosition() != null) {
                Cell robotShouldStayCell = robot.getRobotShouldStayPosition();
                int robotDX = (int) Math.signum(robotShouldStayCell.getX() - robot.getLocationX());
                int robotDY = (int) Math.signum(robotShouldStayCell.getY() - robot.getLocationY());
                if (map.isFreeCell(robot.getLocationX() + robotDX,
                        robot.getLocationY() + robotDY)) {
                    mToX = robot.getLocationX() + robotDX;
                    mToY = robot.getLocationY() + robotDY;
                } else if (map.isFreeCell(robot.getLocationX(), robot.getLocationY() + robotDY)) {
                    mToX = robot.getLocationX();
                    mToY = robot.getLocationY() + robotDY;
                } else if (map.isFreeCell(robot.getLocationX() + robotDX, robot.getLocationY())) {
                    mToX = robot.getLocationX() + robotDX;
                    mToY = robot.getLocationY();
                }
                if (mToX != -1) {
                    mFromX = robot.getLocationX();
                    mFromY = robot.getLocationY();
                    robot.setNewLocation(mToX, mToY);
                    return true;
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
        Log.d(TAG, "updateMap: move to(" + mToX + "," + mToY + ")");
        map.setSymbolAt(mToX, mToY, GlobalConfigurations.SYMBOL_ROBOT);
    }
}
