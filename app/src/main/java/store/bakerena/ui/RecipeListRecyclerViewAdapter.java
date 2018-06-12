package store.bakerena.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.contentapi.model.Recipe;

/**
 * Adapter for the recycler view holding the Bakerena Recipes.
 *
 * @version 1.0
 * @author Ketan Damle
 */

public class RecipeListRecyclerViewAdapter extends RecyclerView.Adapter<RecipeListRecyclerViewAdapter.ViewHolder> {

    private List<Recipe> recipes;

    private final RecipeSelectionCallback callback;

    public RecipeListRecyclerViewAdapter(List<Recipe> recipes, RecipeSelectionCallback callback) {
        this.recipes = recipes;
        this.callback = callback;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        if(recipes!=null && !recipes.isEmpty()) {
            return recipes.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name)
        TextView recipeName;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final Recipe recipe) {
            this.recipeName.setText(recipe.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRecipeSelection(recipe);
                }
            });

            if(Build.VERSION.SDK_INT <=20){
                recipeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemView.getResources().getInteger(R.integer.recipe_name_fonsize_below_v20));
            }
        }

    }
}
