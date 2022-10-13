package dj.arbuz.httpserver;

import dj.arbuz.httpserver.server.HttpServerConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Класс создающий ответ http сервера
 *
 * @author Кедровских Олег
 * @version 0.1
 */
public class HttpResponse {
    /**
     * Метод возвращающий ответ http сервера
     *
     * @param getFileName - имя файла из get запроса
     * @return ответ сервера
     * @throws IOException - возникает при проблемах с открытием файла
     */
    public static String createResponse(String getFileName) throws IOException {
        File file = new File(HttpServerConfiguration.WEB_SRC + getFileName);
        if (!file.exists()) {
            return null;
        }
        return loadResponse(file);
    }

    private static String loadResponse(File srcLoadingFile) throws IOException {
        StringBuilder html = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(srcLoadingFile);
        int _byte;
        while ((_byte = fileInputStream.read()) != -1) {
            html.append((char) _byte);
        }
        fileInputStream.close();
        return "HTTP/1.1 200 OK" + HttpServerConfiguration.CRLF +
                "Content-Length:" + html.toString().getBytes().length + HttpServerConfiguration.CRLF +
                HttpServerConfiguration.CRLF + html + HttpServerConfiguration.CRLF + HttpServerConfiguration.CRLF;
    }
}
