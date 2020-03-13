package com.ride.me.home.submenu.history.details;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.ride.me.GoridemeAplication;
import com.ride.me.api.ServiceGenerator;
import com.ride.me.api.service.BookService;
import com.ride.me.home.submenu.history.details.items.MMartItem;
import com.ride.me.model.MMartDetailTransaksi;
import com.ride.me.model.MMartItemRemote;
import com.ride.me.model.User;
import com.ride.me.model.json.book.detailTransaksi.GetDataTransaksiMMartResponse;
import com.ride.me.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David Studio on 24/02/2017.
 */

public class MMartDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(com.ride.me.R.id.mMartDetail_title)
    TextView title;
    @BindView(com.ride.me.R.id.mMartDetail_recycler)
    RecyclerView recyclerView;
    @BindView(com.ride.me.R.id.mMartDetail_total)
    TextView totalField;
    @BindView(com.ride.me.R.id.delivery_cost)
    TextView delCost;
    @BindView(com.ride.me.R.id.total_cost)
    TextView totalCost;


    FastItemAdapter<MMartItem> adapter;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ride.me.R.layout.activity_mmart_detail);
        ButterKnife.bind(this);

        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = GoridemeAplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(GoridemeAplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());


        title.setText("Detail M-Mart");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FastItemAdapter<>();
        recyclerView.setAdapter(adapter);

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMMart(param).enqueue(new Callback<GetDataTransaksiMMartResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMMartResponse> call, Response<GetDataTransaksiMMartResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        MMartDetailTransaksi detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail, response.body().getListBarang());
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMMartResponse> call, Throwable t) {
                Toast.makeText(MMartDetailActivity.this, "Silahkan coba lagi lain waktu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(MMartDetailTransaksi transaksi, List<MMartItemRemote> items) {
        List<MMartItem> mMartItems = new ArrayList<>();
        for (MMartItemRemote item : items) {
            mMartItems.add(new MMartItem(item.getNamaBarang(), item.getJumlah()));
        }
        adapter.clear();
        adapter.set(mMartItems);
        adapter.notifyDataSetChanged();
        String total = String.format(Locale.US, "$. %s",
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getEstimasiBiaya()));
        totalField.setText(total);
        String delcost = String.format(Locale.US, "$ %s",
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getHarga()));
        delCost.setText(delcost);
        String totalcost = String.format(Locale.US, "$. %s",
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getHarga()));
        totalCost.setText(totalcost);
    }



}
