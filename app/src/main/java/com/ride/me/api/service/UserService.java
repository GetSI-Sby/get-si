package com.ride.me.api.service;

import com.ride.me.model.json.book.RateDriverRequestJson;
import com.ride.me.model.json.fcm.CancelBookRequestJson;
import com.ride.me.model.json.fcm.CancelBookResponseJson;
import com.ride.me.model.json.menu.HelpResponseJson;
import com.ride.me.model.json.menu.HistoryRequestJson;
import com.ride.me.model.json.menu.VersionRequestJson;
import com.ride.me.model.json.menu.VersionResponseJson;
import com.ride.me.model.json.user.ChangePasswordRequestJson;
import com.ride.me.model.json.user.ChangePasswordResponseJson;
import com.ride.me.model.json.user.GetBannerResponseJson;
import com.ride.me.model.json.user.GetFiturResponseJson;
import com.ride.me.model.json.user.GetSaldoResponseJson;
import com.ride.me.model.json.user.RegisterRequestJson;
import com.ride.me.model.json.user.RegisterResponseJson;
import com.ride.me.model.json.user.TopupRequestJson;
import com.ride.me.model.json.user.TopupResponseJson;
import com.ride.me.model.json.user.UpdateProfileResponseJson;
import com.ride.me.model.json.book.RateDriverResponseJson;
import com.ride.me.model.json.menu.HelpRequestJson;
import com.ride.me.model.json.menu.HistoryResponseJson;
import com.ride.me.model.json.user.GetSaldoRequestJson;
import com.ride.me.model.json.user.LoginRequestJson;
import com.ride.me.model.json.user.LoginResponseJson;
import com.ride.me.model.json.user.UpdateProfileRequestJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by David Studio on 10/13/2017.
 */

public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("pelanggan/get_saldo")
    Call<GetSaldoResponseJson> getSaldo(@Body GetSaldoRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/user_send_help")
    Call<HelpResponseJson> sendHelp(@Body HelpRequestJson param);

    @POST("pelanggan/update_profile")
    Call<UpdateProfileResponseJson> updateProfile(@Body UpdateProfileRequestJson param);

    @POST("pelanggan/change_password")
    Call<ChangePasswordResponseJson> changePassword(@Body ChangePasswordRequestJson param);

    @POST("book/user_cancel_transaction")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/check_version")
    Call<VersionResponseJson> checkVersion(@Body VersionRequestJson param);

    @POST("book/user_rate_driver")
    Call<RateDriverResponseJson> rateDriver(@Body RateDriverRequestJson param);

    @POST("pelanggan/verifikasi_topup")
    Call<TopupResponseJson> topUp(@Body TopupRequestJson param);

    @POST("pelanggan/complete_transaksi")
    Call<HistoryResponseJson> getCompleteHistory(@Body HistoryRequestJson param);

    @POST("pelanggan/inprogress_transaksi")
    Call<HistoryResponseJson> getOnProgressHistory(@Body HistoryRequestJson param);

    @GET("pelanggan/banner_promosi")
    Call<GetBannerResponseJson> getBanner();

}
