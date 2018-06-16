package store.bakerena.common;

/**
 * Class containing Constants to be used across the application.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public final class BakerenaConstants {

    private BakerenaConstants() {

    }

    // Dynamic Recycler View Constants
    public static final int MIN_GRID_COLS_PORTRAIT = 1;

    public static final int MIN_GRID_COLS_LANDSCAPE = 2;

    public static final float EMPTY_SPACE_TO_SCREEN_WIDTH_RATIO = 0.2f;

    public static final int DEFAULT_GRID_IMG_WIDTH = 300;

    public static final String GRID_COL_COUNT = "GRID_COL_COUNT";

    public static final String GRID_MARGIN = "GRID_MARGIN";

    // Instance State Constants
    public static final String BUNDLE_KEY_RECIPE_LIST_DATA = "BUNDLE_KEY_RECIPE_LIST_DATA";

    public static final String BUNDLE_KEY_RECIPE_DETAILS = "BUNDLE_KEY_RECIPE_DETAILS";

    public static final String BUNDLE_KEY_RECIPE_STEP_DETAILS = "BUNDLE_KEY_RECIPE_STEP_DETAILS";

    public static final String BUNDLE_KEY_FAVORITE_RECIPE = "BUNDLE_KEY_FAVORITE_RECIPE";

    public static final String BUNDLE_KEY_PLAYBACK_POSITION = "BUNDLE_KEY_PLAYBACK_POSITION";

    public static final String BUNDLE_KEY_CURRENT_WINDOW = "BUNDLE_KEY_CURRENT_WINDOW";

    public static final String BUNDLE_KEY_PLAY_WHEN_READY = "BUNDLE_KEY_PLAY_WHEN_READY";

    public static final String BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE = "BUNDLE_KEY_RECIPE_STEP_DETAIL_FRAGMENT_INVOCATION_SOURCE";

    public static final String BUNDLE_VALUE_SCREEN_ROTATION = "BUNDLE_VALUE_SCREEN_ROTATION";

    public static final String BUNDLE_KEY_RECIPE_RV_SCROLL_POSITION = "BUNDLE_KEY_RECIPE_RV_SCROLL_POSITION";

    public static final String BUNDLE_KEY_INGREDIENTS_RV_SCROLL_POSITION = "BUNDLE_KEY_INGREDIENTS_RV_SCROLL_POSITION";

    public static final String BUNDLE_KEY_RECIPESTEP_RV_SCROLL_POSITION = "BUNDLE_KEY_RECIPESTEP_RV_SCROLL_POSITION";

    // Preferences Constants
    public static final String BAKERENA_PREFERENCES_NAME = "store.bakerena.recipes";

    public static final String PREFERENCE_ALL_RECIPES_RAW_JSON_KEY = "PREFERENCE_ALL_RECIPES_RAW_JSON_KEY";

    public static final String PREFERENCE_FAVORITE_RECIPE_JSON = "PREFERENCE_FAVORITE_RECIPE_JSON";


    // Exoplayer Constants
    public static final long DEFAULT_PLAYBACK_POSITION = 0L;

    public static final int DEFAULT_CURRENT_WINDOW = 0;

    public static final boolean DEFAULT_PLAY_WHEN_READY = true;

    public static final String MP4_FILE_EXTENSION = ".mp4";
}
