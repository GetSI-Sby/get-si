package com.ride.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by David Studio on 12/23/2017.
 */

public class MboxInsurance implements Serializable {
    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("premi")
    public long premi;

    @Expose
    @SerializedName("estimasi_biaya")
    public long estimasi_biaya;

}
