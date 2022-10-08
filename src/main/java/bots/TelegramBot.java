package bots;

import handlers.messages.MessageHandler;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {

    public TelegramBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return "https://t.me/oop_urfu_bot";
    }

    @Override
    public String getBotToken() {
        return "5621043600:AAFot_kJRSb2o9oM3l_eezqIvt-KyaSXrbE";
    }

    public static void main(String[] args) {

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            MessageHandler.executeMessage(message.getText()
                                                   .toString(),);
        }
    }
}