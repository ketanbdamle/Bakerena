package store.bakerena.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Utility class for JSON computations.
 *
 * @author Ketan Damle
 * @version 1.0
 */

public class JsonUtil {

    private static final String JSONUTIL_TAG = BakerenaUtils.class.getName();

    /**
     * Converts the specified JSON string to an object of the specified class.
     *
     * @param jsonString JSON string to be transformed to a object of specified type.
     * @param clazz      Class of the object to which JSON string needs to be transformed.
     * @param <T>        Type of the class of the object to which JSON string needs to be transformed.
     * @return Object of the specified type, null if the string is not a valid representation of the type of the object, or null or empty.
     */
    public static <T> T convertToObject(String jsonString, Class<T> clazz) {
        Log.d(JSONUTIL_TAG, "Inside convertToObject");
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            Log.e(JSONUTIL_TAG, "JsonSyntaxException while parsing JSON string - msg: " + e.getMessage());
        }
        return null;
    }

    /**
     * Converts/serializes the specified object into its equivalent Json representation.
     *
     * @param obj Object for which Json representation is to be created setting for Gson
     * @param <T> Type of the object
     * @return Json representation of the specified object.
     */
    public static <T> String convertToJSON(T obj) {
        Log.d(JSONUTIL_TAG, "Inside convertToJSON");
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * Converts the specified JSON string to list of objects of the specified class.
     *
     * @param jsonString JSON string to be transformed to a list of objects of specified type.
     * @param t          Type of the list of objects to which JSON string needs to be transformed.
     * @return List of objects of the specified type, null if the string is not a valid representation of the type of the object, or null or empty.
     */
    public static <T> List<T> convertToObjectList(String jsonString, Type t) {
        Log.d(JSONUTIL_TAG, "Inside convertToObjectList");
        List<T> objList = null;
        try {
            objList = new GsonBuilder().create().fromJson(jsonString, t);
        } catch (JsonSyntaxException e) {
            Log.e(JSONUTIL_TAG, "JsonSyntaxException while parsing JSON string - msg: " + e.getMessage());

        } catch (JsonParseException e) {
            Log.e(JSONUTIL_TAG, "JsonParseException while parsing JSON string - msg: " + e.getMessage());
        }
        return objList;
    }


}
