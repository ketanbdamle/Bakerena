package store.bakerena.ui;

import store.bakerena.contentapi.model.Recipe;

/**
 * Interface having callback methods to be invoked on Recipe Selection.
 *
 * @version 1.0
 * @author Ketan Damle
 */
interface RecipeSelectionCallback {

    /**
     * Callback method to be invoked upon Recipe Selection on {@link RecipeListActivity}.
     *
     * @param recipe Selected Recipe
     */
    void onRecipeSelection(Recipe recipe);

}
