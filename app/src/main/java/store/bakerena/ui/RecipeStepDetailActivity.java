package store.bakerena.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.utils.DisplayUtils;

/**
 * An activity representing a single RecipeStep detail screen.
 * <br/><br/>This activity is only used on narrow width devices.
 * <br/><br/>On tablet-size devices, item details are presented side-by-side with a list of items in a {@link RecipeStepListActivity}.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class RecipeStepDetailActivity extends AppCompatActivity {

    private Recipe selectedRecipe;

    private Recipe favoriteRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_detail);
        if(!DisplayUtils.isLandscape(this)) {
            Toolbar toolbar = findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);

            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        if (savedInstanceState == null) {
            Bundle arguments = getIntent().getExtras();
            selectedRecipe = arguments.getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            favoriteRecipe = arguments.getParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE);

            // Create the detail fragment and add it to the activity using a fragment transaction.
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_step_detail_fragment_container, fragment)
                    .commit();
        }
        else{
            selectedRecipe = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            favoriteRecipe = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, selectedRecipe);
            arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE, favoriteRecipe);
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.putExtras(arguments);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, selectedRecipe);
        outState.putParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE, favoriteRecipe);
    }
}
