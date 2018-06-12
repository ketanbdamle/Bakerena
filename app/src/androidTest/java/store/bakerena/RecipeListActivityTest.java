package store.bakerena;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import store.bakerena.contentapi.model.Recipe;
import store.bakerena.ui.RecipeListActivity;
import store.bakerena.ui.RecipeListRecyclerViewAdapter;
import store.bakerena.utils.BakerenaUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test class to test {@link RecipeListActivity}.
 *
 * @author Ketan Damle
 * @version 1.0
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeListActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<RecipeListActivity> activityTestRule = new ActivityTestRule<>(RecipeListActivity.class);

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    /**
     * Tests whether the Recipees (names) are properly displayed inside the reycler view.
     */
    @Test
    public void checkRecipeListDisplayed() {
        onView(ViewMatchers.withId(R.id.recipe_list))
                .perform(RecyclerViewActions.<RecipeListRecyclerViewAdapter.ViewHolder>scrollToPosition(1));

        List<Recipe> recipes = new ArrayList<>(activityTestRule.getActivity().getRecipes());
        int i=0;
        for(Recipe recipe : recipes) {
            String recipeName = recipe.getName();
            onView(ViewMatchers.withId(R.id.recipe_list))
                    .perform(RecyclerViewActions.<RecipeListRecyclerViewAdapter.ViewHolder>scrollToPosition(i));
            onView(withText(recipeName)).check(matches(isDisplayed()));
            i++;
        }

        String dummyInvalidRecipeName = "Brownies1";

        onView(withText(dummyInvalidRecipeName)).check(doesNotExist());
    }

    /**
     * Tests whether the Servings text is properly displayed.
     */
    @Test
    public void checkServingsTextDisplayed() {
        onView(ViewMatchers.withId(R.id.recipe_list))
                .perform(RecyclerViewActions.<RecipeListRecyclerViewAdapter.ViewHolder>actionOnItemAtPosition(0, click()));

        List<Recipe> recipes = new ArrayList<>(activityTestRule.getActivity().getRecipes());

        String servingsText = BakerenaUtils.getServingsText(recipes.get(0), activityTestRule.getActivity());

        onView(withText(servingsText)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether the recycler views for Ingredients and Recipe Steps are displayed after a Recipe is selected.
     */
    @Test
    public void checkIngredientsAndStepsRecyclerViews() {
        onView(ViewMatchers.withId(R.id.recipe_list))
                .perform(RecyclerViewActions.<RecipeListRecyclerViewAdapter.ViewHolder>actionOnItemAtPosition(0, click()));

        onView(withId(R.id.ingredients_recyclerview)).check(matches(isDisplayed()));

        onView(withId(R.id.recipestep_recyclerview)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
