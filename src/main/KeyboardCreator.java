package entity;

import entity.commands.Command;
import entity.commands.CommandsInitializer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyboardCreator {

    private final HashMap<String, Command> commands = CommandsInitializer.getAvailableCommands();

    public static InlineKeyboardMarkup getInlineKeyboard(Integer columnCount,String ...buttonNames) {

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var keyboardRow = new ArrayList<InlineKeyboardButton>();
        Integer count = 0;
        for (var buttonName : buttonNames) {
            if (count >= columnCount) {
                keyboard.add(new ArrayList<>(keyboardRow));
                keyboardRow.clear();
                count = 0;
            }
            keyboardRow.add((createButton(buttonName)));
            count++;
        }
        if (!keyboardRow.isEmpty()) {
            keyboard.add(keyboardRow);
        }
        return new InlineKeyboardMarkup(keyboard);
    }

    private static InlineKeyboardButton createButton(String name) {

        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(name);
        button.setCallbackData("/start");

        return button;
    }

}
