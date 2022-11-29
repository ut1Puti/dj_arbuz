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
     * Поле пути до файла с конфигурацией vk
     */
    public static final Path VK_CONFIG_PATH = Path.of("common", "src", "main", "resources", "configs", "vk.cfg.json");
}
