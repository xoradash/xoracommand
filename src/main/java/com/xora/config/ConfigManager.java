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
                "/ecoins add %player% %count%",
                "/give %player% dirt %count%"
        );
        Files.write(COMMANDS_FILE, defaultCommands);
    }

    private static void createDefaultPlayersFile() throws IOException {
        List<String> defaultPlayers = List.of(
                "xoradash 10",
                "WPWolker 20"
        );
        Files.write(PLAYERS_FILE, defaultPlayers);
    }

    private static void createDefaultConstsFile() throws IOException {
        List<String> defaultConsts = List.of(
                "%player% %count%"
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
        List<String> commands = readCommands();
        List<String> players = readPlayers();
        List<String> consts = readConsts();
        
        List<String> generatedCommands = new ArrayList<>();
        
        if (consts.isEmpty()) {
            return generatedCommands;
        }
        
        String constLine = consts.get(0); // Берем первую строку констант
        String[] constNames = constLine.split("\\s+");
        
        for (String playerLine : players) {
            String[] playerValues = playerLine.split("\\s+");
            
            for (String command : commands) {
                String processedCommand = command;
                
                // Заменяем конс��анты на значения
                for (int i = 0; i < constNames.length && i < playerValues.length; i++) {
                    processedCommand = processedCommand.replace(constNames[i], playerValues[i]);
                }
                
                generatedCommands.add(processedCommand);
            }
        }
        
        return generatedCommands;
    }
}