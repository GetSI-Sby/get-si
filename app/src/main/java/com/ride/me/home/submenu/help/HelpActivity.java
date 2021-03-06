package com.ride.me.home.submenu.help;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ride.me.GoridemeAplication;
import com.ride.me.R;
import com.ride.me.api.ServiceGenerator;
import com.ride.me.api.service.UserService;
import com.ride.me.model.User;
import com.ride.me.model.json.menu.HelpRequestJson;
import com.ride.me.model.json.menu.HelpResponseJson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.help_title)
    TextView helpTitle;
    @BindView(R.id.btn_home)
    ImageView backButton;
    @BindView(R.id.help_subject)
    TextView helpSubject;
    @BindView(R.id.help_description)
    TextView helpDescription;
    @BindView(R.id.send_help_request)
    Button sendHelpRequest;

    Context context;
    private int titleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHelpTitle(getIntent().getIntExtra("id",-1));

        sendHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helpSubject.getText().toString().length()>0&&helpDescription.getText().toString().length()>0){
                    Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show();
                    User loginUser = GoridemeAplication.getInstance(HelpActivity.this).getLoginUser();
                    HelpRequestJson request = new HelpRequestJson();
                    request.id_pelanggan = loginUser.getId();
                    request.subject = helpSubject.getText().toString();
                    request.description = helpDescription.getText().toString();
                    request.type = titleId+"";

                    UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
                    service.sendHelp(request).enqueue(new Callback<HelpResponseJson>() {
                        @Override
                        public void onResponse(Call<HelpResponseJson> call, Response<HelpResponseJson> response) {
                            if (response.isSuccessful()) {
                                if (response.body().mesage.equals("success")) {
                                    helpSubject.setText("");
                                    helpDescription.setText("");
                                    View view = HelpActivity.this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(HelpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<HelpResponseJson> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(HelpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setHelpTitle(int id){
        String titile = "";
        titleId = id;
        switch (id){
            case 0:
                titile = "M - CAR";
                break;
            case 1:
                titile = "M - RIDE";
                break;
            case 2:
                titile = "M - SEND";
                break;
            case 3:
                titile = "M - BOX";
                break;
            case 4:
                titile = "M - MASSAGE";
                break;
            case 5:
                titile = "M - FOOD";
                break;
            case 6:
                titile = "M - SERVICE";
                break;
            default:
                titile = "M - JEK";
                break;

        }
        helpTitle.setText(titile);
    }

}
