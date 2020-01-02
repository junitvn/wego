package com.lamnn.wego.screen.trip.create_trip;

import android.content.Context;

import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.trip.create_trip.share_code.ShareCodeActivity;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTripPresenter implements CreateTripContract.Presenter {
    private Context mContext;
    private CreateTripContract.View mView;
    private TripService mTripService;

    public CreateTripPresenter(Context context, CreateTripContract.View view) {
        mContext = context;
        mView = view;
    }

    public void createTrip(final Trip trip) {
        showLoading();
        mTripService = APIUtils.getTripService();
        mTripService.createTrip(trip).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                hideLoading();
                mContext.startActivity(ShareCodeActivity.getIntent(mContext, trip));
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
            }
        });
    }

    @Override
    public void showLoading() {
        mView.showLoading();
    }

    @Override
    public void hideLoading() {
        mView.hideLoading();
    }


}
