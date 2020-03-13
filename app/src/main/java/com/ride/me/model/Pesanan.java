package com.ride.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by David Studio on 12/7/2017.
 */

public class Pesanan implements Serializable {
    @Expose
    @SerializedName("nama_barang")
    private String namaBarang;

    @Expose
    @SerializedName("qty")
    private int qty;

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
