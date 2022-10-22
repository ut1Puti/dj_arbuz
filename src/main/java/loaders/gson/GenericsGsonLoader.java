package loaders.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Scanner;

/**
 *
 *
 * @author
 * @version
 */
public class GenericsGsonLoader<T extends JsonLoadable> {
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
        Type type = new TypeToken<GenericGsonAdapter<T>>(){}.getType();
        Gson jsonFile = new Gson();
        GenericGsonAdapter<T> genericClassJsonAdapter = jsonFile.fromJson(json, type);
        return genericClassJsonAdapter.objectToSaveField;
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

        Gson gson = new Gson();
        GenericGsonAdapter<T> genericClassJsonAdapter = new GenericGsonAdapter<>(objectToSave);
        String json = gson.toJson(genericClassJsonAdapter);
        FileWriter file = new FileWriter(pathToSaveObject);
        file.write(json);
        file.close();
    }

    /**
     *
     *
     * @param <T>
     */
    private class GenericGsonAdapter<T> {
        /**
         *
         */
        private T objectToSaveField;

        /**
         *
         *
         * @param objectToSaveField
         */
        private GenericGsonAdapter(T objectToSaveField) {
            this.objectToSaveField = objectToSaveField;
        }
    }
}

