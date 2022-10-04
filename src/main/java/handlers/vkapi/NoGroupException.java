package handlers.vkapi;

/**
 * Класс ошибки при ненахождении группы
 * @author Кедровских Олег
 * @version 1.0
 */
public class NoGroupException extends Exception{
    /**
     * Конструктор - унаследованный от родительского класса
     * @param groupName - имя группы
     */
    public NoGroupException(String groupName) {
        super("Группы с названием " + groupName + " не существует");
    }
}
