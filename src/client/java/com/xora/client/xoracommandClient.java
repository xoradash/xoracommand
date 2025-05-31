package com.xora.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class xoracommandClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Регистрируем клиентскую команду
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            XoraCommand.register(dispatcher);
        });
        
        // Добавляем shutdown hook для корректного завершения executor'а
        Runtime.getRuntime().addShutdownHook(new Thread(CommandExecutor::shutdown));
    }
}
