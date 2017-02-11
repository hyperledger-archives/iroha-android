package click.kobaken.rxirohaandroid.util;

import com.google.gson.Gson;

public class ParserUtil {
    public static <T> String serialize(T target) {
        return new Gson().toJson(target);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }
}
