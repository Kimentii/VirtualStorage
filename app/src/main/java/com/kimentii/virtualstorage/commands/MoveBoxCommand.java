package com.kimentii.virtualstorage.commands;

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
            int dX = (int) Math.signum(map.getEndX() - robot.getAimX());
            int dY = (int) Math.signum(map.getEndY() - robot.getAimY());
            if (map.isFreeCell(robot.getAimX() + dX, robot.getAimY() + dY)) {
                mFromX = robot.getLocationX();
                mFromY = robot.getLocationY();
                mToX = robot.getAimX();
                mToY = robot.getAimY();
                mNewBoxX = robot.getAimX() + dX;
                mNewBoxY = robot.getAimY() + dY;
                robot.setAim(mNewBoxX, mNewBoxY);
                robot.setNewLocation(mToX, mToY);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateMap(Map map) {
        map.setSymbolAt(mFromX, mFromY, GlobalConfigurations.SYMBOL_FREE_SPACE);
        map.setSymbolAt(mToX, mToY, GlobalConfigurations.SYMBOL_ROBOT);
        map.setSymbolAt(mNewBoxX, mNewBoxY, GlobalConfigurations.SYMBOL_RESERVED_BOX);
    }
}
