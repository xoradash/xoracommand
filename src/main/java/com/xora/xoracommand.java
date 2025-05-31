package com.xora;

import com.xora.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class xoracommand implements ModInitializer {
    public static final String MOD_ID = "xoracommand";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Инициализация XoraCommand мода...");
        
        // Инициализируем конфигурацию
        ConfigManager.initializeConfig();
        
        LOGGER.info("XoraCommand мод успешно загружен!");
    }
}
