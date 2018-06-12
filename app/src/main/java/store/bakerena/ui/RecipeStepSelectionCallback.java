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

    /**
     * Callback method meant to be invoked on button to navigate to a particular Recipe Step.
     *
     * @param stepPosition Step index of the Recipe Step one wishes to navigate to.
     */
    void onGoToStep(int stepPosition);
}
