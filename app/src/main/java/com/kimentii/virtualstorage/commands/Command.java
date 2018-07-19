package com.kimentii.virtualstorage.commands;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kimentii.virtualstorage.Map;
import com.kimentii.virtualstorage.Robot;

import java.io.Serializable;

import static com.kimentii.virtualstorage.Robot.EXTRA_COMMAND;

public abstract class Command implements Serializable {

    private int mPriority;

    public Command(int priority) {
        mPriority = priority;
    }

    public abstract void init();

    /**
     * @param robot - robot, which will do command
     * @param map   - robot map
     * @return true if command has something to change otherwise return false
     */
    public abstract boolean prepareCommandAndUpdateRobot(Robot robot, Map map);

    public abstract void updateMap(Map map);

    public int getPriority() {
        return mPriority;
    }
}
