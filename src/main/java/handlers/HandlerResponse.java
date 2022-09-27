package handlers;

import user.CreateUser;

/**
 * Класс ответов handler'а
 * @author Кедровских Олег
 * @version 1.0
 */
public class HandlerResponse {
    /** Поле текстового сообщения */
    private String textMessage;
    /** Поле содержащее интерфейс для создания или обновления пользователя */
    private CreateUser updateUser;

    /**
     * Конструктор - создание объекта с определенными параметрами
     * @param textMessage - текстовое сообщение
     * @param updateUser - интерфейс для обновления или создания пользователя
     */
    public HandlerResponse(String textMessage, CreateUser updateUser){
        this.textMessage = textMessage;
        this.updateUser = updateUser;
    }

    /**
     * Метод проверяющий наличии текстового сообщения
     * @return наличие текстовое сообщение
     */
    public boolean hasTextMessage(){
        return textMessage != null;
    }

    /**
     * Метод возвращающий текстовое сообщение
     * @return текствое сообщение
     */
    public String getTextMessage(){
        return textMessage;
    }

    /**
     * Метод проверяющий наличии интерфейса для обновления или создания пользователя
     * @return наличие интерфейса для обновления или создания пользователя
     */
    public boolean hasCreateUser(){
        return updateUser != null;
    }

    /**
     * Метод возвращающий интерфейс для обновления или создания пользователя
     * @return интерфейс для обновления или создания пользователя
     */
    public CreateUser getUpdateUser(){
        return updateUser;
    }
}
