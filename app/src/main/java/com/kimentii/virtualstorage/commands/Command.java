package com.kimentii.virtualstorage.commands;

import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

import java.io.Serializable;

public abstract class Command implements Serializable {

    private int mPriority;

    Command(int priority) {
        mPriority = priority;
    }

    /**
     * @param robot - robot, which will do command
     * @param map   - robot map
     * @return true if command has something to change otherwise return false
     */
    public abstract boolean prepareCommandAndUpdateRobot(Robot robot, Map map);

    public abstract void updateMap(Map map);
}
