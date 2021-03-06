package com.ride.me.mSend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ride.me.GoridemeAplication;
import com.ride.me.api.service.BookService;
import com.ride.me.model.Driver;
import com.ride.me.model.json.book.GetNearRideCarRequestJson;

import com.ride.me.api.MapDirectionAPI;
import com.ride.me.api.ServiceGenerator;
import com.ride.me.gmap.directions.Directions;
import com.ride.me.gmap.directions.Route;
import com.ride.me.mMart.PlaceAutocompleteAdapter;
import com.ride.me.model.Fitur;
import com.ride.me.model.User;
import com.ride.me.model.json.book.GetNearRideCarResponseJson;
import com.ride.me.splash.SplashActivity;
import com.ride.me.utils.Log;

import java.io.IOException;
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

import static com.ride.me.config.General.BOUNDS;
import static com.ride.me.utils.Utility.round;

public class SendActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "mSendFiturKey";
    private static final String TAG = "mSendActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(com.ride.me.R.id.mSend_title)
    TextView title;
    @BindView(com.ride.me.R.id.btn_home)
    ImageView backButton;
    @BindView(com.ride.me.R.id.mSend_pickUpContainer)
    LinearLayout setPickUpContainer;
    @BindView(com.ride.me.R.id.mSend_destinationContainer)
    LinearLayout setDestinationContainer;
    @BindView(com.ride.me.R.id.mSend_pickUpButton)
    Button setPickUpButton;
    @BindView(com.ride.me.R.id.mSend_destinationButton)
    Button setDestinationButton;
    @BindView(com.ride.me.R.id.mSend_pickUpText)
    AutoCompleteTextView pickUpText;
    @BindView(com.ride.me.R.id.mSend_destinationText)
    AutoCompleteTextView destinationText;
    @BindView(com.ride.me.R.id.mSend_detail)
    LinearLayout detail;
    @BindView(com.ride.me.R.id.price_container)
    RelativeLayout priceContainer;
    @BindView(com.ride.me.R.id.mSend_distance)
    TextView distanceText;
    @BindView(com.ride.me.R.id.mSend_price)
    TextView priceText;
//    @BindView(R.id.mSend_paymentGroup)
//    RadioGroup paymentGroup;
//    @BindView(R.id.mSend_mPayPayment)
//    RadioButton mPayButton;
//    @BindView(R.id.mSend_cashPayment)
//    RadioButton cashButton;
//    @BindView(R.id.mSend_topUp)
//    Button topUpButton;
    @BindView(com.ride.me.R.id.mSend_next)
    Button nextButton;
//    @BindView(R.id.mSend_mPayBalance)
//    TextView mPayBalanceText;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private ArrayList<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private Realm realm;
    private Fitur designedFitur;
    private double jarak;
    private double timeDistance = 0;
    private long harga;
    private long saldoMpay;
    private boolean isMapReady = false;
    private PlaceAutocompleteAdapter mAdapter;
    SupportMapFragment mapFragment;
    int fiturId;

//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(SendActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(SendActivity.this, json);
                if (distance >= 0) {
                    SendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time / 60;
                            detail.setVisibility(View.VISIBLE);

                        }
                    });
                }else {
                    detail.setVisibility(View.GONE);
                }
            }
        }
    };

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ride.me.R.layout.activity_send);
        ButterKnife.bind(this);

        detail.setVisibility(View.GONE);
        setPickUpContainer.setVisibility(View.GONE);
        setDestinationContainer.setVisibility(View.GONE);


        User userLogin = new User();
        if(GoridemeAplication.getInstance(this).getLoginUser() != null){
            userLogin = GoridemeAplication.getInstance(this).getLoginUser();
        }else{
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
        saldoMpay = userLogin.getmPaySaldo();

        pickUpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setPickUpContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        destinationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setDestinationContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });


        setPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpClick();
            }
        });

        setDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestinationClick();

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.ride.me.R.id.mSend_mapView);
        mapFragment.getMapAsync(this);

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        if(intent.hasExtra(FITUR_KEY)){
            fiturId = intent.getIntExtra(FITUR_KEY, -1);

            if (fiturId != -1)
                designedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();
        }else{
            designedFitur = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
        }



//        setupFitur();
        updateFitur();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextButtonClick();
            }
        });

        setupAutocompleteTextView();

//        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (paymentGroup.getCheckedRadioButtonId()) {
//                    case R.id.mSend_mPayPayment:
//                        long biayaTotal = harga / 2;
//                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
//                        String formattedText = String.format(Locale.US, "$. %s ,-", formattedTotal);
//                        priceText.setText(formattedText);
//                        break;
//                    case R.id.mSend_cashPayment:
//                        biayaTotal = harga;
//                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
//                        formattedText = String.format(Locale.US, "$. %s ,-", formattedTotal);
//                        priceText.setText(formattedText);
//                        break;
//                }
//            }
//        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupAutocompleteTextView() {
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        pickUpText.setAdapter(mAdapter);
        pickUpText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) SendActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(pickUpText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if(latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onPickUpClick();
                        }
                    }
                });

            }
        });

        destinationText.setAdapter(mAdapter);
        destinationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) SendActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(destinationText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if(latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onDestinationClick();
                        }
                    }
                });
            }
        });

    }

    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }


    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
//        String formattedText = String.format(Locale.US, "Rp. %s ,-",
//                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
//        mPayBalanceText.setText(formattedText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {

            }
        }
    }

    private void cancelOrder() {

    }


    private void updateFitur() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (isMapReady) updateLastLocation(false);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        isMapReady = true;

        updateLastLocation(true);
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
            fetchNearDriver();
        }
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = GoridemeAplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);

//            if (designedFitur.getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
//            } else if (designedFitur.getIdFitur() == 2) {
//                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
//                    @Override
//                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
//                        if (response.isSuccessful()) {
//                            driverAvailable = response.body().getData();
//                            createMarker();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {
//
//                    }
//                });
//            }
        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = GoridemeAplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                @Override
                public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                }
            });

        }
    }

    private void createMarker() {
        if (!driverAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driverAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                driverMarkers.add(
                        gMap.addMarker(new MarkerOptions()
                                .position(currentDriverPos)
                                .icon(BitmapDescriptorFactory.fromResource(com.ride.me.R.drawable.ic_m_ride_pin)))
                );

            }
        }
    }

    private void onNextButtonClick(){
        Intent intent = new Intent(this, AddDetailSendActivity.class);
        intent.putExtra("distance", jarak);//double
        intent.putExtra("price", harga);//long
        intent.putExtra("pickup_latlng", pickUpLatLang);
        intent.putExtra("destination_latlng", destinationLatLang);
        intent.putExtra("pickup", pickUpText.getText().toString());
        intent.putExtra("destination", destinationText.getText().toString());
        intent.putExtra("driver", driverAvailable);
        intent.putExtra("time_distance", timeDistance);
        intent.putExtra("driver", driverAvailable);
        intent.putExtra(FITUR_KEY, fiturId);

        startActivity(intent);
    }

//    private void onOrderButtonClick() {
//        switch (paymentGroup.getCheckedRadioButtonId()) {
//            case R.id.mSend_mPayPayment:
//                if (driverAvailable.isEmpty()) {
//                    AlertDialog dialog = new AlertDialog.Builder(SendActivity.this)
//                            .setMessage("Mohon maaf, tidak ada driver disekitar.")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            })
//                            .create();
//                    dialog.show();
//                } else {
//                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
//                    User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
//                    param.setIdPelanggan(userLogin.getId());
//                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
//                    param.setStartLatitude(pickUpLatLang.latitude);
//                    param.setStartLongitude(pickUpLatLang.longitude);
//                    param.setEndLatitude(destinationLatLang.latitude);
//                    param.setEndLongitude(destinationLatLang.longitude);
//                    param.setJarak(this.jarak);
//                    param.setHarga(this.harga/2);
//                    param.setAlamatAsal(pickUpText.getText().toString());
//                    param.setAlamatTujuan(destinationText.getText().toString());
//
//                    Log.e("M-PAY", "used");
//                    param.setPakaiMpay(1);
//
//                    Intent intent = new Intent(SendActivity.this, WaitingActivity.class);
//                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
//                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
//                    intent.putExtra("time_distance", timeDistance);
//                    startActivity(intent);
//                }
//
//
//                break;
//            case R.id.mSend_cashPayment:
//                if (driverAvailable.isEmpty()) {
//                    AlertDialog dialog = new AlertDialog.Builder(SendActivity.this)
//                            .setMessage("Mohon maaf, tidak ada driver disekitar.")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            })
//                            .create();
//                    dialog.show();
//                } else {
//                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
//                    User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
//                    param.setIdPelanggan(userLogin.getId());
//                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
//                    param.setStartLatitude(pickUpLatLang.latitude);
//                    param.setStartLongitude(pickUpLatLang.longitude);
//                    param.setEndLatitude(destinationLatLang.latitude);
//                    param.setEndLongitude(destinationLatLang.longitude);
//                    param.setJarak(this.jarak);
//                    param.setHarga(this.harga);
//                    param.setAlamatAsal(pickUpText.getText().toString());
//                    param.setAlamatTujuan(destinationText.getText().toString());
//
//
//                    Log.e("M-PAY", "not using m pay");
//
//
//                    Intent intent = new Intent(SendActivity.this, WaitingActivity.class);
//                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
//                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
//                    intent.putExtra("time_distance", timeDistance);
//                    startActivity(intent);
//                }
//                break;
//        }
//    }

    private void onDestinationClick() {
        if (destinationMarker != null) destinationMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(com.ride.me.R.drawable.ic_location_orange)));
        destinationLatLang = centerPos;

        fillAddress(destinationText, destinationLatLang);

        requestRoute();
    }

    private void onPickUpClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(com.ride.me.R.drawable.ic_location_blue2)));
        pickUpLatLang = centerPos;

        fillAddress(pickUpText, pickUpLatLang);

        destinationText.requestFocus();
        fetchNearDriver(pickUpLatLang.latitude, pickUpLatLang.longitude);
        requestRoute();
    }

    private void requestRoute() {
        if (pickUpLatLang != null && destinationLatLang != null) {
            MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang).enqueue(updateRouteCallback);
        }
    }

    private void fillAddress(final EditText editText, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(SendActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    SendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    editText.setText(address);
                                }
                            } else {
                                editText.setText(com.ride.me.R.string.text_addressNotAvailable);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(SendActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(SendActivity.this, com.ride.me.R.color.directionLine))
                        .width(5));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDistance(long distance) {
        detail.setVisibility(View.VISIBLE);
        priceContainer.setVisibility(View.VISIBLE);
        float km = ((float) distance) / 1000f;

        this.jarak = km;

        String format = String.format(Locale.US, "Distance (%.1f Km)", km);
        distanceText.setText(format);

        Log.i("JARAK MSEND", km+"");
        km = (float)round(km, 1);
        long biayaTotal = (long) (designedFitur.getBiaya() * Math.ceil(km));
        Log.i("JARAK MSEND", Math.ceil(km)+"");
        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;

        this.harga = biayaTotal;
        if(biayaTotal < designedFitur.getBiaya_minimum()){
            this.harga = designedFitur.getBiaya_minimum();
            biayaTotal = designedFitur.getBiaya_minimum();
        }

//        if(mPayButton.isChecked()){
//            biayaTotal /= 2;
//        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        priceText.setText(formattedText);


//        if(saldoMpay < (harga/2)){
//            mPayButton.setEnabled(false);
//            cashButton.toggle();
//        }else {
//            mPayButton.setEnabled(true);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
