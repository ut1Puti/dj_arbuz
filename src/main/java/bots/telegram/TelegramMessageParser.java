package bots.telegram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramMessageParser {
    /**
     *
     */
    private static final Pattern pattern = Pattern.compile("(?<link>https://vk.com/[\\d\\w]+) \\((?<name>[@а-яА-Я\\w\\s]+)\\)?");

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private TelegramMessageParser() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     *
     *
     * @param parseString
     * @return
     */
    public static String parseMessageTextToHtml(String parseString) {
        StringBuilder sb = new StringBuilder(parseString);
        Matcher matcher = pattern.matcher(parseString);
        while (matcher.find()) {
            sb.replace(matcher.start(), sb.length(), matcher.replaceFirst(createInlineLink(matcher.group("link"), matcher.group("name"))));
            matcher = pattern.matcher(sb);
        }
        return sb.toString();
    }

    /**
     *
     *
     * @param groupLink
     * @param groupScreenName
     * @return
     */
    private static String createInlineLink(String groupLink, String groupScreenName) {
        return "<a href=\"" + groupLink + "\">" + groupScreenName + "</a>";
    }
}
