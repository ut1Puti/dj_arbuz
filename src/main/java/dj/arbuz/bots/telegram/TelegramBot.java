package dj.arbuz.bots.telegram;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.bots.console.ConsoleBot;
import dj.arbuz.stoppable.Stoppable;
import dj.arbuz.database.UserStorage;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import dj.arbuz.handlers.notifcations.NotificationsPuller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import dj.arbuz.user.User;

import java.util.HashMap;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot implements Stoppable {
    private final NotificationsPuller notificationsPuller;

    public TelegramBot() {
        super();
        notificationsPuller = new NotificationsPuller(this);
        notificationsPuller.start();
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