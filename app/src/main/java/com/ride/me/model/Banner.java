package com.ride.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by David Studio on 12/01/2017.
 */

public class Banner implements Serializable {
    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("fitur_promosi")
    public String fiturPromosi;

    @Expose
    @SerializedName("foto")
    public String foto;


}
