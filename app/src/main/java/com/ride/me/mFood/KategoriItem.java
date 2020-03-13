package com.ride.me.mFood;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/28/2017.
 */

public class KategoriItem extends AbstractItem<KategoriItem, KategoriItem.ViewHolder> {

    Context context;
    public int idKategori;
    public String kategori;
    public String image;

    public KategoriItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return com.ride.me.R.id.kategori_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.kategoriName.setText(kategori);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(image)
                .apply(options)
                .into(holder.kategoriIMG);
//        Picasso.with(context).load(image).fit().centerCrop().into(holder.kategoriIMG);
    }

    @Override
    public int getLayoutRes() {
        return com.ride.me.R.layout.item_kategori;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(com.ride.me.R.id.kategori_img)
        ImageView kategoriIMG;

        @BindView(com.ride.me.R.id.kategori_name)
        TextView kategoriName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
