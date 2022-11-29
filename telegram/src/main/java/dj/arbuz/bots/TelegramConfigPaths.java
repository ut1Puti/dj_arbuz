package dj.arbuz.bots;

import java.nio.file.Path;

/**
 * Класс путей до файлов с конфигурациями телеграмма
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramConfigPaths {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private TelegramConfigPaths() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Поле пути до файла с конфигурацией телеграма
     */
    public static final Path TELEGRAM_CONFIG_PATH = Path.of("src", "main", "resources", "configs", "telegram.cfg.json");

    /**
     * Поле пути до файла с конфигурацией hibernate
     */
    public static final String HIBERNATE_CONFIG_PATH_AS_STRING = "src/main/resources/configs/hibernate.cfg.xml";
}
