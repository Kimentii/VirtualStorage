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

    public abstract void init(Robot robot);

    public abstract boolean hasSomethingToChange();

    public abstract void updateMap(Map map);

    @Override
    public abstract String toString();

    public int getPriority() {
        return mPriority;
    }
}
