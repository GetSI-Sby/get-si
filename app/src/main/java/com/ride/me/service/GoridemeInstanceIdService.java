package com.ride.me.service;

import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.ride.me.model.FirebaseToken;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by David Studio on 10/13/2017.
 */

public class GoridemeInstanceIdService extends FirebaseMessagingService {

//    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//
//
//    }
    @Override
    public void onNewToken(String s) {
        saveToken(s);
    }


    private void saveToken(String tokenId) {
        FirebaseToken token = new FirebaseToken(tokenId);
        EventBus.getDefault().postSticky(token);
    }

}
