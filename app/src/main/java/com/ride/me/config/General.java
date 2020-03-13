package com.ride.me.config;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;



public class General {
    public static final String FCM_KEY = "AIzaSyCAML1ceMamB0kT6nqi-lJI2AzmaDDiclU";
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-7.216001, 0), // southwest
            new LatLng(0, 107.903316)); // northeast
}
