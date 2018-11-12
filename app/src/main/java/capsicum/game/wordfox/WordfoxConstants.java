package capsicum.game.wordfox;

public class WordfoxConstants {
    public static final String MONITOR_TAG = "myTag";
    public static final int NUMBER_ROUNDS = 3;
    public static final int RESULT_GRIDS_PER_ROW = NUMBER_ROUNDS;
    public static final int RESULT_GRID_SCREEN_WIDTH_PERCENT = 25;
    public static final int MAX_PLAYER_NAME_LENGTH = 12;
    public static final String WORD_TAG_PREFIX = "word_";
    public static final String GRID_TAG_PREFIX = "grid_";
    public static final String TEST_AD_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
    public static final String TEST_AD_BANNER = "ca-app-pub-3940256099942544/6300978111";

    public static class Analytics{
        public static final String INTERSTITIAL_LOAD_DURATION_ID = "interstitial_load_duration_id";
        public static final String INTERSTITIAL_LOAD_DURATION_NAME = "interstitial_load_duration_name";
        public static final String INTERSTITIAL_LOAD_DURATION_TIME = "load_duration_time";

        public static class Event{
            public static final String INTERSTITIAL_AD_LOAD_DURATION = "interstitial_ad_load_duration";
        }

    }
}
