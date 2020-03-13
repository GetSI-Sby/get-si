package com.ride.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by David Studio on 12/23/2017.
 */

public class AdditionalMbox implements Serializable{
    @Expose
    @SerializedName("additional_shipper")
    public long additional_shipper;

    @Expose
    @SerializedName("asuransi")
    public List<MboxInsurance> asuransi;


}
