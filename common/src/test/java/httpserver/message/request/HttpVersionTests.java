package httpserver.message.request;

import httpserver.messages.request.HttpVersion;
import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Класс для тестирования класса совместимой версии http
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpVersionTests {
    /**
     * Метод тестирующий корректность получения лучшей совместимой версии http
     *
     * @param httpVersion - полученная версия http
     * @param expectedBestCompatibleHttpVersion - лучшая совместимая версия
     * @throws HttpParserException - вознкает если нельзя получить http версию из httpVersion
     */
    @ParameterizedTest(name = "Версия из запроса {0} : Совместимая версия {1}")
    @MethodSource("testCompatibleVersionsData")
    public void testCompatibleHttpVersion(String httpVersion, HttpVersion expectedBestCompatibleHttpVersion)
            throws HttpParserException {
        HttpVersion compatibleVersion = HttpVersion.getBestCompatibleVersion(httpVersion);
        assertEquals(expectedBestCompatibleHttpVersion, compatibleVersion);
    }

    /**
     * Метод создающий данные для теста совместимости версий
     *
     * @return stream данных для теста
     */
    public static Stream<Arguments> testCompatibleVersionsData() {
        return Stream.of(
                Arguments.of("HTTP/1.1", HttpVersion.HTTP_1_1),
                Arguments.of("HTTP/1.0", HttpVersion.HTTP_1_1),
                Arguments.of("HTTP/1.5", HttpVersion.HTTP_1_1),
                Arguments.of("HTTP/2.2", HttpVersion.HTTP_1_1),
                Arguments.of("HTTP/0.9", HttpVersion.HTTP_1_1)
        );
    }

    /**
     * Метод тестирующий обработку исключения возникающего при невозможности извлечь версию http из переданной строки
     */
    @Test
    public void testIncorrectNameOfHttpVersion() {
        String httpVersion = "HTT/1.1";
        try {
            HttpVersion.getBestCompatibleVersion(httpVersion);
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }
}
