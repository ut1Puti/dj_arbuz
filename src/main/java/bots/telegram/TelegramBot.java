package bots.telegram;

import bots.BotMessageExecutable;
import database.GroupsStorage;
import handlers.messages.MessageHandleable;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.messages.TelegramMessageExecutor;
import handlers.notifcations.TelegramPostsPullingThread;
import httpserver.server.HttpServer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import socialnetworks.socialnetwork.SocialNetwork;
import socialnetworks.vk.Vk;
import stoppable.Stoppable;
import database.UserStorage;
import bots.StoppableByUser;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для обработки сообщений, полученных из телеграммац
 *
 * @author Щёголев Андрей
 * @version 1.2
 */
public class TelegramBot extends TelegramLongPollingBot implements Stoppable, StoppableByUser, BotMessageExecutable {
    /**
     * Поле класса содержащего конфигурацию телеграм бота
     *
     * @see TelegramBotConfiguration
     */
    private final TelegramBotConfiguration telegramBotConfiguration;
    /**
     * Поле хранящее пользователя пользующегося ботом
     *
     * @see UserStorage
     */
    private final UserStorage userBase;
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageHandleable
     */
    private final MessageHandleable messageHandler;
    /**
     * Поле класса-отправителя сообщений пользователю
     *
     * @see TelegramMessageExecutor
     */
    private final TelegramMessageExecutor messageExecutor;
    /**
     * Поле класса получающего новые посты из групп в базе данных
     *
     * @see TelegramPostsPullingThread
     */
    private final TelegramPostsPullingThread telegramPostsPullingThread;
    /**
     * Поле кнопок в телеграмм
     */
    private final List<KeyboardRow> keyBoardRows = new ArrayList<>();

    /**
     * Конструктор класса для инициализации бота и поля кнопок
     * Также полученные данных бота(ник и токен)
     *
     * @param tgConfigurationFilePath путь до json файла с конфигурацией
     */
    public TelegramBot(String tgConfigurationFilePath, UserStorage userStorage, GroupsStorage groupsStorage, SocialNetwork socialNetwork) {
        //TODO кнопки
        super();
        telegramBotConfiguration = TelegramBotConfiguration.loadTelegramBotConfigurationFromJson(tgConfigurationFilePath);
        this.userBase = userStorage;
        messageHandler = new MessageHandler(groupsStorage, userStorage, socialNetwork);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        messageExecutor = new TelegramMessageExecutor(this);
        telegramPostsPullingThread = new TelegramPostsPullingThread(this, groupsStorage, socialNetwork);
        telegramPostsPullingThread.start();
        KeyboardRow rowFirst = new KeyboardRow();
        rowFirst.add("/auth");
        rowFirst.add("/help");
        keyBoardRows.add(rowFirst);
    }

    /**
     * Основная логика работы бота
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        HttpServer httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        httpServer.start();
        SocialNetwork vk = new Vk();
        UserStorage userStorage = UserStorage.getInstance();
        GroupsStorage groupsStorage = GroupsStorage.getInstance();
        TelegramBot telegramBot = new TelegramBot("src/main/resources/anonsrc/telegram_config.json", userStorage, groupsStorage, vk);
        while (telegramBot.isWorking()) Thread.onSpinWait();
        telegramBot.stopWithInterrupt();
        httpServer.stop();
        userStorage.saveToJsonFile();
        groupsStorage.saveToJsonFile();
        System.exit(0);
    }

    /**
     * Метод для получения имени бота
     *
     * @return никнейм бота в Телеграмме
     */
    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.botUserName;
    }

    /**
     * Метод для получения токена бота
     *
     * @return токен бота в Телеграмме
     */
    @Override
    public String getBotToken() {
        return telegramBotConfiguration.telegramBotToken;
    }

    /**
     * Основной метод для отправки сообщений бота
     * Бот получает какие-то обновления и моментально на них реагирует
     *
     * @param update обновления, полученные с Телеграмма, когда какой-то пользователь написал боту
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            MessageHandlerResponse response = messageHandler.handleMessage(update.getMessage().getText(), update.getMessage().getChatId().toString(), this);
            messageExecutor.executeMessage(response, userBase);
        }

    }

    /**
     * Реализация интерфейса для отправки сообщения пользователю, отправляет сообщение пользователю в чат с ботом в телеграме
     *
     * @param userSendResponseId  id пользователя, которому необходимо отправить сообщение
     * @param responseSendMessage сообщение, которое будет отправлено пользователю
     */
    @Override
    public void execute(String userSendResponseId, String responseSendMessage) {
        SendMessage sendMessage = new SendMessage(userSendResponseId, responseSendMessage);
        ReplyKeyboardMarkup keyBoardMarkup = new ReplyKeyboardMarkup();
        keyBoardMarkup.setKeyboard(keyBoardRows);
        sendMessage.setReplyMarkup(keyBoardMarkup);
        try {
            execute(sendMessage);
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
        telegramPostsPullingThread.stopWithInterrupt();
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