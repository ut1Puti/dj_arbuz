package loaders.gson;

import com.google.gson.Gson;

import java.io.*;
import java.util.Scanner;

/**
 * Класс для преобразования объектов классов в json строки
 *
 * @author Кедровских Олег
 * @version 1.0
 * @param <T> тип объекта обрабатываемого классом
 */
public class GenericsGsonLoader<T> {
    /**
     * Поле объекта преобразовывающего объекты в json строки
     *
     * @see Gson
     */
    private static final Gson gson = new Gson();
    /**
     * Поле класс, который преобразуем
     */
    private final Class<T> jsonLoadingClass;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param jsonLoadingClass класс, который будет обрабатываться экземпляром {@code GenericsGsonLoader}
     */
    public GenericsGsonLoader(Class<T> jsonLoadingClass) {
        this.jsonLoadingClass = jsonLoadingClass;
    }

    /**
     * Метод превращающий прочитанную из json файла строку в экземпляр объекта типа {@code T}
     *
     * @param pathToLoadObject - путь до json файла, из которого будет читать json строка
     * @return объект типа {@code T} созданный из строки из переданного файла
     * @throws IOException возникает при ошибках чтения файла
     */
    public T loadFromJson(String pathToLoadObject) throws IOException {
        FileReader fileReader = new FileReader(pathToLoadObject);
        Scanner scanner = new Scanner(fileReader);
        String json = scanner.nextLine();
        return gson.fromJson(json, jsonLoadingClass);
    }

    /**
     * Метод превращающий объект типа {@code T} в json строку и записывающий ее в файл
     *
     * @param pathToSaveObject - путь до json файла в который будет сохранен объект
     * @param objectToSave - объект типа {@code T} который будет сохранен
     * @throws IOException возникает при ошибках записи в файл
     */
    public void loadToJson(String pathToSaveObject, T objectToSave) throws IOException {

        if (objectToSave == null) {
            return;
        }

        String json = gson.toJson(objectToSave);
        FileWriter file = new FileWriter(pathToSaveObject);
        file.write(json);
        file.close();
    }
}

