package dj.arbuz.bots.telegram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramMessageParser {
    /**
     * Поле регулярного выражения для нахождения подстроки состоящей из ссылки на группу и ее названия
     */
    private static final Pattern groupNameAndLinkRegex = Pattern.compile("\\[(?<name>[@а-яА-Я\\w\\s]+)]\\((?<link>https://vk.com/\\w+\\d+)\\)?");

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private TelegramMessageParser() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Метод, парсящий строку в html
     *
     * @param parseString строка, которую нужно распарсить
     * @return строку распаршенную для работы в html
     */
    public static String parseMessageTextToHtml(String parseString) {
        StringBuilder parsedStringBuilder = new StringBuilder(parseString);
        Matcher groupNameAndLinkMatcher = groupNameAndLinkRegex.matcher(parseString);
        while (groupNameAndLinkMatcher.find()) {
            parsedStringBuilder.replace(0, parsedStringBuilder.length(), groupNameAndLinkMatcher.replaceFirst(createInlineLink(groupNameAndLinkMatcher.group("link"), groupNameAndLinkMatcher.group("name"))));
            groupNameAndLinkMatcher = groupNameAndLinkRegex.matcher(parsedStringBuilder);
        }
        return parsedStringBuilder.toString();
    }

    /**
     * Метод создающий inline url для html
     *
     * @param groupLink ссылка на группу
     * @param groupScreenName короткое название группы
     * @return строку, которая представляет собой inline ссылку на группу
     */
    private static String createInlineLink(String groupLink, String groupScreenName) {
        return "<a href=\"" + groupLink + "\">" + groupScreenName + "</a>";
    }
}
