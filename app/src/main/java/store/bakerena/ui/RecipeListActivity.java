package store.bakerena.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.IdlingResource.SimpleIdlingResource;
import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.impl.RecipeDetailsHandler;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.utils.BakerenaUtils;
import store.bakerena.utils.DisplayUtils;
import store.bakerena.utils.NetworkUtils;

/**
 * Activity displaying the list of available Baking Recipes.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, RecipeSelectionCallback {

    private static final String RECIPE_LIST_ACTIVITY_TAG = RecipeListActivity.class.getName();

    @BindView(R.id.recipe_recycler_view_parent)
    View recyclerViewParent;

    @BindView(R.id.progress_bar_frame)
    View progressBarFrame;

    @BindView(R.id.recipe_list)
    RecyclerView recyclerView;

    @BindView((R.id.toolbar))
    Toolbar toolbar;

    private RecipeListRecyclerViewAdapter recipeListRecyclerViewAdapter;

    private AlertDialog alertDialog;

    private List<Recipe> recipes;

    private static final int RECIPES_LOADER = 10;

    private int recipeRecyclerViewScrollPosition;

    @Nullable
    private SimpleIdlingResource idlingResource;

    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(BakerenaConstants.BUNDLE_KEY_RECIPE_LIST_DATA)) {
            recipes = savedInstanceState.getParcelableArrayList(BakerenaConstants.BUNDLE_KEY_RECIPE_LIST_DATA);
            recipeRecyclerViewScrollPosition = savedInstanceState.getInt(BakerenaConstants.BUNDLE_KEY_RECIPE_RV_SCROLL_POSITION);
            Log.d(RECIPE_LIST_ACTIVITY_TAG, "savedInstanceState exists");
            Log.d(RECIPE_LIST_ACTIVITY_TAG, recipes.toString());
        } else {
            Log.d(RECIPE_LIST_ACTIVITY_TAG, "savedInstanceState does not exist");
            recipes = new ArrayList<>();
        }

        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle(R.string.title_activity_recipe_list);
        }

        setupRecyclerView(recyclerView);
        recyclerView.scrollToPosition(recipeRecyclerViewScrollPosition);

        getIdlingResource();
    }

    /**
     * Configures the recycler view and sets the padding on recycler view parent to center the recycler view.
     *
     * @param recyclerView Recycler View that serves as the container for the Recipes sourced from Content Api
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside setupRecyclerView - Start");

        recyclerView.setPadding(12, 8, 0, 0);

        int screenWidth;
        int defaultImgPixelWidth = DisplayUtils.getPixelValue(this, BakerenaConstants.DEFAULT_GRID_IMG_WIDTH);

        Map<String, Integer> gridParams;
        screenWidth = DisplayUtils.getScreenWidth(getWindowManager());
        gridParams = DisplayUtils.computeGridColCountAndMargin(screenWidth, defaultImgPixelWidth, DisplayUtils.isLandscape(this));


        int gridColCount = gridParams.get(BakerenaConstants.GRID_COL_COUNT);
        int gridMargin = gridParams.get(BakerenaConstants.GRID_MARGIN);

        Log.d(RECIPE_LIST_ACTIVITY_TAG, "screenWidth: " + screenWidth);
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "defaultImgPixelWidth: " + defaultImgPixelWidth);
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "gridColCount: " + gridColCount);
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "gridMargin: " + gridMargin);

        recipeListRecyclerViewAdapter = new RecipeListRecyclerViewAdapter(recipes, this);

        recyclerViewParent.setPadding(gridMargin, 0, gridMargin, 0);

        recyclerView.setAdapter(recipeListRecyclerViewAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, gridColCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recipeRecyclerViewScrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
            }
        });

    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside onResume: ");
        if (recipes == null || recipes.isEmpty()) {
            fetchRecipes();
        }
    }

    /**
     * Initiates the fetch of Baking Recipes using the Bakerena API through an AsyncTaskLoader.
     */
    private void fetchRecipes() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> recipesLoader = loaderManager.getLoader(RECIPES_LOADER);
        if (recipesLoader == null) {
            loaderManager.initLoader(RECIPES_LOADER, new Bundle(), this);
        } else {
            loaderManager.restartLoader(RECIPES_LOADER, new Bundle(), this);
        }

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            /* Raw JSON from the results of fetching of Baking Recipes  */
            String bakingRecipesJson;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                // Show the loading indicator
                progressBarFrame.setVisibility(View.VISIBLE);

                // Set Idle state to false for IdlingResource as the network call is about to be initiated
                if (idlingResource != null) {
                    idlingResource.setIdleState(false);
                }

                /*
                 * If we already have cached results, just deliver them now. If we don't have any
                 * cached results, force a load.
                 */
                if (bakingRecipesJson != null) {
                    Log.d(RECIPE_LIST_ACTIVITY_TAG, "Loader Cache getting used.");
                    deliverResult(bakingRecipesJson);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                RecipeDetailsHandler recipeDetailsHandler = new RecipeDetailsHandler();
                return recipeDetailsHandler.fetchRecipeDetailsAsJSON();
            }

            @Override
            public void deliverResult(String bakingRecipesJson) {
                this.bakingRecipesJson = bakingRecipesJson;
                super.deliverResult(bakingRecipesJson);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        // Response received from API, update the display
        updateDisplay(data);

        if (idlingResource != null) {
            idlingResource.setIdleState(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    /**
     * Updates Display based on the response received after querying the API for fetching the baking recipes and performs miscellaneous tasks as below
     * <br/>
     * 1. In case of valid response, the recycler view is updated and the JSON response is saved to SharedPreferences for offline use.
     * 2. In case of invalid response, it is seen if network is available or not.
     * 3. In case of a network issue, a cached JSON from SharedPreferences is fetched if it exists and appropriate message is shown to user.
     * 4. In case no cached JSON is present, an alert dialog is shown with links to network settings and appropriate message.
     * 5. If network is available but invalid response is received, ContentAPI failure message is shown to the user.
     *
     * @param bakingRecipesJson JSON response containing baking recipe details
     */
    private void updateDisplay(String bakingRecipesJson) {
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay");

        if (bakingRecipesJson != null && !bakingRecipesJson.isEmpty()) {
            Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay - Received Response");
            progressBarFrame.setVisibility(View.GONE);

            RecipeDetailsHandler recipeDetailsHandler = new RecipeDetailsHandler();

            // Important that the cached AsyncTaskLoader response is passed on to the member variable - recipes
            recipes = recipeDetailsHandler.fetchRecipeDetailsFromJSON(bakingRecipesJson);

            recipeListRecyclerViewAdapter.setRecipes(recipes);
            recipeListRecyclerViewAdapter.notifyDataSetChanged();

            BakerenaUtils.saveBakerenaJsonToSharedPrefs(bakingRecipesJson, this);
        } else {
            Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay - No Response Received");
            if (!NetworkUtils.isNetworkAvailable(this)) {
                String recipesJsonFromSharedPrefs = BakerenaUtils.readBakerenaJsonFromSharedPrefs(this);

                if (recipesJsonFromSharedPrefs != null) {
                    Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay - Network not available - readBakerenaJsonFromSharedPrefs returned saved JSON");
                    Toast.makeText(this, getString(R.string.no_network_show_last_saved), Toast.LENGTH_LONG).show();

                    progressBarFrame.setVisibility(View.GONE);
                    RecipeDetailsHandler recipeDetailsHandler = new RecipeDetailsHandler();
                    recipes = recipeDetailsHandler.fetchRecipeDetailsFromJSON(recipesJsonFromSharedPrefs);
                    recipeListRecyclerViewAdapter.setRecipes(recipes);
                    recipeListRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay - Network not available - readBakerenaJsonFromSharedPrefs did not find a saved JSON");
                    createAlertDialog(getString(R.string.alertdlg_title_no_network), getString(R.string.alertdlg_content_no_network), true);
                    Toast.makeText(this, getString(R.string.alertdlg_title_no_network), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside updateDisplay - Network available - ContentAPI failure");
                createAlertDialog(getString(R.string.contentapi_failure_title), getString(R.string.contentapi_failure_msg), false);
            }
        }
    }

    /**
     * Alert Dialog shown to the user in case of Content API failure or network issues.
     *
     * @param title            Title of Alert Dialog
     * @param alertContent     Content of the Alert Dialog
     * @param isNetworkFailure true in case of Network Failure, false otherwise.
     */
    private void createAlertDialog(String title, String alertContent, boolean isNetworkFailure) {
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside createDialog ...");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title).setMessage(alertContent);


        builder.setPositiveButton(getString(R.string.nw_fail_retry), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fetchRecipes();
            }
        });
        if (isNetworkFailure) {
            builder.setNeutralButton(getString(R.string.nw_fail_netsettings), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent networkSettings = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(networkSettings);
                }
            });
        }
        builder.setNegativeButton(getString(R.string.nw_fail_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRecipeSelection(Recipe recipe) {
        Intent intent = new Intent(this, RecipeStepListActivity.class);
        intent.putExtra(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, recipe);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BakerenaConstants.BUNDLE_KEY_RECIPE_LIST_DATA, (ArrayList<? extends Parcelable>) recipes);
        outState.putInt(BakerenaConstants.BUNDLE_KEY_RECIPE_RV_SCROLL_POSITION, recipeRecyclerViewScrollPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(RECIPE_LIST_ACTIVITY_TAG, "Inside onDestroy");
    }
}
