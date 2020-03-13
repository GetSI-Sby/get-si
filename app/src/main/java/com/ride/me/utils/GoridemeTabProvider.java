package com.ride.me.utils;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 10/10/2017.
 */

public class GoridemeTabProvider implements SmartTabLayout.TabProvider, MenuSelector {

    private static int[] iconId = {
            com.ride.me.R.drawable.ic_action_home,
            com.ride.me.R.drawable.ic_action_history,
            com.ride.me.R.drawable.ic_faq,
            com.ride.me.R.drawable.ic_action_settings
    };

    private List<TabViewHolder> tabViewHolders;

    private LayoutInflater inflater;

    public GoridemeTabProvider(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.tabViewHolders = new ArrayList<>();
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View view;
        try {
            if (this.tabViewHolders.get(position) == null) {
                view = inflater.inflate(com.ride.me.R.layout.item_tab_layout, container, false);
                tabViewHolders.add(position, new TabViewHolder(view, iconId[position], adapter.getPageTitle(position)));
            }
            view = this.tabViewHolders.get(position).getView();
        } catch (Exception e) {
            view = inflater.inflate(com.ride.me.R.layout.item_tab_layout, container, false);
            tabViewHolders.add(position, new TabViewHolder(view, iconId[position], adapter.getPageTitle(position)));
        }
        return view;
    }

    @Override
    public void selectMenu(int position) {
        for (TabViewHolder holder : tabViewHolders) holder.setSelected(false);
        if (tabViewHolders.get(position) != null) tabViewHolders.get(position).setSelected(true);
    }


    static class TabViewHolder {
        @BindView(com.ride.me.R.id.item_tab_icon)
        ImageView tabIcon;
        @BindView(com.ride.me.R.id.item_tab_text)
        TextView tabText;
        private View view;

        public TabViewHolder(View view, @DrawableRes int iconRes, CharSequence pageTitle) {
            this.view = view;
            ButterKnife.bind(TabViewHolder.this, view);

            tabIcon.setImageResource(iconRes);
            tabText.setText(pageTitle);

            DrawableExtension.setTintSelector(tabIcon, com.ride.me.R.color.tab_selector);
        }

        public View getView() {
            return this.view;
        }

        public void setSelected(boolean isSelected) {
            tabIcon.setSelected(isSelected);
            tabText.setSelected(isSelected);
        }
    }
}
