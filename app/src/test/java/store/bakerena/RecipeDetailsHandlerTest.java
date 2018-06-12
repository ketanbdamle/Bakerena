package store.bakerena;


import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import store.bakerena.contentapi.impl.RecipeDetailsHandler;
import store.bakerena.contentapi.model.Recipe;

/**
 * Test Class to test Bakerena  API calls
 *
 * @version 1.0
 * @author Ketan Damle
 */
public class RecipeDetailsHandlerTest {

    /**
     * Tests fetch of Recipes from the Bakerena Content API as a JSON object
     */
    @Test
    public void testFetchRecipeDetailsAsJSON(){
        RecipeDetailsHandler recipeDetailsHandler = new RecipeDetailsHandler();
        String recipeDetailsAsJSON = recipeDetailsHandler.fetchRecipeDetailsAsJSON();
        Assert.assertNotNull(recipeDetailsAsJSON);
    }

    /**
     * Tests the conversion of Bakerena JSON to a java object representation in form of list of {@link Recipe}s.
     */
    @Test
    public void testFetchRecipeDetailsFromJSON(){

        RecipeDetailsHandler recipeDetailsHandler = new RecipeDetailsHandler();
        String recipeDetailsAsJSON = recipeDetailsHandler.fetchRecipeDetailsAsJSON();

        List<Recipe> recipes = recipeDetailsHandler.fetchRecipeDetailsFromJSON(recipeDetailsAsJSON);
        Assert.assertNotNull(recipes);
        Assert.assertTrue(recipes.size() > 0);
    }
}
