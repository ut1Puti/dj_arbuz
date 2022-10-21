package httpserver.messages.response;

import httpserver.server.HttpServerConfiguration;

import java.io.*;

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
     * @see HttpServerConfiguration#WEB_SRC
     * @see HttpResponse#loadResponse(File)
     */
    public static String createResponse(String getFileName) throws IOException {
        File file = new File(HttpServerConfiguration.WEB_SRC + getFileName);
        if (!file.exists()) {
            return loadResponse(new File(HttpServerConfiguration.WEB_SRC + "/404.html"));
        }
        return loadResponse(file);
    }

    /**
     * Метод загружающий данные из файла в строку
     *
     * @param srcLoadingFile - файл из которого загружаем
     * @return строку с данными из файла
     * @throws IOException - возникает при ошибке чтения из файла
     * @see HttpServerConfiguration#CRLF
     */
    private static String loadResponse(File srcLoadingFile) throws IOException {
        StringBuilder html = new StringBuilder();
        BufferedReader fileReader = new BufferedReader(new FileReader(srcLoadingFile));
        fileReader.lines().forEach(html::append);
        fileReader.close();
        return "HTTP/1.1 200 OK" + HttpServerConfiguration.CRLF +
                "Content-Length:" + html.toString().getBytes().length + HttpServerConfiguration.CRLF +
                HttpServerConfiguration.CRLF + html;
    }
}
