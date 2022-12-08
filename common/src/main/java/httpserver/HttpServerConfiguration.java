package httpserver;

import loaders.gson.GsonLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class HttpServerConfiguration {
    final String hostName;
    final Integer port;
    final String webDataPathAsString;

    static HttpServerConfiguration readConfigurationFromFile(Path pathToConfigFile) throws IOException {
        GsonLoader<HttpServerConfiguration> httpServerConfigurationGsonLoader = new GsonLoader<>();
        HttpServerConfiguration loadedConfig = httpServerConfigurationGsonLoader.loadFromJson(pathToConfigFile, HttpServerConfiguration.class);
        if (loadedConfig.hostName == null || loadedConfig.port == null || loadedConfig.webDataPathAsString == null) {
            throw new IOException("Bad json, couldn't read all data");
        }
        return loadedConfig;
    }
}
