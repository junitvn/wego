package com.lamnn.wego.screen.trip.create_trip;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.RouteResponse;
import com.lamnn.wego.data.remote.DirectionService;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DirectionService mDirectionService;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDirectionService = APIUtils.getDirectionService();
        getDirection();
    }

    private void getDirection() {
        LatLng latLngOrigin = new LatLng(10.3181466, 123.9029382);// Ayala
        LatLng latLngDes = new LatLng(10.311795, 123.915864); // SM City
        mDirectionService.getRoute(latLngOrigin.toString(), latLngDes.toString(), "AIzaSyCfigxwS6gPrQUWn43WYq2269GuYRv3T2k").enqueue(new Callback<RouteResponse>() {
            private static final String TAG = "HIHIHI";

            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                Log.d(TAG, "hihi" + response.body());
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.d(TAG, "hihi" + t);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
