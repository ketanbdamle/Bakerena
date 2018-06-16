package store.bakerena.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.ui.RecipeStepListActivity;
import store.bakerena.utils.BakerenaUtils;

/**
 * Implementation of Bakerena Widget which shows list of ingredients of the Favorite Recipe.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class RecipeIngredientsWidget extends AppWidgetProvider {

    /**
     * Fetches the RemoteViews and updates the widget
     *
     * @param context          Context for computation
     * @param appWidgetManager {@link AppWidgetManager} through which the widget is updated.
     * @param appWidgetId      App Widget Id of the widget to be updated.
     */
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = getIngredientsListRemoteView(context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Creates the RemoteViews for the list of ingredients of the chosen Favorite Recipe.
     *
     * @param context Context reference for computation
     * @return RemoteViews for the list of ingredients of the chosen Favorite Recipe
     */
    private static RemoteViews getIngredientsListRemoteView(Context context) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);

        // Read favorite recipe from SharedPreferences
        Recipe favoriteRecipe = BakerenaUtils.readFavoriteRecipeFromSharedPrefs(context);

        // Set the IngredientsListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, IngredientsListWidgetService.class);

        views.setRemoteAdapter(R.id.ingredients_widget_list, intent);

        if (favoriteRecipe != null) {
            // Set the RecipeStepListActivity intent to launch when clicked on an ingredient.
            Intent appIntent = new Intent(context, RecipeStepListActivity.class);
            appIntent.putExtra(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, favoriteRecipe);
            appIntent.putExtra(BakerenaConstants.BUNDLE_KEY_FAVORITE_RECIPE, favoriteRecipe);

            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.ingredients_widget_list, appPendingIntent);

            views.setTextViewText(R.id.favorite_recipe_name, favoriteRecipe.getName());
        }

        // Handles no favorite recipe scenario
        views.setEmptyView(R.id.ingredients_widget_list, R.id.empty_view);


        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateAllAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Updates all the widgets for the Bakerena App.
     *
     * @param context Context reference for computation
     * @param appWidgetManager {@link AppWidgetManager} through which the widgets are updated.
     * @param appWidgetIds Widget Ids of the widgets to be updated.
     */
    public static void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

