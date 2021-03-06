package com.ride.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by David Studio on 12/18/2017.
 */

public class JenisService implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("jenis_service")
    private String jenisService;

    @Expose
    @SerializedName("fitur_mservice")
    private int fiturMservice;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("deskripsi")
    private String deskripsi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJenisService() {
        return jenisService;
    }

    public void setJenisService(String jenisService) {
        this.jenisService = jenisService;
    }

    public int getFiturMservice() {
        return fiturMservice;
    }

    public void setFiturMservice(int fiturMservice) {
        this.fiturMservice = fiturMservice;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
