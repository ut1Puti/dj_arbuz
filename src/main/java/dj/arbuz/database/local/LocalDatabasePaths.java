package dj.arbuz.database.local;

import java.nio.file.Path;

/**
 * Класс путей до файлов с данными локальной базы данных
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class LocalDatabasePaths {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private LocalDatabasePaths() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Поле пути до файла с данными о группах
     */
    static final Path LOCAL_GROUP_DATA_BASE_PATH = Path.of("src", "main", "resources", "anonsrc", "database_for_groups.json");
    /**
     * Поле пути до файла с данными о пользователях
     */
    static final Path LOCAL_USER_DATA_BASE_PATH = Path.of("src", "main", "resources", "anonsrc", "database_for_users.json");
}
