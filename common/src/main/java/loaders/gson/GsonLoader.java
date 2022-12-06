package loaders.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Класс для преобразования экземпляров классов в json строки
 *
 * @param <T> тип объекта обрабатываемого классом
 * @author Кедровских Олег
 * @version 1.0
 */
public final class GsonLoader<T> {
    /**
     * Поле класса преобразовывающего объекты в json строки
     *
     * @see Gson
     */
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Метод превращающий прочитанную из json файла строку в экземпляр типа {@code T}
     *
     * @param pathToLoadObject - путь до json файла, из которого будет читать json строка
     * @return экземпляр типа {@code T} созданный из строки из переданного файла
     * @throws IOException возникает при ошибках чтения файла
     */
    public T loadFromJson(Path pathToLoadObject, Type jsonLoadingType) throws IOException {
        try (Reader fileReader = Files.newBufferedReader(pathToLoadObject, StandardCharsets.UTF_8)) {
            return GSON.fromJson(fileReader, jsonLoadingType);
        }
    }

    /**
     * Метод для превращения json строки объекта
     *
     * @param objectToString объект, который превратиться в json строку
     * @return json строку объекта
     */
    public String toJsonString(T objectToString) {
        return GSON.toJson(objectToString);
    }

    /**
     * Метод превращающий json строку в объект
     *
     * @param jsonString json строка объекта
     * @return объект построенный по json строке
     */
    public T fromJsonString(String jsonString, Type jsonLoadingType) {
        return GSON.fromJson(jsonString, jsonLoadingType);
    }

    /**
     * Метод превращающий объект типа {@code T} в json строку и записывающий ее в файл
     *
     * @param pathToSaveObject - путь до json файла в который будет сохранен объект
     * @param objectToSave     - объект типа {@code T} который будет сохранен
     * @throws IOException возникает при ошибках записи в файл
     */
    public void loadToJson(Path pathToSaveObject, T objectToSave) throws IOException {

        if (objectToSave == null) {
            return;
        }

        String json = GSON.toJson(objectToSave);
        try (Writer fileWriter = Files.newBufferedWriter(pathToSaveObject, StandardCharsets.UTF_8)) {
            fileWriter.write(json);
            fileWriter.flush();
        }
    }
}

