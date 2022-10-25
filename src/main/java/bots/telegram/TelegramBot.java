package bots.telegram;

import bots.BotStartInstances;
import bots.BotTextResponse;
import handlers.messages.MessageExecutable;
import handlers.notifcations.PostsPullingThread;
import handlers.notifcations.TelegramPostsPullingThread;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import stoppable.Stoppable;
import database.UserStorage;
import handlers.messages.MessageExecutorResponse;
import bots.StoppableByUser;
import user.User;

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
public class TelegramBot extends TelegramLongPollingBot implements Stoppable, StoppableByUser {
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
     * @see MessageExecutable
     */
    private final MessageExecutable messageExecutor;
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
    public TelegramBot(String tgConfigurationFilePath, BotStartInstances botStartInstances) {
        //TODO кнопки
        super();
        telegramBotConfiguration = TelegramBotConfiguration.loadTelegramBotConfigurationFromJson(tgConfigurationFilePath);
        this.userBase = botStartInstances.userStorage;
        messageExecutor = botStartInstances.messageExecutor;
        telegramPostsPullingThread = new TelegramPostsPullingThread(
                this, botStartInstances.groupsStorage, botStartInstances.vk
        );
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
        BotStartInstances botStartInstances = new BotStartInstances("src/main/resources/anonsrc/vk_config.json");
        TelegramBot telegramBot = new TelegramBot(
                "src/main/resources/anonsrc/telegram_config.json", botStartInstances
        );
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        while (telegramBot.isWorking()) Thread.onSpinWait();
        telegramBot.stopWithInterrupt();
        botStartInstances.stop();
        ;
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

        if (update.hasMessage() && update.getMessage()
                .hasText()) {
            String messageUpdate = update.getMessage()
                    .getText();
            MessageExecutorResponse response = messageExecutor.executeMessage(
                    messageUpdate, String.valueOf(update.getMessage().getChatId()), this
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
                    userBase.addInfoUser(String.valueOf(update.getMessage()
                            .getChatId()), user);
                }

            }

        }

    }

    /**
     * Метод для отправки сообщений
     *
     * @param update   обновления, полученные с Телеграма, когда какой-то пользователь написал боту
     * @param response хранит в себе текст сообщения для отправки пользователю
     */
    private void sendMessage(Update update, MessageExecutorResponse response) {
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