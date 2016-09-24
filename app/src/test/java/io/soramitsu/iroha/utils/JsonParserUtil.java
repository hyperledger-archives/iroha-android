package io.soramitsu.iroha.utils;

import com.google.gson.Gson;


public class JsonParserUtil {
    private JsonParserUtil() {
    }

    public static Object parse(String json, Class clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static String serealize(Object object) {
        return new Gson().toJson(object);
    }
}
