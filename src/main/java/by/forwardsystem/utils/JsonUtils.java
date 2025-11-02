package by.forwardsystem.utils;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import javax.swing.*;

@UtilityClass
public class JsonUtils {

    private static final Gson gson = new Gson();

    public <T> T parseJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public static String create(Object object) {
        return gson.toJson(object);
    }
}
