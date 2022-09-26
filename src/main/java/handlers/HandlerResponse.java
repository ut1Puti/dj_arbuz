package handlers;

import user.CreateUser;

/**
 * Класс ответов handler'а
 * @author Кедровских Олег
 * @version 1.0
 */
public class HandlerResponse {
    /** Поле текстового сообщения */
    private String outMessage;
    /** Поле содержащее интерфейс для создания или обновления пользователя */
    private CreateUser createUser;

    /**
     * Конструктор - создание объекта с определенными параметрами
     * @param outMessage - текстовое сообщение
     * @param createUser - интерфейс для обновления или создания пользователя
     */
    public HandlerResponse(String outMessage, CreateUser createUser){
        this.outMessage = outMessage;
        this.createUser = createUser;
    }

    /**
     * Метод проверяющий наличии текстового сообщения
     * @return наличие текстовое сообщение
     */
    public boolean hasOutMessage(){
        return outMessage != null;
    }

    /**
     * Метод возвращающий текстовое сообщение
     * @return текствое сообщение
     */
    public String getOutMessage(){
        return outMessage;
    }

    /**
     * Метод проверяющий наличии интерфейса для обновления или создания пользователя
     * @return наличие интерфейса для обновления или создания пользователя
     */
    public boolean hasCreateUser(){
        return createUser != null;
    }

    /**
     * Метод возвращающий интерфейс для обновления или создания пользователя
     * @return интерфейс для обновления или создания пользователя
     */
    public CreateUser getCreateUser(){
        return createUser;
    }
}
