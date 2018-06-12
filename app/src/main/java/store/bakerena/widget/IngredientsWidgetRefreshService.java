package store.bakerena.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import store.bakerena.R;

/**
 * Intent Service to be invoked in case of a change in the favorite recipe.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class IngredientsWidgetRefreshService extends IntentService {

    private static final String ACTION_UPDATE_INGREDIENTS_WIDGETS = "ACTION_UPDATE_INGREDIENTS_WIDGETS";

    public IngredientsWidgetRefreshService() {
        super(IngredientsWidgetRefreshService.class.getName());
    }

    /**
     * Initiates or starts the Intent Service to nake updates to the app widget.
     *
     * @param context Context for starting the Intent Service
     */
    public static void startActionUpdateIngredientsWidgets(Context context){
        Intent intent = new Intent(context, IngredientsWidgetRefreshService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(ACTION_UPDATE_INGREDIENTS_WIDGETS.equals(action)){
                handleActionUpdateIngredientsWidgets();
            }
        }
    }

    /**
     * Updates the Ingredients Widget.
     */
    private void handleActionUpdateIngredientsWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeIngredientsWidget.class));

        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredients_widget_list);

        //Now update all widgets
        RecipeIngredientsWidget.updateAllAppWidgets(this, appWidgetManager, appWidgetIds);
    }
}
