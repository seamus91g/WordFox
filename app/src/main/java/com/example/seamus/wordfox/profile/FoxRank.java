package com.example.seamus.wordfox.profile;

import org.json.JSONException;
import org.json.JSONObject;

public class FoxRank {
    private static final String IMG_RESOURCE_KEY = "img_resource_key";
    private static final String RANK_KEY = "rank_key";
    public final int imageResource;
    public final String foxRank;


    public FoxRank(int resource, String rank) {
        this.imageResource = resource;
        this.foxRank = rank;
    }

    public FoxRank(JSONObject foxRankJson) throws JSONException {
        this.imageResource = foxRankJson.getInt(IMG_RESOURCE_KEY);
        this.foxRank = foxRankJson.getString(RANK_KEY);
    }

    public JSONObject toJson() {
        JSONObject rank = new JSONObject();
        try {
            rank.put(IMG_RESOURCE_KEY, imageResource);
            rank.put(RANK_KEY, foxRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rank;
    }
}
