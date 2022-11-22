package dj.arbuz;

import java.nio.file.Path;

/**
 * Класс путей до файлов с конфигурацией
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConfigPaths {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private ConfigPaths() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Поле пути до файла с конфигурацией телеграма
     */
    public static final Path TELEGRAM_CONFIG_PATH = Path.of("src", "main", "resources", "config", "telegram.cfg.json");
    /**
     * Поле пути до файла с конфигурацией vk
     */
    public static final Path VK_CONFIG_PATH = Path.of("src", "main", "resources", "config", "vk.cfg.json");
    /**
     * Поле пути до файла с конфигурацией hibernate
     */
    public static final String HIBERNATE_CONFIG_PATH_AS_STRING = "src/main/resources/config/hibernate.cfg.xml";
}
