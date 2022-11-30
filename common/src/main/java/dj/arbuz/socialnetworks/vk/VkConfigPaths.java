package dj.arbuz.socialnetworks.vk;

import java.nio.file.Path;

/**
 * Класс путей до файлов с конфигурацией
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkConfigPaths {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private VkConfigPaths() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Поле пути до файла с конфигурацией vk
     */
    public static final Path VK_CONFIG_PATH = Path.of("common", "src", "main", "resources", "configs", "vk.cfg.json");
}
