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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.contentapi.model.Step;
import store.bakerena.utils.BakerenaUtils;

/**
 * Adapter for the recycler view holding the Steps of a chosen Bakerena Recipe.
 *
 * @version 1.0
 * @author Ketan Damle
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
        holder.bind(steps.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(steps !=null && !steps.isEmpty()) {
            return steps.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_step_shortdesc)
        TextView stepShortDesc;

        @BindView(R.id.goToStep)
        ImageView goToStep;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Step step, final int position) {
            if(step.getId() == 0) {
                this.stepShortDesc.setText(step.getShortDescription());
            }
            else{
                this.stepShortDesc.setText(BakerenaUtils.getRecipeStepShortDesc(step, itemView.getContext()));
            }

            if(Build.VERSION.SDK_INT <=20){
                stepShortDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, itemView.getResources().getInteger(R.integer.recipe_step_shortdesc_fonsize_below_v20));
            }

            this.stepShortDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRecipeStepSelection(step);
                }
            });

            this.goToStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int totalSteps = getItemCount();
                    if(position < totalSteps - 1){
                        callback.onGoToStep(position +1);
                    }
                    else{
                        callback.onGoToStep(0);
                    }
                }
            });
        }
    }
}
