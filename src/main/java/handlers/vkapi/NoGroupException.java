package handlers.vkapi;

public class NoGroupException extends Exception{
    public NoGroupException(String groupName) {
        super("Группы с названием" + groupName + "не существует");
    }
}
