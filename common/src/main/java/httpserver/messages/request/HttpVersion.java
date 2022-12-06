package httpserver.messages.request;

import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Енум хранящий версии http поддерживаемые сервером
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public enum HttpVersion {
    /**
     * Значения поддерживаемых версий
     */
    HTTP_1_1("HTTP/1.1", 1, 1);

    /**
     * Поле протокола запроса
     */
    private final String stringNameOfHttpVersion;
    /**
     * Поле версии
     */
    private final int major;
    /**
     * Поле версии
     */
    private final int minor;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param stringNameOfHttpVersion - протокол запроса
     * @param major    - версия протокола
     * @param minor    - версия протокола
     */
    HttpVersion(String stringNameOfHttpVersion, int major, int minor) {
        this.stringNameOfHttpVersion = stringNameOfHttpVersion;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Метод ищущий лучшую совместимую версию
     *
     * @param requestVersion - версия протокола из полученного запроса
     * @return лучшую совместимую версию протокола
     * @throws HttpParserException - ошибка возникает если протокол не поддерживает сервером или некорректно составлен
     * @see HttpStatusCode#CLIENT_ERROR_400_BAD_REQUEST
     */
    public static HttpVersion getBestCompatibleVersion(String requestVersion) throws HttpParserException {
        Matcher matcher = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)").matcher(requestVersion);

        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new HttpParserException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));
        HttpVersion bestCompatibleVersion = null;
        int minMajorDiff = Integer.MAX_VALUE;
        int minMinorDiff = Integer.MAX_VALUE;
        for (HttpVersion httpVersion : HttpVersion.values()) {

            if (httpVersion.stringNameOfHttpVersion.equals(requestVersion)) {
                return httpVersion;
            }

            if (Math.abs(httpVersion.major - major) < minMajorDiff) {

                if (Math.abs(httpVersion.minor - minor) < minMinorDiff) {
                    bestCompatibleVersion = httpVersion;
                }
            }
        }
        return bestCompatibleVersion;
    }

    /**
     * Метод превращающий имя в строку
     *
     * @return строку енума
     */
    public String getValueString() {
        return stringNameOfHttpVersion;
    }
}
