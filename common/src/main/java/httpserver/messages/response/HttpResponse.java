package httpserver.messages.response;

import httpserver.messages.HttpMessage;
import httpserver.messages.request.HttpRequest;
import httpserver.messages.request.HttpVersion;
import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;
import httpserver.server.HttpServerConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс ответа http сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class HttpResponse extends HttpMessage {
    private HttpVersion httpVersion;
    private HttpStatusCode httpStatusCode;
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    private HttpResponse() {
        headers.put("Connection:", "close");
    }

    public HttpResponse addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    public static HttpResponse createResponseFromRequest(HttpRequest httpRequest) throws HttpParserException {
        HttpResponse httpResponse = new HttpResponse();
        Path requestTargetFile = Path.of(HttpServerConfiguration.WEB_SRC, httpRequest.getTarget().getTargetFile()).normalize();

        if (!requestTargetFile.startsWith(HttpServerConfiguration.WEB_SRC)) {
            throw new HttpParserException(HttpStatusCode.NOT_FOUND_404);
        }

        try (Stream<String> fileDataStream = Files.lines(requestTargetFile, StandardCharsets.UTF_8)) {
            String body = fileDataStream.collect(Collectors.joining(" "));
            httpResponse.setHttpStatusCode(HttpStatusCode.OK_200)
                    .setBody(body)
                    .addHeader("Content-Type:", "text/html; charset=utf-8")
                    .addHeader("Content-Length:", String.valueOf(body.getBytes().length));
        } catch (IOException e) {
            throw new HttpParserException(HttpStatusCode.NOT_FOUND_404);
        }

        httpResponse.setHttpVersion(httpRequest.getBestCompatibleHttpVersion());
        return httpResponse;
    }

    public static HttpResponse createFromHttpErrorCode(HttpStatusCode httpStatusCode) {
        return new HttpResponse().setHttpStatusCode(httpStatusCode)
                .setHttpVersion(HttpVersion.HTTP_1_1);
    }

    public String toHttpMessage() {
        return httpVersion.getValueString() + ' ' + httpStatusCode.getStatusCode() + ' ' + httpStatusCode.getCodeMessage() + HttpServerConfiguration.CRLF +
                headers.entrySet().stream().map(entry -> entry.getKey() + entry.getValue()).collect(Collectors.joining("\n")) + HttpServerConfiguration.CRLF +
                HttpServerConfiguration.CRLF +
                body;
    }
}
