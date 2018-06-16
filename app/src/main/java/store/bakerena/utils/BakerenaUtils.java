package store.bakerena.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Ingredient;
import store.bakerena.contentapi.model.Recipe;

/**
 * General Utility class for the application.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class BakerenaUtils {

    private static final String BAKERENA_UTILS_TAG = BakerenaUtils.class.getName();

    /**
     * Computes the formatted ingredient details based on its name, quantity and measure.
     * <br/> E.g. 400 G sifted cake flour
     *
     * @param ingredient Ingredient whose formatted details are needed.
     * @return Formatted Ingredient Details
     */
    public static String getIngredientText(Ingredient ingredient) {
        return ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getIngredient();
    }

    /**
     * Computes the formatted Servings details for a Recipe.
     * <br/>E.g. Servings - 8
     *
     * @param recipe  Recipe for which Servings details need to be computed
     * @param context Context reference for computation
     * @return Formatted Servings Details
     */
    public static String getServingsText(Recipe recipe, Context context) {
        return context.getString(R.string.servings_text_prefix, String.valueOf(recipe.getServings()));
    }

    /**
     * Reads the stored Bakerena Recipes as JSON from the SharedPreferences.
     *
     * @param context Context for reading from SharedPreferences
     * @return JSON as a string representing all the recipe details
     */
    public static String readBakerenaJsonFromSharedPrefs(Context context) {
        Log.d(BAKERENA_UTILS_TAG, "Inside readBakerenaJsonFromSharedPrefs");
        SharedPreferences sharedPreferences = context.getSharedPreferences(BakerenaConstants.BAKERENA_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(BakerenaConstants.PREFERENCE_ALL_RECIPES_RAW_JSON_KEY, null);
    }

    /**
     * Saves the Bakerena Recipes as a String, that are received in form of a JSON from the Content API.
     *
     * @param bakingRecipesJson Baking Recipes JSON
     * @param context           Context for saving SharedPreferences
     */
    public static void saveBakerenaJsonToSharedPrefs(String bakingRecipesJson, Context context) {
        Log.d(BAKERENA_UTILS_TAG, "Inside saveBakerenaJsonToSharedPrefs");
        SharedPreferences sharedPreferences = context.getSharedPreferences(BakerenaConstants.BAKERENA_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BakerenaConstants.PREFERENCE_ALL_RECIPES_RAW_JSON_KEY, bakingRecipesJson);
        editor.apply();
    }

    /**
     * Reads the stored Favorite Bakerena Recipe from the SharedPreferences.
     *
     * @param context Context for reading from the SharedPreferences
     * @return {@link Recipe} object representing all the  favorite recipe details, and null, in case no favorites exist.
     */
    public static Recipe readFavoriteRecipeFromSharedPrefs(Context context) {
        Log.d(BAKERENA_UTILS_TAG, "Inside readFavoriteRecipeFromSharedPrefs");
        SharedPreferences sharedPreferences = context.getSharedPreferences(BakerenaConstants.PREFERENCE_FAVORITE_RECIPE_JSON, Context.MODE_PRIVATE);
        String favoriteRecipeJSON = sharedPreferences.getString(BakerenaConstants.PREFERENCE_FAVORITE_RECIPE_JSON, null);
        Recipe favoriteRecipe = JsonUtil.convertToObject(favoriteRecipeJSON, Recipe.class);
        Log.d(BAKERENA_UTILS_TAG, "Favorite Recipe - " + favoriteRecipe);
        return favoriteRecipe;
    }

    /**
     * Saves the Recipe marked as favorite, as a JSON string in the SharedPreferences.
     *
     * @param recipe  Favorite recipe represented by {@link Recipe} object.
     * @param context Context for saving to SharedPreferences.
     */
    public static void saveFavoriteRecipeToSharedPrefs(Recipe recipe, Context context) {
        Log.d(BAKERENA_UTILS_TAG, "Inside saveFavoriteRecipeToSharedPrefs");
        String favoriteRecipeJSON = JsonUtil.convertToJSON(recipe);
        Log.d(BAKERENA_UTILS_TAG, "Favorite Recipe as JSON - " + favoriteRecipeJSON);
        SharedPreferences sharedPreferences = context.getSharedPreferences(BakerenaConstants.PREFERENCE_FAVORITE_RECIPE_JSON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BakerenaConstants.PREFERENCE_FAVORITE_RECIPE_JSON, favoriteRecipeJSON);
        editor.apply();
    }
}
