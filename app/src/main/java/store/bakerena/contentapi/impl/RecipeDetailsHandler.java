package store.bakerena.contentapi.impl;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import store.bakerena.BuildConfig;
import store.bakerena.contentapi.model.Recipe;

/**
 * Implementation class that handles calls to the Bakerena Recipe API.
 *
 * @version 1.0
 * @author Ketan Damle
 */

public class RecipeDetailsHandler {

    private static final String RECIPE_DETAILS_HANDLER_TAG = "RecipeDetailsHandler";

    public String fetchRecipeDetailsAsJSON(){
        Log.d(RECIPE_DETAILS_HANDLER_TAG, "Inside fetchRecipeDetailsAsJSON - Start");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(BuildConfig.BAKERENA_API_URL).build();

        Call call = client.newCall(request);

        String recipeDetailsAsJSON = null;
        try {
            Response response = call.execute();
            recipeDetailsAsJSON = response.isSuccessful() ? ((response.body() != null) ? response.body().string() : null) : null;
            if (recipeDetailsAsJSON == null) {
                Log.e(RECIPE_DETAILS_HANDLER_TAG, "Bakerena Api Response is not successful, Response Code: " + response.code());
            }
        } catch (IOException e) {
            Log.e(RECIPE_DETAILS_HANDLER_TAG, "Bakerena Api Response Failed - "+e.getMessage());
        }

        Log.d(RECIPE_DETAILS_HANDLER_TAG, "Inside fetchRecipeDetailsAsJSON - End");
        return recipeDetailsAsJSON;
    }

    public List<Recipe> fetchRecipeDetailsFromJSON(String recipeDetailsJSON){
        Log.d(RECIPE_DETAILS_HANDLER_TAG, "Inside fetchRecipeDetailsFromJSON - Start");
        List<Recipe> recipes = new GsonBuilder().create().fromJson(recipeDetailsJSON, new TypeToken<List<Recipe>>(){}.getType());
        Log.d(RECIPE_DETAILS_HANDLER_TAG, "Inside fetchRecipeDetailsFromJSON - End");
        return recipes;
    }
}
