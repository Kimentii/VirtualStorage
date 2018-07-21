package com.kimentii.virtualstorage;

import com.kimentii.virtualstorage.commands.Command;
import com.kimentii.virtualstorage.commands.MoveBoxCommand;
import com.kimentii.virtualstorage.commands.MoveCommand;
import com.kimentii.virtualstorage.commands.ReserveBoxCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CommandsFactory {

    public static ArrayList<Command> getAllCommands() {
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(new MoveCommand());
        commands.add(new ReserveBoxCommand());
        commands.add(new MoveBoxCommand());
        Collections.sort(commands, new Comparator<Command>() {
            @Override
            public int compare(Command command, Command command2) {
                return command2.getPriority() - command.getPriority();
            }
        });
        return commands;
    }


}
