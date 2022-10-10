package handlers.vk.groups;

/**
 * Класс ошибки при ненахождении группы
 * @author Кедровских Олег
 * @version 2.0
 */
public class NoGroupException extends Exception{
    /**
     * Конструктор - унаследованный от родительского класса
     * @param groupName - имя группы
     */
    public NoGroupException(String groupName) {
        super("Группы с названием " + groupName + " не существует");
    }
    public NoGroupException(int groupId) {
        super("Группы с id " + groupId + " не существует");
    }
}
