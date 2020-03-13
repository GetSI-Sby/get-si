package com.ride.me.home;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ride.me.GoridemeAplication;
import com.ride.me.model.json.user.GetFiturResponseJson;

import com.ride.me.R;
import com.ride.me.api.ServiceGenerator;
import com.ride.me.api.service.UserService;
import com.ride.me.home.submenu.help.HelpFragment;
import com.ride.me.home.submenu.history.HistoryFragment;
import com.ride.me.home.submenu.home.HomeFragment;
import com.ride.me.home.submenu.setting.SettingFragment;
import com.ride.me.mMassage.MassagePreference;
import com.ride.me.mMassage.MenuMassageItem;
import com.ride.me.model.DiskonMpay;
import com.ride.me.model.Fitur;
import com.ride.me.model.MfoodMitra;
import com.ride.me.model.User;
import com.ride.me.utils.GoridemeTabProvider;
import com.ride.me.utils.MenuSelector;
import com.ride.me.utils.SnackbarController;
import com.ride.me.utils.view.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David Studio on 10/10/2017.
 */

public class MainActivity extends AppCompatActivity implements SnackbarController {

    @BindView(R.id.main_container)
    LinearLayout mainLayout;

    @BindView(R.id.main_tabLayout)
    SmartTabLayout mainTabLayout;

    @BindView(R.id.main_viewPager)
    CustomViewPager mainViewPager;

    private Snackbar snackBar;

    private MenuSelector selector;
    private SmartTabLayout.TabProvider tabProvider;
    private MenuMassageItem massageItem;
    private MassagePreference massagePreference;

    private FragmentPagerItemAdapter adapter;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupTabLayoutViewPager();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press the 'BACK' button again to exit ...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void setupTabLayoutViewPager() {
        tabProvider = new GoridemeTabProvider(this);
        selector = (MenuSelector) tabProvider;
        mainTabLayout.setCustomTabView(tabProvider);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.main_menuHome, HomeFragment.class)
                .add(R.string.main_menuHistory, HistoryFragment.class)
                .add(R.string.main_menuHelp, HelpFragment.class)
                .add(R.string.main_menuSetting, SettingFragment.class)
                .create());
        mainViewPager.setAdapter(adapter);
        mainTabLayout.setViewPager(mainViewPager);
        mainViewPager.setPagingEnabled(false);

        mainTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                selector.selectMenu(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFiturGorideme();
    }

    @Override
    public void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener) {
        snackBar = Snackbar.make(mainLayout, stringRes, duration);
        if (actionResText != -1 && onClickListener != null) {
            snackBar.setAction(actionResText, onClickListener)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        snackBar.show();
    }



    private void updateFiturGorideme() {
        User loginUser = GoridemeAplication.getInstance(this).getLoginUser();
        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getFitur().enqueue(new Callback<GetFiturResponseJson>() {
            @Override
            public void onResponse(Call<GetFiturResponseJson> call, Response<GetFiturResponseJson> response) {
                if (response.isSuccessful()) {
                    Realm realm = GoridemeAplication.getInstance(MainActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Fitur.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    DiskonMpay diskonMpay = response.body().getDiskonMpay();
                    realm.beginTransaction();
                    realm.delete(DiskonMpay.class);
                    realm.copyToRealm(response.body().getDiskonMpay());
                    realm.commitTransaction();
                    GoridemeAplication.getInstance(MainActivity.this).setDiskonMpay(diskonMpay);

                    MfoodMitra mfoodMitra = response.body().getMfoodMitra();
                    realm.beginTransaction();
                    realm.delete(MfoodMitra.class);
                    realm.copyToRealm(response.body().getMfoodMitra());
                    realm.commitTransaction();
                    GoridemeAplication.getInstance(MainActivity.this).setMfoodMitra(mfoodMitra);
                }
            }

            @Override
            public void onFailure(Call<GetFiturResponseJson> call, Throwable t) {

            }
        });
    }
}
