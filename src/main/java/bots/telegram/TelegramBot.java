package bots.telegram;

import bots.BotTextResponse;
import database.UserStorage;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import user.User;

public class TelegramBot extends TelegramLongPollingBot {
    private final DefaultBotSession telegramBotThread;

    public TelegramBot(DefaultBotSession telegramBotThread) {
        super();
        this.telegramBotThread = telegramBotThread;
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
        SendMessage message = new SendMessage();
        if (update.hasMessage() && update.getMessage()
                                         .hasText()) {
            String messageUpdate = update.getMessage()
                                         .getText();
            MessageHandlerResponse response = MessageHandler.executeMessage(
                    messageUpdate, String.valueOf(update.getMessage().getChatId()), null
            );
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText(response.getTextMessage());
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            if (response.hasUpdateUser()) {
                User user = response.getUpdateUser().createUser();
                if (user == null) {
                    System.out.println(BotTextResponse.AUTH_ERROR);
                }
                if (user != null) {
                    UserStorage userBase = UserStorage.getInstance();
                    userBase.addInfoUser(String.valueOf(update.getMessage().getChatId()), user);
                }
            }

        }
    }

    public boolean isWorking() {
        return telegramBotThread.isRunning();
    }

    public void stop() {
        telegramBotThread.stop();
    }
}