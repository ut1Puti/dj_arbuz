package httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpParser {
    public static String parseRequest(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            requestBuilder.append(line + HttpServerConfig.CRLF);
        }
        return requestBuilder.toString();
    }
}
