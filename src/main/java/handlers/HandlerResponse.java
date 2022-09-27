package handlers;

import user.CreateUser;

/**
 * Класс ответов handler'а
 * @author Кедровских Олег
 * @version 1.0
 */
public class HandlerResponse {
    /** Поле текстового сообщения */
    private String textMessage = null;
    /** Поле содержащее интерфейс для создания или обновления пользователя */
    private CreateUser updateUser = null;

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
     * Конструктор - создание объекта с определенными параметрами
     * @param textMessage - текстовое сообщение
     */
    public HandlerResponse(String textMessage){
        this.textMessage = textMessage;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     * @param updateUser - интерфейс для обновления или создания пользователя
     */
    public HandlerResponse(CreateUser updateUser){
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

    /**
     * Метод объединяющий сообщения разных ответов
     * @param anotherHandlerResponse - другой ответ
     * @return ответ вызвавший этот метод, с добавленным сообщением другого ответа
     */
    HandlerResponse appendTextMessage(HandlerResponse anotherHandlerResponse) {
        if (anotherHandlerResponse != null && anotherHandlerResponse.textMessage != null) {
            this.textMessage = this.textMessage + "\n" + anotherHandlerResponse.textMessage;
        }
        return this;
    }
}
