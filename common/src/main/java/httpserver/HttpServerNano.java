package httpserver;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpServerNano extends NanoHTTPD {
    private static HttpServerNano httpServerInstance;
    private final HttpServerConfiguration httpServerConfiguration;
    private final Map<String, String> map = Collections.synchronizedMap(new WeakHashMap<>());

    private HttpServerNano(HttpServerConfiguration serverConfig) {
        super(serverConfig.hostName, serverConfig.port);
        this.httpServerConfiguration = serverConfig;
    }

    public static HttpServerNano createInstance(Path serverConfigPath) throws IOException {
        if (httpServerInstance == null) {
            httpServerInstance = new HttpServerNano(HttpServerConfiguration.readConfigurationFromFile(serverConfigPath));
        } else {
            throw new IllegalStateException("Вы не можете создать сервер повторно");
        }
        return httpServerInstance;
    }

    public static HttpServerNano getInstance() {
        return httpServerInstance;
    }

    @Override
    public Response serve(IHTTPSession session) {
        switch (session.getMethod()) {
            case GET -> {
                Path requestedFile = Path.of(httpServerConfiguration.webDataPathAsString + session.getUri()).normalize();

                if (!requestedFile.startsWith(httpServerConfiguration.webDataPathAsString)) {
                    return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Requested file not found");
                }

               String httpGetParams = session.getQueryParameterString();

                if (httpGetParams != null) {
                    map.put(httpGetParams, null);
                }

                try {
                    return newFixedLengthResponse(Status.OK, MIME_HTML, readRequestedFile(requestedFile));
                } catch (IOException | UncheckedIOException e) {
                    return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal server error");
                }
            }
            case HEAD -> {
                return newFixedLengthResponse(Status.OK, MIME_PLAINTEXT, "hello");
            }
            default -> {
                return newFixedLengthResponse(Status.NOT_IMPLEMENTED, MIME_PLAINTEXT, "Method unsupported");
            }
        }
    }

    private String readRequestedFile(Path pathToFile) throws IOException {
        try (Stream<String> fileDataStream = Files.lines(pathToFile, StandardCharsets.UTF_8)) {
            return fileDataStream.collect(Collectors.joining(" "));
        }
    }

    public Stream<String> getHttpGetRequestParams() {
        return map.keySet().stream();
    }
}
