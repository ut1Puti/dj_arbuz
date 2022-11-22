package dj.arbuz.database.local;

import java.nio.file.Path;

/**
 *
 *
 * @author
 * @version 1.0
 */
public class LocalDatabasePaths {
    /**
     *
     */
    public static final Path localGroupDataBasePath = Path.of("src", "main", "resources", "anonsrc", "database_for_groups.json");
    /**
     *
     */
    public static final Path localUserDataBasePath = Path.of("src", "main", "resources", "anonsrc", "database_for_users.json");
}
