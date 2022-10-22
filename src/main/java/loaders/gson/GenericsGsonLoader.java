package loaders.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.vk.oAuth.VkAuthConfiguration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Класс для преобразования объектов классов в json строки
 *
 * @author Кедровских Олег
 * @version 1.0
 * @param <T>
 */
public class GenericsGsonLoader<T> {
    /**
     * Поле объекта преобразовывающего объекты в json строки
     */
    private static final Gson gson = new Gson();
    /**
     * Поле класс, который преобразуем
     */
    private final Class<T> clazz;

    /**
     *
     *
     * @param clazz
     */
    public GenericsGsonLoader(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     *
     *
     * @param pathToLoadObject
     * @return
     * @throws IOException
     */
    public T loadFromJson(String pathToLoadObject) throws IOException {
        FileReader fileReader = new FileReader(pathToLoadObject);
        Scanner scanner = new Scanner(fileReader);
        String json = scanner.nextLine();
        return gson.fromJson(json, clazz);
    }

    /**
     *
     *
     * @param pathToSaveObject
     * @param objectToSave
     * @throws IOException
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

