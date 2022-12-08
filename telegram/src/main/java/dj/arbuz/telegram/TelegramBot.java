package dj.arbuz.telegram;

import dj.arbuz.ExecutableMessage;
import dj.arbuz.TelegramConfigPaths;

import httpserver.HttpServerNano;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import stoppable.Stoppable;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для обработки сообщений, полученных из телеграммац
 *
 * @author Щёголев Андрей
 * @version 1.2
 */
public final class TelegramBot extends TelegramLongPollingBot implements Stoppable, ExecutableMessage {
    /**
     * Поле класса содержащего конфигурацию телеграм бота
     *
     * @see TelegramBotConfiguration
     */
    private final TelegramBotConfiguration telegramBotConfiguration;
    /**
     * Поле класс для исполнения сообщений пользователя
     *
     * @see TelegramMessageExecutor
     */
    private final TelegramMessageExecutor messageExecutor;

    /**
     * Конструктор класса для инициализации бота и поля кнопок
     * Также полученные данных бота(ник и токен)
     *
     * @param tgConfigurationFilePath путь до json файла с конфигурацией
     */
    public TelegramBot(Path tgConfigurationFilePath) {
        super();
        telegramBotConfiguration = TelegramBotConfiguration.loadTelegramBotConfigurationFromJson(tgConfigurationFilePath);
        messageExecutor = new TelegramMessageExecutor(this);
    }

    /**
     * Конструктор - создает экземпляр класса
     */
    public TelegramBot() {
        super();
        telegramBotConfiguration = TelegramBotConfiguration.loadFromEnvVariables();
        messageExecutor = new TelegramMessageExecutor(this);
    }

    public static void main(String[] args) throws RuntimeException, IOException {
        HttpServerNano httpServerNano = HttpServerNano.createInstance(TelegramConfigPaths.SERVER_CONFIG_PATH);
        int startTimeout = 0;
        boolean isDaemon = true;
        httpServerNano.start(startTimeout, isDaemon);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot(TelegramConfigPaths.TELEGRAM_CONFIG_PATH));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String userReceivedMessage = update.getMessage().getText();
                String userReceivedMessageId = update.getMessage().getChatId().toString();
                messageExecutor.executeUserMessage(userReceivedMessageId, userReceivedMessage);
            }
        }

    }

    /**
     * Реализация интерфейса для отправки сообщения пользователю, отправляет сообщение пользователю в чат с ботом в телеграме
     *
     * @param userSendResponseId  id пользователя, которому необходимо отправить сообщение
     * @param responseSendMessage сообщение, которое будет отправлено пользователю
     */
    @Override
    public void send(String userSendResponseId, String responseSendMessage) {
        SendMessage sendMessage = new SendMessage(userSendResponseId, TelegramMessageParser.parseMessageTextToHtml(responseSendMessage));
        sendMessage.setParseMode(ParseMode.HTML);
        List<KeyboardRow> keyBoardRows = new ArrayList<>();
        KeyboardRow rowFirst = new KeyboardRow();
        rowFirst.add("/auth");
        rowFirst.add("/help");
        keyBoardRows.add(rowFirst);
        ReplyKeyboardMarkup keyBoardMarkup = new ReplyKeyboardMarkup();
        keyBoardMarkup.setKeyboard(keyBoardRows);
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
        messageExecutor.stop();
        exe.shutdownNow();
    }
}
