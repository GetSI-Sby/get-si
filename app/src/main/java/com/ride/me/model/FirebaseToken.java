package com.ride.me.model;

import io.realm.RealmObject;

/**
 * Created by David Studio on 10/13/2017.
 */

public class FirebaseToken extends RealmObject {
    private String tokenId;

    public FirebaseToken(String tokenId) {
        this.tokenId = tokenId;
    }

    public FirebaseToken() {
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
