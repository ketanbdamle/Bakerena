package store.bakerena.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import store.bakerena.R;
import store.bakerena.contentapi.model.Ingredient;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.utils.BakerenaUtils;

/**
 * Adapter to display the list of ingredients via a ListView in the app widget.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class IngredientsListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsListRemoteViewsFactory(this.getApplicationContext());
    }
}

class IngredientsListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context appContext;
    private List<Ingredient> ingredients;

    public IngredientsListRemoteViewsFactory(Context applicationContext) {
        this.appContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Recipe favoriteRecipe = BakerenaUtils.readFavoriteRecipeFromSharedPrefs(appContext);
        if(favoriteRecipe !=null){
            ingredients = favoriteRecipe.getIngredients();
        }
    }

    @Override
    public void onDestroy() {
        ingredients = null;
    }

    @Override
    public int getCount() {
        return ingredients == null ? 0 : ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(appContext.getPackageName(), R.layout.ingredients_widget_item);

        views.setTextViewText(R.id.ingredient_description, BakerenaUtils.getIngredientText(ingredients.get(position)));

        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.ingredient_description, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
