package com.ride.me.mMassage;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/21/2017.
 */

public class MenuMassageItem extends AbstractItem<MenuMassageItem, MenuMassageItem.ViewHolder>
        implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("layanan")
    private String layanan;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("foto")
    private String foto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLayanan() {
        return layanan;
    }

    public void setLayanan(String layanan) {
        this.layanan = layanan;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.textView.setText(getLayanan());
//        Picasso.with(holder.imageView.getContext()).load(getFoto()).fit().centerCrop().into(holder.imageView);
        Glide.with(holder.imageView.getContext()).load(getFoto()).into(holder.imageView);
    }

    @Override
    public int getType() {
        return com.ride.me.R.id.itemMassageMenu_item;
    }

    @Override
    public int getLayoutRes() {
        return com.ride.me.R.layout.item_massage_menu;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(com.ride.me.R.id.itemMassageMenu_image)
        ImageView imageView;

        @BindView(com.ride.me.R.id.itemMassageMenu_text)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ViewHolder.this, itemView);
        }
    }
}
