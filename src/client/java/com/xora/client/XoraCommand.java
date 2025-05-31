package com.xora.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.xora.config.ConfigManager;
import com.xora.config.CommandResult;
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
            // Генерируем результаты команд из конфигурации
            List<CommandResult> commandResults = ConfigManager.generateCommandResults();
            
            if (commandResults.isEmpty()) {
                context.getSource().sendFeedback(Text.literal("§cНе удалось сгенерировать команды. Проверьте конфигурационные файлы!"));
                return 0;
            }

            // Показываем превью команд
            context.getSource().sendFeedback(Text.literal("§eБудут выполнены следующие команды:"));
            int commandIndex = 1;
            for (CommandResult result : commandResults) {
                if (result.shouldExecute()) {
                    context.getSource().sendFeedback(Text.literal("§7" + commandIndex + ". §a" + result.getCommand()));
                } else {
                    context.getSource().sendFeedback(Text.literal("§7" + commandIndex + ". §c" + result.getCommand() + " §7(пропущена)"));
                }
                commandIndex++;
            }
            
            // Выполняем команды
            CommandExecutor.executeCommandResults(commandResults);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cОшибка при выполнении команд: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}