package bots.telegram;

import bots.BotTextResponse;
import bots.BotUtils;
import stoppable.Stoppable;
import database.UserStorage;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.NotificationsPuller;
import user.User;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot extends TelegramLongPollingBot implements Stoppable {
    private final NotificationsPuller notificationsPuller;

    public TelegramBot() {
        //TODO кнопки
        super();
        notificationsPuller = new NotificationsPuller(this);
        notificationsPuller.start();
    }

    public static void main(String[] args) {
        BotUtils.initInstances();
        TelegramBot telegramBot = new TelegramBot();
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

    @Override
    public String getBotUsername() {
        return "https://t.me/oop_urfu_bot";
    }

    @Override
    public String getBotToken() {
        return "5621043600:AAFot_kJRSb2o9oM3l_eezqIvt-KyaSXrbE";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage()
                .hasText()) {
            String messageUpdate = update.getMessage()
                    .getText();
            MessageHandlerResponse response = MessageHandler.executeMessage(
                    messageUpdate, String.valueOf(update.getMessage().getChatId()), this
            );
            if (response.hasTextMessage()) {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(update.getMessage().getChatId()));
                message.setText(response.getTextMessage());
                try {
                    execute(message);
                } catch (TelegramApiException ignored) {}
            }

            if (response.hasPostsMessages()) {
                for (String post : response.getPostsMessages()) {
                    SendMessage message = new SendMessage();
                    message.setText(post);
                    message.setChatId(String.valueOf(update.getMessage().getChatId()));
                    try {
                        execute(message);
                    } catch (TelegramApiException ignored) {}
                }
            }

            if (response.hasUpdateUser()) {
                User user = response.getUpdateUser().createUser(update.getMessage().getChatId().toString());

                if (user == null) {
                    SendMessage authErrorMessage = new SendMessage(update.getMessage().getChatId().toString(), BotTextResponse.AUTH_ERROR);
                    try {
                        execute(authErrorMessage);
                    } catch (TelegramApiException ignored) {}
                } else {
                    UserStorage userBase = UserStorage.getInstance();
                    userBase.addInfoUser(String.valueOf(update.getMessage().getChatId()), user);
                }

            }

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
}