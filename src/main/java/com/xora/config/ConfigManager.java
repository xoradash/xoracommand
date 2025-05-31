package com.xora.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("xoracommand");
    private static final Path COMMANDS_FILE = CONFIG_DIR.resolve("commands.txt");
    private static final Path PLAYERS_FILE = CONFIG_DIR.resolve("players.txt");
    private static final Path CONSTS_FILE = CONFIG_DIR.resolve("consts.txt");

    public static void initializeConfig() {
        try {
            // Создаем папку конфига если её нет
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }

            // Создаем файлы конфигурации с примерами если их нет
            if (!Files.exists(COMMANDS_FILE)) {
                createDefaultCommandsFile();
            }
            if (!Files.exists(PLAYERS_FILE)) {
                createDefaultPlayersFile();
            }
            if (!Files.exists(CONSTS_FILE)) {
                createDefaultConstsFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultCommandsFile() throws IOException {
        List<String> defaultCommands = List.of(
                "/give %player% %1% %2%",
                "/msg %player% Вы получили %1% x%2%!",
                "/ecoins add %player% %3%"
        );
        Files.write(COMMANDS_FILE, defaultCommands);
    }

    private static void createDefaultPlayersFile() throws IOException {
        List<String> defaultPlayers = List.of(
                "xoradash diamond 10 100",
                "WPWolker iron_ingot %% 50"
        );
        Files.write(PLAYERS_FILE, defaultPlayers);
    }

    private static void createDefaultConstsFile() throws IOException {
        List<String> defaultConsts = List.of(
                "%player% %1% %2% %3%"
        );
        Files.write(CONSTS_FILE, defaultConsts);
    }

    public static List<String> readCommands() {
        return readFileLines(COMMANDS_FILE);
    }

    public static List<String> readPlayers() {
        return readFileLines(PLAYERS_FILE);
    }

    public static List<String> readConsts() {
        return readFileLines(CONSTS_FILE);
    }

    private static List<String> readFileLines(Path filePath) {
        try {
            if (Files.exists(filePath)) {
                return Files.readAllLines(filePath).stream()
                        .filter(line -> !line.trim().isEmpty())
                        .toList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String> generateCommands() {
        List<CommandResult> commandResults = generateCommandResults();
        List<String> generatedCommands = new ArrayList<>();
        
        for (CommandResult result : commandResults) {
            if (result.shouldExecute()) {
                generatedCommands.add(result.getCommand());
            }
        }
        
        return generatedCommands;
    }
    
    public static List<CommandResult> generateCommandResults() {
        List<String> commands = readCommands();
        List<String> players = readPlayers();
        List<String> consts = readConsts();
        
        List<CommandResult> commandResults = new ArrayList<>();
        
        if (consts.isEmpty()) {
            return commandResults;
        }
        
        String constLine = consts.get(0); // Берем первую строку констант
        String[] constNames = constLine.split("\\s+");
        
        for (String playerLine : players) {
            String[] playerValues = playerLine.split("\\s+");
            
            for (String command : commands) {
                CommandResult result = processCommandWithResult(command, constNames, playerValues);
                commandResults.add(result);
            }
        }
        
        return commandResults;
    }
    
    private static CommandResult processCommandWithResult(String command, String[] constNames, String[] playerValues) {
        String processedCommand = command;
        boolean shouldExecute = true;
        
        // Проходим по всем константам и заменяем их соответствующими значениями
        for (int i = 0; i < constNames.length; i++) {
            String constName = constNames[i];
            
            // Проверяем, используется ли эта константа в команде
            if (processedCommand.contains(constName)) {
                // Проверяем, есть ли значение на этой позиции
                if (i < playerValues.length) {
                    String value = playerValues[i];
                    
                    // Если значение %%, то команда не выполняется, но показываем её
                    if (value.equals("%%")) {
                        shouldExecute = false;
                        processedCommand = processedCommand.replace(constName, "%%");
                    } else {
                        // Заменяем константу на значение
                        processedCommand = processedCommand.replace(constName, value);
                    }
                } else {
                    // Нет значения для этой константы
                    shouldExecute = false;
                    processedCommand = processedCommand.replace(constName, "???");
                }
            }
        }
        
        return new CommandResult(processedCommand, shouldExecute);
    }
    
    private static String processCommand(String command, String[] constNames, String[] playerValues) {
        String processedCommand = command;
        
        // Проходим по всем константам и заменяем их соответствующими значениями
        for (int i = 0; i < constNames.length; i++) {
            String constName = constNames[i];
            
            // Проверяем, используется ��и эта константа в команде
            if (processedCommand.contains(constName)) {
                // Проверяем, есть ли значение на этой позиции
                if (i < playerValues.length) {
                    String value = playerValues[i];
                    
                    // Если значение %%, то команда не выполняется
                    if (value.equals("%%")) {
                        return null;
                    }
                    
                    // Заменяем константу на значение
                    processedCommand = processedCommand.replace(constName, value);
                } else {
                    // Нет значения для этой константы
                    return null;
                }
            }
        }
        
        return processedCommand;
    }
}