package loaders.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Класс для преобразования экземпляров классов в json строки
 *
 * @param <T> тип объекта обрабатываемого классом
 * @author Кедровских Олег
 * @version 1.0
 */
public class GsonLoader<T> {
    /**
     * Поле класса преобразовывающего объекты в json строки
     *
     * @see Gson
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Поле класса, который преобразуем
     */
    private final Class<T> jsonLoadingClass;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param jsonLoadingClass класс, который будет обрабатываться экземпляром {@code GenericsGsonLoader}
     */
    public GsonLoader(Class<T> jsonLoadingClass) {
        this.jsonLoadingClass = jsonLoadingClass;
    }

    /**
     * Метод превращающий прочитанную из json файла строку в экземпляр типа {@code T}
     *
     * @param pathToLoadObject - путь до json файла, из которого будет читать json строка
     * @return экземпляр типа {@code T} созданный из строки из переданного файла
     * @throws IOException возникает при ошибках чтения файла
     */
    public T loadFromJson(String pathToLoadObject) throws IOException {
        try (Reader fileReader = Files.newBufferedReader(Path.of(pathToLoadObject))) {
            return GSON.fromJson(fileReader, jsonLoadingClass);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Метод превращающий экземпляр типа {@code T} в json строку и записывающий ее в файл
     *
     * @param pathToSaveObject путь до json файла в который будет сохранен объект
     * @param objectToSave     экземпляр типа {@code T} который будет сохранен
     * @throws IOException возникает при ошибках записи в файл
     */
    public void loadToJson(String pathToSaveObject, T objectToSave) throws IOException {

        if (objectToSave == null) {
            return;
        }

        String json = GSON.toJson(objectToSave);
        try (Writer fileWriter = Files.newBufferedWriter(Path.of(pathToSaveObject), StandardCharsets.UTF_8)) {
            fileWriter.write(json);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}

