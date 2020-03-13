package com.ride.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ride.me.model.LokasiDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Studio on 24/02/2017.
 */

public class LiatLokasiDriverResponse {

    @SerializedName("data")
    @Expose
    private List<LokasiDriver> data = new ArrayList<>();

    public List<LokasiDriver> getData() {
        return data;
    }

}
