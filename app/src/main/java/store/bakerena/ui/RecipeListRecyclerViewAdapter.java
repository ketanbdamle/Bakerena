package store.bakerena.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.contentapi.model.Recipe;

/**
 * Adapter for the recycler view holding the Bakerena Recipes.
 *
 * @author Ketan Damle
 * @version 1.0
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
        if (recipes != null && !recipes.isEmpty()) {
            return recipes.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_image)
        ImageView recipeImage;

        @BindView(R.id.recipe_name)
        TextView recipeName;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final Recipe recipe) {
            this.recipeName.setText(recipe.getName());

            final String recipeImageURL = recipe.getImage();

            // Set dynamic content description
            recipeImage.setContentDescription(itemView.getContext().getString(R.string.recipe_image_contentdesc) + recipe.getName());
            /*
             * Placeholders not set here, as empty recipeImageURL leads to exception, and neither normal or error placeholders are set.
             */
            try {
                Picasso.with(itemView.getContext()).load(recipeImageURL).into(recipeImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        recipeImage.setVisibility(View.VISIBLE);
                        recipeName.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        recipeImage.setVisibility(View.GONE);
                        recipeName.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                recipeImage.setVisibility(View.GONE);
                recipeName.setVisibility(View.VISIBLE);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRecipeSelection(recipe);
                }
            });

            if (Build.VERSION.SDK_INT <= 20) {
                recipeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemView.getResources().getInteger(R.integer.recipe_name_fonsize_below_v20));
            }
        }

    }
}
