package capsicum.game.wordfox;

public class WordfoxConstants {
    public static final String MONITOR_TAG = "myTag";
    public static final int NUMBER_ROUNDS = 3;
    public static final int GRID_LETTERS_PER_ROW = 3;
    public static final int GRID_LETTERS_PER_COLUMN = 3;
    public static final int GRID_LETTERS_COUNT = GRID_LETTERS_PER_ROW * GRID_LETTERS_PER_COLUMN;
    public static final float GRID_WHITE_SPACE_PERCENT = 0.1f;
    public static final int RESULT_GRIDS_PER_ROW = NUMBER_ROUNDS;
    public static final float RESULT_GRID_SCREEN_WIDTH_PERCENT = 0.25f;
    // Can percentage width of the standard speech bubble can be used to display text
    public static final float TEXT_WIDTH_PERCENT_SPEECH_BUBBLE = 0.8f;
    public static final int MAX_PLAYER_NAME_LENGTH = 12;
    public static final String WORD_TAG_PREFIX = "word_";
    public static final String GRID_TAG_PREFIX = "grid_";
    public static final String TEST_AD_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
    public static final String TEST_AD_BANNER = "ca-app-pub-3940256099942544/6300978111";
    public static final String GAME_END_BANNER_AD_UNIT_ID = "ca-app-pub-5181377347442835/9136484888";
    public static final String DATA_PAGE_BANNER_AD_UNIT_ID = "ca-app-pub-5181377347442835/9069160957";
    public static final String END_OF_ROUND3_BANNER_AD_UNIT_ID = "ca-app-pub-5181377347442835/4199977656";
    public static final String PROFILE_PAGE_BANNER_AD_UNIT_ID = "ca-app-pub-5181377347442835/8686017575";

    public static class Analytics{
        public static final String INTERSTITIAL_LOAD_DURATION_ID = "interstitial_load_duration_id";
        public static final String INTERSTITIAL_LOAD_DURATION_NAME = "interstitial_load_duration_name";
        public static final String INTERSTITIAL_LOAD_DURATION_TIME = "load_duration_time";

        public static class Event{
            public static final String INTERSTITIAL_AD_LOAD_DURATION = "interstitial_ad_load_duration";
        }

    }
}
