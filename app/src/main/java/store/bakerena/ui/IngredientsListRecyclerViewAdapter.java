package store.bakerena.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.contentapi.model.Ingredient;
import store.bakerena.utils.BakerenaUtils;

/**
 * Adapter for the recycler view holding the Ingredients of a chosen Bakerena Recipe.
 *
 * @version 1.0
 * @author Ketan Damle
 */

public class IngredientsListRecyclerViewAdapter extends RecyclerView.Adapter<IngredientsListRecyclerViewAdapter.ViewHolder> {

    private List<Ingredient> ingredients;

    public IngredientsListRecyclerViewAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredients_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        if(ingredients !=null && !ingredients.isEmpty()) {
            return ingredients.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient_text)
        TextView ingredientText;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Ingredient ingredient) {
            this.ingredientText.setText(BakerenaUtils.getIngredientText(ingredient));
        }
    }
}
