package com.ride.me.mMassage;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/22/2017.
 */

public class MassagePreferenceFragment extends Fragment {

    public static final String MASSAGE_ITEM_ID = "MassageItem";

    @BindView(com.ride.me.R.id.massagePreference_image)
    ImageView massagePicture;

    @BindView(com.ride.me.R.id.massagePreference_massageService)
    TextView massageDesc;

    @BindView(com.ride.me.R.id.massagePreference_yourGenderGroup)
    RadioGroup yourGenderGroup;

    @BindView(com.ride.me.R.id.massagePreference_durationGroup)
    RadioGroup durationGroup;

    @BindView(com.ride.me.R.id.massagePreference_therapistGroup)
    RadioGroup therapistGroup;

    @BindView(com.ride.me.R.id.massagePreference_next)
    Button nextButton;

    private MassagePreference preference;
    private MenuMassageItem massageItem;

    private MassageActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof MassageActivity) activity = (MassageActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.ride.me.R.layout.fragment_massage_preference, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        preference = new MassagePreference();
        preference.setIdGender(2);
        preference.setGender("Male");
        preference.setDurasi(1);
        preference.setDurasiText("60 minutes");
        preference.setIdTherapist(2);
        preference.setTherapist("Male");

        massageItem = activity.getMassageItem();
//        Picasso.with(getContext()).load(massageItem.getFoto()).fit().centerCrop().into(massagePicture);
        Glide.with(getContext()).load(massageItem.getFoto()).apply(new RequestOptions().centerCrop()
        ).into(massagePicture);
        massageDesc.setText(massageItem.getLayanan());

        yourGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case com.ride.me.R.id.massagePreference_yourGenderMale:
                        preference.setIdGender(2);
                        preference.setGender("Male");
                        break;
                    case com.ride.me.R.id.massagePreference_yourGenderFemale:
                        preference.setIdGender(1);
                        preference.setGender("Female");
                        break;
                }
            }
        });

        durationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case com.ride.me.R.id.massagePreference_duration60:
                        preference.setDurasi(1);
                        preference.setDurasiText("60 minutes");
                        break;
                    case com.ride.me.R.id.massagePreference_duration90:
                        preference.setDurasi(1.5);
                        preference.setDurasiText("90 minutes");
                        break;
                    case com.ride.me.R.id.massagePreference_duration120:
                        preference.setDurasi(2);
                        preference.setDurasiText("120 minutes");
                        break;
                }
            }
        });

        therapistGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case com.ride.me.R.id.massagePreference_therapistMale:
                        preference.setIdTherapist(2);
                        preference.setTherapist("Male");
                        break;
                    case com.ride.me.R.id.massagePreference_therapistFemale:
                        preference.setIdTherapist(1);
                        preference.setTherapist("Female");
                        break;
                    case com.ride.me.R.id.massagePreference_therapistNoPrefs:
                        preference.setIdTherapist(3);
                        preference.setTherapist("No Preferences");
                        break;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setMassagePreference(preference);
                activity.addFragmentBackstack(new ConfirmMassageFragment());
            }
        });
    }

}
