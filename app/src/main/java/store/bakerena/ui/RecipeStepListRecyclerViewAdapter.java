package store.bakerena.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Step;

/**
 * Adapter for the recycler view holding the Steps of a chosen Bakerena Recipe.
 *
 * @author Ketan Damle
 * @version 1.0
 */

public class RecipeStepListRecyclerViewAdapter extends RecyclerView.Adapter<RecipeStepListRecyclerViewAdapter.ViewHolder> {

    private List<Step> steps;
    private final RecipeStepSelectionCallback callback;

    public RecipeStepListRecyclerViewAdapter(List<Step> steps, RecipeStepSelectionCallback callback) {
        this.steps = steps;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(steps.get(position));
    }

    @Override
    public int getItemCount() {
        if (steps != null && !steps.isEmpty()) {
            return steps.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_step_image)
        ImageView recipeStepImage;

        @BindView(R.id.recipe_step_shortdesc)
        TextView stepShortDesc;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Step step) {

            this.stepShortDesc.setText(step.getShortDescription());

            if (Build.VERSION.SDK_INT <= 20) {
                stepShortDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemView.getResources().getInteger(R.integer.recipe_step_shortdesc_fonsize_below_v20));
            }

            // Set dynamic content description
            recipeStepImage.setContentDescription(itemView.getContext().getString(R.string.recipe_step_thumbnail_image_contentdesc) + step.getShortDescription());

            String thumbnailURL = step.getThumbnailURL();
            // Incorrect images with MP4 extensions are also being filtered early, as Picasso onError handling of visibility toggling is not smooth and with a slight delay.
            if (StringUtils.isNotEmpty(thumbnailURL) && (thumbnailURL.endsWith(BakerenaConstants.MP4_FILE_EXTENSION))) {
                thumbnailURL = "";
            }

            /*
             * Placeholders not set here, as empty thumbnail URI leads to exception, and neither normal or error placeholders are set.
             */
            try {
                Picasso.with(itemView.getContext()).load(thumbnailURL).into(recipeStepImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        recipeStepImage.setVisibility(View.VISIBLE);
                        stepShortDesc.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        recipeStepImage.setVisibility(View.GONE);
                        stepShortDesc.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                recipeStepImage.setVisibility(View.GONE);
                stepShortDesc.setVisibility(View.VISIBLE);
            }

            this.stepShortDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRecipeStepSelection(step);
                }
            });

            this.recipeStepImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRecipeStepSelection(step);
                }
            });
        }
    }
}
