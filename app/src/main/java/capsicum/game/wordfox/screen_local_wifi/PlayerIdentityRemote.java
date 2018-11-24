package capsicum.game.wordfox.screen_local_wifi;

import capsicum.game.wordfox.PlayerIdentity;
import capsicum.game.wordfox.screen_profile.FoxRank;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class PlayerIdentityRemote extends PlayerIdentity {
    public final String macAddress;
    public final boolean isGroupOwner;
    public final long timeStamp;
    private static final String MAC_ADDRESS_KEY = "mac_address_key";
    private static final String TIME_STAMP_KEY = "time_stamp_key";
    private static final String IS_GROUP_OWNER_KEY = "is_group_owner_key";

    public PlayerIdentityRemote(UUID ID, String username, FoxRank rank, String macAddress, boolean isGroupOwner) {
        super(ID, username, rank);
        this.macAddress = macAddress;
        this.isGroupOwner = isGroupOwner;
        this.timeStamp = System.currentTimeMillis();
    }

    public PlayerIdentityRemote(JSONObject player) throws JSONException {
        super(UUID.fromString(player.getString(PLAYER_ID)),
                player.getString(USERNAME),
                new FoxRank(player.getJSONObject(FOX_RANK_KEY)));
        this.macAddress = player.getString(MAC_ADDRESS_KEY);
        this.timeStamp = player.getLong(TIME_STAMP_KEY);
        this.isGroupOwner = player.getBoolean(IS_GROUP_OWNER_KEY);
    }

    public JSONObject toJson() {
        JSONObject player = new JSONObject();
        try {
            player.put(PLAYER_ID, ID.toString());
            player.put(USERNAME, username);
            player.put(FOX_RANK_KEY, rank.toJson());
            player.put(MAC_ADDRESS_KEY, macAddress);
            player.put(TIME_STAMP_KEY, timeStamp);
            player.put(IS_GROUP_OWNER_KEY, isGroupOwner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return player;
    }
}
