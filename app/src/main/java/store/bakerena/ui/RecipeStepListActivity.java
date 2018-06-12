package store.bakerena.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.contentapi.model.Step;
import store.bakerena.utils.BakerenaUtils;
import store.bakerena.widget.IngredientsWidgetRefreshService;

/**
 * An activity representing a lists of Recipe Ingredients and Steps. This activity
 * has different presentations for handset and tablet-size devices.
 *
 * <br/><br/>On handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing item details.
 *
 * <br/> <br/>On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class RecipeStepListActivity extends AppCompatActivity implements RecipeStepSelectionCallback{

    private static final String RECIPE_STEP_LIST_ACTIVITY_TAG = RecipeStepListActivity.class.getName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ingredients_recyclerview)
    RecyclerView ingredientsRecyclerView;

    @BindView(R.id.servings_text)
    TextView servings;

    @BindView(R.id.recipestep_recyclerview)
    RecyclerView recipeStepRecyclerView;

    // Current selected Recipe Details
    private Recipe selectedRecipe;

    // Favorite recipe Details
    private Recipe favoriteRecipe;

    // Ingredients RecyclerView Scroll Position
    private int ingredientsRVScrollPosition;

    // RecipeStep RecyclerView Scroll Position
    private int recipeStepRVScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(RECIPE_STEP_LIST_ACTIVITY_TAG, "Inside RecipeStepListActivity - onCreate");
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS)) {
            selectedRecipe = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            favoriteRecipe = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE);
            ingredientsRVScrollPosition = savedInstanceState.getInt(BakerenaConstants.BUNDLE_KEY_INGREDIENTS_RV_SCROLL_POSITION);
            recipeStepRVScrollPosition = savedInstanceState.getInt(BakerenaConstants.BUNDLE_KEY_RECIPESTEP_RV_SCROLL_POSITION);
        }
        else {
            selectedRecipe = getIntent().getParcelableExtra(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            favoriteRecipe = getIntent().getParcelableExtra(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE);
        }

        setContentView(R.layout.activity_recipestep_list);
        ButterKnife.bind(this);


        if (findViewById(R.id.recipe_step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if(mTwoPane){
            onRecipeStepSelection(selectedRecipe.getSteps().get(recipeStepRVScrollPosition));
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(selectedRecipe.getName());
        }

        setupIngredientsListRecyclerView();

        servings.setText(BakerenaUtils.getServingsText(selectedRecipe, this));

        setupRecipeStepListRecyclerView();
    }

    /**
     * Sets up a 'Vertical' recycler view supporting page style snapping to display the list of Recipe Ingredients.
     * <br/><br/>
     * <u>Credits</u><br/>
     * 1. https://stackoverflow.com/questions/43766324/how-to-get-current-position-in-horizontal-recyclerview?rq=1<br/><br/>
     * 2. https://stackoverflow.com/questions/46653770/android-get-current-position-from-recyclerview-with-listener-when-position-cha
     */
    private void setupIngredientsListRecyclerView() {
        IngredientsListRecyclerViewAdapter ingredientsListRecyclerViewAdapter = new IngredientsListRecyclerViewAdapter(selectedRecipe.getIngredients());
        ingredientsRecyclerView.setAdapter(ingredientsListRecyclerViewAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ingredientsRecyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(ingredientsRecyclerView);
        ingredientsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ingredientsRVScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
            }
        });
        ingredientsRecyclerView.scrollToPosition(ingredientsRVScrollPosition);
    }

    /**
     * Sets up a 'Horizontal' recycler view supporting page style snapping to display the list of Recipe steps by their short descriptions.
     * <br/><br/>
     * <u>Credits</u><br/>
     * 1. https://stackoverflow.com/questions/43766324/how-to-get-current-position-in-horizontal-recyclerview?rq=1<br/><br/>
     * 2. https://stackoverflow.com/questions/46653770/android-get-current-position-from-recyclerview-with-listener-when-position-cha
     */
    private void setupRecipeStepListRecyclerView() {
        RecipeStepListRecyclerViewAdapter recipeStepListRecyclerViewAdapter = new RecipeStepListRecyclerViewAdapter(selectedRecipe.getSteps(), this);
        recipeStepRecyclerView.setAdapter(recipeStepListRecyclerViewAdapter);
        recipeStepRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recipeStepRecyclerView);
        recipeStepRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int horizontalOffset = recyclerView.computeHorizontalScrollOffset();
                final int recyclerViewChildWidth = recyclerView.getChildAt(0).getMeasuredWidth();
                if(horizontalOffset % recyclerViewChildWidth == 0){
                    recipeStepRVScrollPosition = horizontalOffset / recyclerViewChildWidth;
                }
            }
        });
        recipeStepRecyclerView.scrollToPosition(recipeStepRVScrollPosition);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipestep_list, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if(favoriteRecipe == null){
            favoriteRecipe = BakerenaUtils.readFavoriteRecipeFromSharedPrefs(this);
        }
        if(favoriteRecipe!=null && favoriteRecipe.getId() == selectedRecipe.getId()){
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_favorite));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
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

            case R.id.action_favorite:
                if(favoriteRecipe == null) {
                    BakerenaUtils.saveFavoriteRecipeToSharedPrefs(selectedRecipe, this);
                    favoriteRecipe = selectedRecipe;
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_favorite));
                    Snackbar.make(getCurrentFocus(), getString(R.string.favorite_recipe_added_msg, selectedRecipe.getName()), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    IngredientsWidgetRefreshService.startActionUpdateIngredientsWidgets(this);
                }
                else{
                    int id = favoriteRecipe.getId();
                    if(id != selectedRecipe.getId()){
                        String oldFavoriteRecipe = favoriteRecipe.getName();
                        favoriteRecipe = selectedRecipe;
                        BakerenaUtils.saveFavoriteRecipeToSharedPrefs(selectedRecipe, this);
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_favorite));
                        Snackbar.make(getCurrentFocus(), getString(R.string.favorite_recipe_replaced_msg, selectedRecipe.getName(), oldFavoriteRecipe), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        IngredientsWidgetRefreshService.startActionUpdateIngredientsWidgets(this);
                    }
                    else{
                        Snackbar.make(getCurrentFocus(), getString(R.string.favorite_recipe_already_msg, selectedRecipe.getName()), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
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
        outState.putInt(BakerenaConstants.BUNDLE_KEY_INGREDIENTS_RV_SCROLL_POSITION, ingredientsRVScrollPosition);
        outState.putInt(BakerenaConstants.BUNDLE_KEY_RECIPESTEP_RV_SCROLL_POSITION, recipeStepRVScrollPosition);
    }

    @Override
    public void onRecipeStepSelection(Step recipeStep) {
        Log.d(RECIPE_STEP_LIST_ACTIVITY_TAG, "Inside onRecipeStepSelection .. ");
        Log.d(RECIPE_STEP_LIST_ACTIVITY_TAG, "mTwoPane : "+ mTwoPane);
        recipeStepRVScrollPosition = selectedRecipe.getSteps().indexOf(recipeStep);
        Bundle arguments = new Bundle();
        arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, selectedRecipe);
        arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE, favoriteRecipe);
        arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAILS, recipeStep);
        if (mTwoPane) {
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

    @Override
    public void onGoToStep(int stepPosition) {
        recipeStepRecyclerView.scrollToPosition(stepPosition);
    }
}
