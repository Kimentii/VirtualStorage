package com.kimentii.virtualstorage.commands;

import com.kimentii.virtualstorage.Cell;
import com.kimentii.virtualstorage.GlobalConfigurations;
import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

public class MoveBoxCommand extends Command {
    private static final int COMMAND_PRIORITY = 9;

    private int mFromX = -1;
    private int mFromY = -1;
    private int mToX = -1;
    private int mToY = -1;
    private int mNewBoxX = -1;
    private int mNewBoxY = -1;

    public MoveBoxCommand() {
        super(COMMAND_PRIORITY);
    }

    @Override
    public void init() {
        mFromX = -1;
        mFromY = -1;
        mToX = -1;
        mToY = -1;
        mNewBoxX = -1;
        mNewBoxY = -1;
    }

    @Override
    public boolean prepareCommandAndUpdateRobot(Robot robot, Map map) {
        if (robot.isReadyToMoveAim()) {
            Cell aimNextPosition = robot.getAimNextPosition();
            if (aimNextPosition != null) {
                if (map.isFreeCell(aimNextPosition.getX(), aimNextPosition.getY())) {
                    mFromX = robot.getLocationX();
                    mFromY = robot.getLocationY();
                    mToX = robot.getAim().getX();
                    mToY = robot.getAim().getY();
                    mNewBoxX = aimNextPosition.getX();
                    mNewBoxY = aimNextPosition.getY();
                    robot.setAim(mNewBoxX, mNewBoxY);
                    robot.setNewLocation(mToX, mToY);
                    return true;
                } else if (map.isEndCell(aimNextPosition.getX(), aimNextPosition.getY())) {
                    mFromX = robot.getLocationX();
                    mFromY = robot.getLocationY();
                    mToX = robot.getAim().getX();
                    mToY = robot.getAim().getY();
                    robot.clearAim();
                    robot.setNewLocation(mToX, mToY);
                    return true;
                }
            }
            /*int dX = (int) Math.signum(map.getEndX() - robot.getAim().getX());
            int dY = (int) Math.signum(map.getEndY() - robot.getAim().getY());
            if (map.isFreeCell(robot.getAim().getX() + dX, robot.getAim().getY() + dY)) {
                mFromX = robot.getLocationX();
                mFromY = robot.getLocationY();
                mToX = robot.getAim().getX();
                mToY = robot.getAim().getY();
                mNewBoxX = robot.getAim().getX() + dX;
                mNewBoxY = robot.getAim().getY() + dY;
                robot.setAim(mNewBoxX, mNewBoxY);
                robot.setNewLocation(mToX, mToY);
                return true;
            } else if (map.getSymbolAt(robot.getAim().getX() + dX, robot.getAim().getY() + dY)
                    == GlobalConfigurations.SYMBOL_END) {
                mFromX = robot.getLocationX();
                mFromY = robot.getLocationY();
                mToX = robot.getAim().getX();
                mToY = robot.getAim().getY();
                robot.clearAim();
                robot.setNewLocation(mToX, mToY);
                return true;
            }*/
        }
        return false;
    }

    @Override
    public void updateMap(Map map) {
        map.setSymbolAt(mFromX, mFromY, GlobalConfigurations.SYMBOL_FREE_SPACE);
        map.setSymbolAt(mToX, mToY, GlobalConfigurations.SYMBOL_ROBOT);
        if (mNewBoxX != -1 && mNewBoxY != -1) {
            map.setSymbolAt(mNewBoxX, mNewBoxY, GlobalConfigurations.SYMBOL_RESERVED_BOX);
        }
    }
}
