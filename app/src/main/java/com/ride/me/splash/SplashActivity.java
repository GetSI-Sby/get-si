package com.ride.me.splash;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ride.me.api.ServiceGenerator;
import com.ride.me.home.MainActivity;
import com.ride.me.home.login;
import com.ride.me.model.json.menu.VersionResponseJson;
import com.ride.me.GoridemeAplication;
import com.ride.me.api.service.UserService;
import com.ride.me.model.User;
import com.ride.me.model.json.menu.VersionRequestJson;
import com.ride.me.signIn.SignInActivity;
import com.ride.me.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David Studio on 10/12/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ride.me.R.layout.activity_splash);
        PackageInfo pInfo;
        VersionRequestJson request = new VersionRequestJson();
        request.version = "1";
        request.application = "0";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            request.version = pInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        UserService service = ServiceGenerator.createService(UserService.class, null, null);
        service.checkVersion(request).enqueue(new Callback<VersionResponseJson>() {
            @Override
            public void onResponse(Call<VersionResponseJson> call, Response<VersionResponseJson> response) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                alertDialogBuilder.setTitle("UPDATE GoRideMe");
                alertDialogBuilder.setMessage("Click at the corner in your app.");

                if (response.isSuccessful()) {
                    if (response.body().new_version.equals("yes")) {
                        alertDialogBuilder.setMessage(response.body().message);
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        final String appPackageName = getPackageName();

                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else if(response.body().new_version.equals("no")) {
                        start();
                    }else if(response.body().new_version.equals("maintenance")){
                        Log.e("VERSION","Maintenance");
                        alertDialogBuilder.setPositiveButton("dismiss",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                        finish();
//                                        start();
                                    }
                                });

//                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                start();
//                            }
//                        });
                        alertDialogBuilder.setMessage(response.body().message);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else{
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                        start();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                start();
                            }
                        });
                        alertDialogBuilder.setMessage(response.body().message);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }


            }

            @Override
            public void onFailure(Call<VersionResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(SplashActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("System error:", t.getLocalizedMessage());
//                restartActivity();
                start();
            }
        });



    }


    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void start(){
//        new CountDownTimer(1000, 1000) {
//
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
        User user = GoridemeAplication.getInstance(this).getLoginUser();
        Intent intent;
        if (user != null) {
            intent = new Intent(SplashActivity.this, MainActivity.class);

        } else {
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        }
        startActivity(intent);
//            }
//        }.start();
    }

}
