package com.xora.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class CommandExecutor {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int DELAY_MS = 500;

    public static void executeCommands(List<String> commands) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null) {
            return;
        }

        if (commands.isEmpty()) {
            client.player.sendMessage(Text.literal("§cНет команд для выполнения!"), false);
            return;
        }

        client.player.sendMessage(Text.literal("§aНачинаю выполнение " + commands.size() + " команд..."), false);

        for (int i = 0; i < commands.size(); i++) {
            final String command = commands.get(i);
            final int commandIndex = i + 1;
            
            scheduler.schedule(() -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null && mc.getNetworkHandler() != null) {
                    // Отправляем команду через чат
                    String cmd = command.startsWith("/") ? command.substring(1) : command;
                    mc.getNetworkHandler().sendChatCommand(cmd);
                    
                    // Уведомляем игрока о выполнении команды
                    mc.player.sendMessage(Text.literal("§7[" + commandIndex + "] §f" + command), false);
                }
            }, (long) i * DELAY_MS, TimeUnit.MILLISECONDS);
        }

        // Уведомление о завершении
        scheduler.schedule(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("§aВсе команды выполнены!"), false);
            }
        }, (long) commands.size() * DELAY_MS, TimeUnit.MILLISECONDS);
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}