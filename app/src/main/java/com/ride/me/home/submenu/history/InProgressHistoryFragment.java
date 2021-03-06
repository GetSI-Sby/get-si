package com.ride.me.home.submenu.history;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.me.GoridemeAplication;
import com.ride.me.adapter.HistoryAdapter;
import com.ride.me.api.ServiceGenerator;
import com.ride.me.api.service.UserService;
import com.ride.me.model.User;
import com.ride.me.model.json.menu.HistoryRequestJson;
import com.ride.me.splash.SplashActivity;
import com.ride.me.utils.Log;
import com.ride.me.model.ItemHistory;
import com.ride.me.model.json.menu.HistoryResponseJson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(com.ride.me.R.id.inProgress_recyclerView)
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    User user;

    public InProgressHistoryFragment() {
    }

    public static InProgressHistoryFragment newInstance() {
        InProgressHistoryFragment fragment = new InProgressHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.ride.me.R.layout.fragment_in_progress_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if(GoridemeAplication.getInstance(getActivity()).getLoginUser() != null){
            user = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        }else{
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }
        requestData();
    }


    private void requestData(){
        if(GoridemeAplication.getInstance(getActivity()).getLoginUser() != null){
            user = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        }
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getOnProgressHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;

//                    Log.e("HISTORY", data.get(0).toString());

                    for(int i = 0;i<data.size();i++){
                        switch (data.get(i).order_fitur){
                            case "m-ride":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_motor;
                                break;
                            case "m-car":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_mobil;
                                break;
                            case "m-send":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_paket;
                                break;
                            case "m-mart":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_toko;
                                break;
                            case "m-box":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_box;
                                break;
                            case "m-service":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_jasa;
                                break;
                            case "m-massage":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_pijat;
                                break;
                            case "m-food":
                                data.get(i).image_id = com.ride.me.R.mipmap.pro_food;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, false);
                    recyclerView.setAdapter(historyAdapter);

                    if (response.body().data.size() == 0) {
                        Log.d("HISTORY", "Empty");

                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("System error:", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }
}
