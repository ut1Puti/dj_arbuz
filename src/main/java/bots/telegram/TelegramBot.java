package bots.telegram;

import bots.BotTextResponse;
import bots.BotUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import stoppable.Stoppable;
import database.UserStorage;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.NotificationsPuller;
import bots.StoppableByUser;
import user.User;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Класс для обработки сообщений, полученных из телеграммац
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
public class TelegramBot extends TelegramLongPollingBot implements Stoppable, StoppableByUser {
    private final NotificationsPuller notificationsPuller;

    /** Никнейм бота*/
    private final String botUserName;
    /**Токен бота*/
    private final String telegramToken;
    /**
     * Поле кнопок в телеграмм
     **/
    private final List<KeyboardRow> keyBoardRows;

    /**
     * Конструктор класса для инициализации бота и поля кнопок
     * Также полученные данных бота(ник и токен)
     */
    public TelegramBot(String tgConfigurationFilePath) {
        //TODO кнопки
        super();
        keyBoardRows = new ArrayList<>();
        Properties prop = new Properties();
        FileInputStream stream;
        try {
            stream = new FileInputStream(tgConfigurationFilePath);
            prop.load(stream);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Файла по пути " + tgConfigurationFilePath + " не найдено");
        }
        botUserName = prop.getProperty("botUsernName");
        telegramToken = prop.getProperty("botToken");
        KeyboardRow rowFirst = new KeyboardRow();
        rowFirst.add("/auth");
        rowFirst.add("/help");
        keyBoardRows.add(rowFirst);
        notificationsPuller = new NotificationsPuller(this);
        notificationsPuller.start();
    }

    /**
     * Основная логика работы бота
     *
     * @param args - аргументы командной строки
     */
    public static void main(String[] args) {
        BotUtils.initInstances();
        TelegramBot telegramBot = new TelegramBot("src/main/resources/anonsrc/tgconfig.properties");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        while (telegramBot.isWorking()) Thread.onSpinWait();
        telegramBot.stopWithInterrupt();
        BotUtils.stopInstances();
        System.exit(0);
    }

    /**
     * Метод для получения имени бота
     *
     * @return никнейм бота в Телеграмме
     */
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    /**
     * Метод для получения токена бота
     *
     * @return токен бота в Телеграмме
     */
    @Override
    public String getBotToken() {
        return telegramToken;
    }

    /**
     * Основной метод для отправки сообщений бота
     * Бот получает какие-то обновления и моментально на них реагирует
     *
     * @param update обновления, полученные с Телеграмма, когда какой-то пользователь написал боту
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage()
                                         .hasText()) {
            String messageUpdate = update.getMessage()
                                         .getText();
            MessageHandlerResponse response = MessageHandler.executeMessage(
                    messageUpdate, String.valueOf(update.getMessage()
                                                        .getChatId()), this
            );
            if (response.hasTextMessage()) {
                sendMessage(update, response);
            }

            if (response.hasPostsMessages()) {
                for (String post : response.getPostsMessages()) {
                    SendMessage message = new SendMessage();
                    message.setText(post);
                    message.setChatId(String.valueOf(update.getMessage()
                                                           .getChatId()));
                    try {
                        execute(message);
                    } catch (TelegramApiException ignored) {
                    }
                }
            }

            if (response.hasUpdateUser()) {
                User user = response.getUpdateUser()
                                    .createUser(update.getMessage()
                                                      .getChatId()
                                                      .toString());

                if (user == null) {
                    SendMessage authErrorMessage = new SendMessage(update.getMessage()
                                                                         .getChatId()
                                                                         .toString(), BotTextResponse.AUTH_ERROR);
                    try {
                        execute(authErrorMessage);
                    } catch (TelegramApiException ignored) {
                    }
                } else {
                    UserStorage userBase = UserStorage.getInstance();
                    userBase.addInfoUser(String.valueOf(update.getMessage()
                                                              .getChatId()), user);
                }

            }

        }

    }

    /**
     * Метод для отправки сообщений
     *
     * @param update   обновления, полученные с Телеграмма, когда какой-то пользователь написал боту
     * @param response хранит в себе текст сообщения для отправки пользователю
     */
    private void sendMessage(Update update, MessageHandlerResponse response) {
        SendMessage message = new SendMessage();
        ReplyKeyboardMarkup keyBoardMarkup = new ReplyKeyboardMarkup();
        keyBoardMarkup.setKeyboard(keyBoardRows);
        message.setReplyMarkup(keyBoardMarkup);
        message.setChatId(String.valueOf(update.getMessage()
                                               .getChatId()));
        message.setText(response.getTextMessage());
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    /**
     * Метод проверяющий работает ли поток
     *
     * @return true - если работает
     * false - если поток завершил работу
     */
    @Override
    public boolean isWorking() {
        return !exe.isShutdown();
    }

    /**
     * Метод останавливающий работу потока путем прерывания
     */
    @Override
    public void stopWithInterrupt() {
        notificationsPuller.stop();
        exe.shutdownNow();
    }

    /**
     * Реализация интерфейса позволяющая останавливать поток по запросу пользователя
     */
    @Override
    public void stopByUser() {
        stopWithInterrupt();
    }
}