package httpserver;

import java.nio.file.Path;

/**
 * Класс пути до дефолтного файла с конфигурацией сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class DefaultServerConfigPath {
    /**
     * Поле пути до файла с дефолтной конфигурацией сервера
     */
    public static final Path DEFAULT_SERVER_CONFIG_PATH = Path.of("common", "src", "main", "resources", "configs", "default_server.cfg.json");
}
