package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//сделать javadoc(весь пакет)
//переписать с вменяемыми переменными и методами(весь пакет)
public class HttpResponse {
    public static String createResponse(String getFileName) throws IOException {
        StringBuilder html = new StringBuilder();
        String response = null;
        File file = new File(HttpServerConfig.WEB_SRC + getFileName);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            int _byte;
            while ((_byte = fileInputStream.read()) != -1) {
                html.append((char) _byte);
            }
            fileInputStream.close();
            response = "HTTP/1.1 200 OK" + HttpServerConfig.CRLF +
                    "Content-Length:" + html.toString().getBytes().length + HttpServerConfig.CRLF +
                    HttpServerConfig.CRLF + html + HttpServerConfig.CRLF + HttpServerConfig.CRLF;
        }
        return response;
    }
}
