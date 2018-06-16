package store.bakerena.ui;

import store.bakerena.contentapi.model.Step;

/**
 * Interface having callback methods relating to Recipe Steps details and their navigation
 *
 * @version 1.0
 * @author Ketan Damle
 */
interface RecipeStepSelectionCallback {

    /**
     * Callback method meant to be invoked on selection of a Recipe Step.
     *
     * @param recipeStep Selected Recipe Step.
     */
    void onRecipeStepSelection(Step recipeStep);

}
