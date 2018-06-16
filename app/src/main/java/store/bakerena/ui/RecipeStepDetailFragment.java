package store.bakerena.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import store.bakerena.R;
import store.bakerena.common.BakerenaConstants;
import store.bakerena.contentapi.model.Recipe;
import store.bakerena.contentapi.model.Step;
import store.bakerena.utils.DisplayUtils;

import static android.support.constraint.Constraints.TAG;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity} in two-pane mode (on tablets)
 * or a {@link RecipeStepDetailActivity} on handsets.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class RecipeStepDetailFragment extends Fragment {

    private static final String RECIPE_STEP_DETAIL_FRAGMENT_TAG = RecipeStepDetailFragment.class.getName();

    @BindView(R.id.video_view_container)
    View videoViewContainer;

    @BindView(R.id.video_view)
    PlayerView playerView;

    @Nullable
    @BindView(R.id.recipe_thumbnail_image)
    ImageView recipeThumbnailImage;

    @Nullable
    @BindView(R.id.step_details_container)
    View stepDetailsContainer;

    @Nullable
    @BindView(R.id.recipe_step_shortdesc_text)
    TextView recipeStepShortDescText;

    @Nullable
    @BindView(R.id.recipe_step_detail_text)
    TextView recipeStepDetailsText;

    @Nullable
    @BindView(R.id.previous_step)
    ImageView previousStep;

    @Nullable
    @BindView(R.id.next_step)
    ImageView nextStep;

    private Recipe recipeDetails;
    private Step recipeStepDetails;
    private List<Step> allRecipeStepDetails;

    private SimpleExoPlayer player;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackStateCompatBuilder;
    private CustomPlayerListener customPlayerListener = new CustomPlayerListener();
    private Uri recipe_uri;

    private boolean mTwoPane;
    private String fragmentInvocationSource;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "savedInstanceState is null");
            recipeDetails = getArguments().getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            recipeStepDetails = getArguments().getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAILS);
            fragmentInvocationSource = getArguments().getString(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE);

            // Player state params are taken from parent activity if the fragment is part of a two-pane layout in both portrait and landscape mode.
            playbackPosition = getArguments().getLong(BakerenaConstants.BUNDLE_KEY_PLAYBACK_POSITION);
            currentWindow = getArguments().getInt(BakerenaConstants.BUNDLE_KEY_CURRENT_WINDOW);
            playWhenReady = getArguments().getBoolean(BakerenaConstants.BUNDLE_KEY_PLAY_WHEN_READY);
        } else {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "savedInstanceState is not null");
            recipeDetails = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS);
            recipeStepDetails = savedInstanceState.getParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAILS);
            fragmentInvocationSource = getArguments().getString(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE);

            playbackPosition = savedInstanceState.getLong(BakerenaConstants.BUNDLE_KEY_PLAYBACK_POSITION);
            currentWindow = savedInstanceState.getInt(BakerenaConstants.BUNDLE_KEY_CURRENT_WINDOW);
            playWhenReady = savedInstanceState.getBoolean(BakerenaConstants.BUNDLE_KEY_PLAY_WHEN_READY);
        }

        allRecipeStepDetails = recipeDetails.getSteps();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (getActivity().findViewById(R.id.recipe_step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "mTwoPane: " + mTwoPane);

        setContentViews();

        return rootView;
    }

    private void setContentViews() {
        String videoUrl = recipeStepDetails.getVideoURL();
        String thumbnailURL = recipeStepDetails.getThumbnailURL();

        // Determine Recipe Uri
        if (StringUtils.isEmpty(videoUrl)) {
            if (StringUtils.isNotEmpty(thumbnailURL) && !thumbnailURL.endsWith(BakerenaConstants.MP4_FILE_EXTENSION) && (!DisplayUtils.isLandscape(getContext()) || mTwoPane)) {
                showThumbnailInPlaceOfVideo();
            } else {
                // If video or thumbnail are not available, show the step details in full screen in landscape mode instead
                showStepDetailsInFullScreen();
            }
        } else {
            recipe_uri = Uri.parse(videoUrl);
        }

        // Show video in fullscreen in landscape mode, but not in master-detail layout on tablets
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet && !mTwoPane && StringUtils.isNotEmpty(videoUrl) && DisplayUtils.isLandscape(getContext())) {
            stepDetailsContainer.setVisibility(View.GONE);
            videoViewContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        }

        // Not all views or layouts have step navigation via these buttons, hence they could be null at times.
        if (previousStep != null && nextStep != null) {
            previousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentStepIndex = allRecipeStepDetails.indexOf(recipeStepDetails);
                    // Go back cyclically to the last step if the current step is the first one
                    int goToStepIndex = allRecipeStepDetails.size() - 1;
                    if (currentStepIndex > 0) {
                        goToStepIndex = currentStepIndex - 1;
                    }
                    goToStep(goToStepIndex);
                }
            });

            nextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentStepIndex = allRecipeStepDetails.indexOf(recipeStepDetails);
                    // Go cyclically to the first step if the current step is the last one
                    int goToStepIndex = 0;
                    if (currentStepIndex < allRecipeStepDetails.size() - 1) {
                        goToStepIndex = currentStepIndex + 1;
                    }
                    goToStep(goToStepIndex);
                }
            });
        }

        // Underlined text for the Recipe Short Description.
        SpannableString underlinedText = new SpannableString(recipeStepDetails.getShortDescription());
        underlinedText.setSpan(new UnderlineSpan(), 0, recipeStepDetails.getShortDescription().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        recipeStepShortDescText.setText(underlinedText);

        recipeStepDetailsText.setText(recipeStepDetails.getDescription());
    }

    /**
     * Displays thumbnail image instead of video if video url does not exist.<br/>
     * If error occurs while loading the thumbnail, step details are shown in full screen instead using {@link #showStepDetailsInFullScreen()}
     */
    private void showThumbnailInPlaceOfVideo() {
        playerView.setVisibility(View.GONE);
        // Setting visibility here as well, as onSuccess call is not invoked in case the image is cached.
        recipeThumbnailImage.setVisibility(View.VISIBLE);

        // Set dynamic content description
        recipeThumbnailImage.setContentDescription(getString(R.string.recipe_step_thumbnail_image_contentdesc) + recipeStepDetails.getShortDescription());

        /*
         * Placeholders not set here, as empty thumbnail URI leads to exception, and neither normal or error placeholders are set.
         * Incorrect images with MP$ extensions are also being filtered early, as onError handling of visibility toggling is not smooth and a with a slight delay.
         */
        try {
            Picasso.with(getContext()).load(recipeStepDetails.getThumbnailURL()).fit().into(recipeThumbnailImage, new Callback() {
                @Override
                public void onSuccess() {
                    recipeThumbnailImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    recipeThumbnailImage.setVisibility(View.GONE);
                    // Both video and thumbnail cannot be displayed, hence show step details in full screen
                    showStepDetailsInFullScreen();
                }
            });
        } catch (Exception e) {
            recipeThumbnailImage.setVisibility(View.GONE);
            // Both video and thumbnail cannot be displayed, hence show step details in full screen
            showStepDetailsInFullScreen();
        }
    }

    /**
     * Displays the step details in full screen only in landscape mode and if its not a two-pane layout.
     */
    private void showStepDetailsInFullScreen() {
        videoViewContainer.setVisibility(View.GONE);
        if (!mTwoPane && DisplayUtils.isLandscape(getContext())) {
            stepDetailsContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Navigates to the Recipe Step Details of the specified Step.
     *
     * @param goToStepIndex Step Index of the Recipe Step to navigate to.
     */
    private void goToStep(int goToStepIndex) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        RecipeStepDetailFragment newRecipeStepDetailFragment = new RecipeStepDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, recipeDetails);
        arguments.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAILS, allRecipeStepDetails.get(goToStepIndex));
        arguments.putString(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE, RecipeStepDetailFragment.class.getName());

        // Send default values of the primitives for player state parameters
        arguments.putLong(BakerenaConstants.BUNDLE_KEY_PLAYBACK_POSITION, BakerenaConstants.DEFAULT_PLAYBACK_POSITION);
        arguments.putInt(BakerenaConstants.BUNDLE_KEY_CURRENT_WINDOW, BakerenaConstants.DEFAULT_CURRENT_WINDOW);
        arguments.putBoolean(BakerenaConstants.BUNDLE_KEY_PLAY_WHEN_READY, BakerenaConstants.DEFAULT_PLAY_WHEN_READY);

        newRecipeStepDetailFragment.setArguments(arguments);

        fragmentTransaction.replace(R.id.recipe_step_detail_fragment_container, newRecipeStepDetailFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        updatePlayerStateParams();
        updatePlayerStateInParent();

        outState.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_DETAILS, recipeDetails);
        outState.putParcelable(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAILS, recipeStepDetails);
        outState.putString(BakerenaConstants.BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE, BakerenaConstants.BUNDLE_VALUE_SCREEN_ROTATION);

        outState.putLong(BakerenaConstants.BUNDLE_KEY_PLAYBACK_POSITION, playbackPosition);
        outState.putInt(BakerenaConstants.BUNDLE_KEY_CURRENT_WINDOW, currentWindow);
        outState.putBoolean(BakerenaConstants.BUNDLE_KEY_PLAY_WHEN_READY, playWhenReady);
    }

    /**
     * Updates the parameters relating to the Exoplayer state.
     */
    private void updatePlayerStateParams() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
        }
    }

    /**
     * Updates or syncs the player state parameter values with those maintained in the parent activity.<br/><br/>
     * This is essential to maintain player state on screen rotation, if both the portrait and landscape orientations have a two-pane layout.
     * <b>Note: </b>Currently, the player state parameters are only maintained in {@link RecipeStepListActivity} that supports a two-pane layout.
     */
    private void updatePlayerStateInParent() {
        if (RecipeStepListActivity.class.getName().equals(fragmentInvocationSource) && mTwoPane) {
            RecipeStepListActivity recipeStepListActivity = (RecipeStepListActivity) getActivity();
            recipeStepListActivity.updatePlayerStateParams(playbackPosition, currentWindow, playWhenReady);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside onStart");
            // Fragment is instantiated on every screen rotation, thereby the player gets re-initialized when screen rotation from two-pane to single pane layout happens.
            if (RecipeStepListActivity.class.getName().equals(fragmentInvocationSource) && !mTwoPane) {
                return;
            }
            initializeMediaSession();
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside onResume - mTwoPane : " + mTwoPane);

        // Hide System UI to show recipe video in full-screen in landscape mode, except in master-detail layout.
        if (!mTwoPane && DisplayUtils.isLandscape(getContext())) {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Calling hideSystemUi");
            hideSystemUi();
        }
        if ((Util.SDK_INT <= 23 || player == null)) {
            // Fragment is instantiated on every screen rotation, thereby the player gets re-initialized when screen rotation from two-pane to single pane layout happens.
            if (RecipeStepListActivity.class.getName().equals(fragmentInvocationSource) && !mTwoPane) {
                return;
            }
            initializeMediaSession();
            initializePlayer();
        }
    }

    /**
     * Hides System UI to show recipe video in full-screen in landscape mode, except in master-detail layout.
     */
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.<br/><br/>
     * <p>
     * <b>Note: </b>Currently media button receiver functionality is not in place, hence restricting Media session initialization to a safe Version 23
     * as media button receiver is mandatory for versions below Lollipop.
     */
    private void initializeMediaSession() {
        if (Util.SDK_INT >= 23) {

            // Create a MediaSessionCompat.
            mediaSessionCompat = new MediaSessionCompat(getContext(), TAG);

            // Enable callbacks from MediaButtons and TransportControls.
            mediaSessionCompat.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            // Do not let MediaButtons restart the player when the app is not visible.
            mediaSessionCompat.setMediaButtonReceiver(null);

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
            playbackStateCompatBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);

            mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());


            // MySessionCallback has methods that handle callbacks from a media controller.
            mediaSessionCompat.setCallback(new MediaSessionCallback());

            // Start the Media Session since the activity is active.
            mediaSessionCompat.setActive(true);
        }
    }

    /**
     * Sets up the Exoplayer instance.
     */
    private void initializePlayer() {
        Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside initializePlayer");
        if (recipe_uri != null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            playerView.setPlayer(player);
            player.addListener(customPlayerListener);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

            MediaSource mediaSource = buildMediaSource(recipe_uri);
            player.prepare(mediaSource, false, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("Bakerena")).
                createMediaSource(uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside onPause");
            releasePlayer();
            deactivateMediaSession();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside onStop");
            releasePlayer();
            deactivateMediaSession();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(RECIPE_STEP_DETAIL_FRAGMENT_TAG, "Inside onDestroy");
        releasePlayer();
        deactivateMediaSession();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(customPlayerListener);
            player.stop();
            player.release();
            player = null;
        }
    }

    private void deactivateMediaSession() {
        if (Util.SDK_INT >= 23) {
            if (mediaSessionCompat != null) {
                mediaSessionCompat.setActive(false);
            }
        }
    }

    /**
     * Exoplayer Listener to listen to player state changes.
     */
    private class CustomPlayerListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (Util.SDK_INT >= 23) {
                if ((playbackState == Player.STATE_READY) && playWhenReady) {
                    playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                            player.getCurrentPosition(), 1f);
                } else if ((playbackState == Player.STATE_READY)) {
                    playbackStateCompatBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            player.getCurrentPosition(), 1f);
                }
                mediaSessionCompat.setPlaybackState(playbackStateCompatBuilder.build());
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            player.seekTo(0);
        }
    }

}
