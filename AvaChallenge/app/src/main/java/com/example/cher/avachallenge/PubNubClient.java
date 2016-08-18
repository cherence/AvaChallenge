package com.example.cher.avachallenge;

import android.os.Binder;

/**
 * Created by leisforkokomo on 8/17/16.
 */
public class PubNubClient extends Binder {
    private PubNubService pubNubService;

    public PubNubClient(PubNubService pubNubService) {
        this.pubNubService = pubNubService;
    }

    PubNubClient getService(){
        return PubNubClient.this;
    }


}
