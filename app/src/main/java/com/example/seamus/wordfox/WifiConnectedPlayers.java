package com.example.seamus.wordfox;

import android.util.Log;

import java.util.ArrayList;

public class WifiConnectedPlayers {

    private PeersUpdated peersUpdate;

    WifiConnectedPlayers(PeersUpdated peersUpdate) {
        this.peersUpdate = peersUpdate;
    }

    private ArrayList<PlayerIdentityRemote> connectedPlayers = new ArrayList<>();

    public ArrayList<PlayerIdentityRemote> getConnectedPlayers() {
        return connectedPlayers;
    }

    public void clear() {
        connectedPlayers.clear();
        peersUpdate.notifyPeersUpdated();
    }

    public PlayerIdentityRemote getGroupOwner(){
        for(PlayerIdentityRemote p : connectedPlayers){
            if(p.isGroupOwner){
                log(p.username + " is GO!!  : " + p.macAddress);
                return p;
            }
            log(p.username + " is not GO : " + p.macAddress);
        }
        return null;
    }

    // TODO: make thread safe
    public void addConnectedPlayer(PlayerIdentityRemote player) {
        // add player but remove older players with same id
        ArrayList<PlayerIdentityRemote> toBeRemoved = new ArrayList<>();
        for (PlayerIdentityRemote existingPlayer : connectedPlayers) {
            if (existingPlayer.equals(player)) {
                if (existingPlayer.timeStamp > player.timeStamp) {
                    log("WCP Add : ======== Not adding player," + player.toJson().toString() + " more recent exists : " + existingPlayer.toJson().toString());
                    return;
                }
                toBeRemoved.add(existingPlayer);
                log("WCP Add : ======== Removing duplicate player : " + player.toJson().toString());
            }
        }
        log("=======!======== Success added player =======!======== " + player.toJson().toString());
        connectedPlayers.removeAll(toBeRemoved);
        connectedPlayers.add(player);
        peersUpdate.notifyPeersUpdated();
    }

    public int size() {
        return connectedPlayers.size();
    }

    private void log(String msg) {
        Log.d(WordfoxConstants.MONITOR_TAG, msg);
    }

    private void removePlayer(PlayerIdentityRemote player) {
        // Only remove if  equals(id) and   toBeRemoved.time >=  existing.time
        log("Attempting to remove: " + player.toJson().toString());
        log("Existing players: " + connectedPlayers);
        ArrayList<PlayerIdentityRemote> toBeRemoved = new ArrayList<>();
        for (PlayerIdentityRemote existingPlayer : connectedPlayers) {
            if (existingPlayer.equals(player) && existingPlayer.timeStamp <= player.timeStamp) {
                toBeRemoved.add(existingPlayer);
                log("WCP Rem : ======== Removing player : " + player.toJson().toString());
            } else if (existingPlayer.equals(player)) {
                log("WCP : ======= Didn't remove matching player as time stamp was more recent : " + existingPlayer.toString());
            }
        }
        log("=======!======== removed player? " + (toBeRemoved.size() > 0) + " =======!========");
        connectedPlayers.removeAll(toBeRemoved);
    }

    public void removeConnectedPlayer(PlayerIdentityRemote player) {
        removePlayer(player);
        peersUpdate.notifyPeersUpdated();
    }

    public void removeMultipleConnectedPlayers(ArrayList<PlayerIdentityRemote> players) {
        for (PlayerIdentityRemote p : players) {
            removePlayer(p);
        }
        peersUpdate.notifyPeersUpdated();
    }
}
