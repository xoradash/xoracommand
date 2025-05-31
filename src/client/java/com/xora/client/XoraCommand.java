package com.xora.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.xora.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class XoraCommand {
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("xoracommand")
                .executes(XoraCommand::execute));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        try {
            // Генерируем команды из конфигурации
            List<String> commands = ConfigManager.generateCommands();
            
            if (commands.isEmpty()) {
                context.getSource().sendFeedback(Text.literal("§cНе удалось сгенерировать команды. Проверьте конфигурационные файлы!"));
                return 0;
            }

            // Показываем превью команд
            context.getSource().sendFeedback(Text.literal("§eБудут выполнены следующие команды:"));
            for (int i = 0; i < commands.size(); i++) {
                context.getSource().sendFeedback(Text.literal("§7" + (i + 1) + ". §f" + commands.get(i)));
            }
            
            // Выполняем команды
            CommandExecutor.executeCommands(commands);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cОшибка при выполнении команд: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}