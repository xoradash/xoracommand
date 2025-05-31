package com.xora.config;

public class CommandResult {
    private final String command;
    private final boolean shouldExecute;
    
    public CommandResult(String command, boolean shouldExecute) {
        this.command = command;
        this.shouldExecute = shouldExecute;
    }
    
    public String getCommand() {
        return command;
    }
    
    public boolean shouldExecute() {
        return shouldExecute;
    }
}