package com.lamnn.wego.screen.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.EventStatus;
import com.lamnn.wego.data.model.Location;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private UserLocation mUserLocation;
    private Event mEvent;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private String TAG = "INFO WINDOW";

    public InfoWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getInfoContents(Marker marker) {
        UserLocation userLocation = (UserLocation) marker.getTag();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUserLocation = userLocation;
        View v = inflater.inflate(R.layout.item_info_window_linear, null);
        if (mUserLocation != null) {
            String status;
            if (userLocation.getStatus().equals("online")) {
                status = mContext.getString(R.string.now_online);
            } else {
                MyTimeStamp myTimeStamp = userLocation.getTimeStamp();
                status = printDifference(new Date(Long.parseLong(myTimeStamp.getSeconds()) * 1000), new Date(), mContext);
            }
            String address = getAddressByLatLng(userLocation.getLocation()).equals("")
                    ? mContext.getString(R.string.unknown_place)
                    : getAddressByLatLng(userLocation.getLocation());
            v.setClipToOutline(true);
            TextView textName = v.findViewById(R.id.text_info_name);
            if (mUserLocation.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                textName.setText(mContext.getString(R.string.text_me));
            } else textName.setText(mUserLocation.getUser().getName());
            TextView textAddress = v.findViewById(R.id.text_info_address);
            textAddress.setText(address);
            TextView textStatus = v.findViewById(R.id.text_info_status);
            textStatus.setText(status);
            String eventStatusInfo;
            TextView textViewEventStatus = v.findViewById(R.id.text_info_event_status);
            if (mUserLocation.getEvents() != null && mUserLocation.getEvents().size() > 0) {
                textViewEventStatus.setVisibility(View.VISIBLE);
                List<Event> events = mUserLocation.getEvents();
                if (events.size() == 1) {
                    eventStatusInfo = events.get(0).getTitle();
                } else {
                    eventStatusInfo = events.get(events.size() - 1).getTitle() + mContext.getString(R.string.and)
                            + (events.size() - 1) + mContext.getString(R.string.other_event);
                }
                textViewEventStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            } else {
                textViewEventStatus.setVisibility(View.VISIBLE);
                eventStatusInfo = mContext.getString(R.string.no_event_yet);
                textViewEventStatus.setTextColor(mContext.getResources().getColor(R.color.colorButtonEnable));
            }
            if (mUserLocation.getEventStatuses() != null) {
                for (EventStatus eventStatus : mUserLocation.getEventStatuses()) {
                    if (eventStatus.getTripId().equals(mUserLocation.getUser().getActiveTrip())) {
                        textViewEventStatus.setVisibility(View.VISIBLE);
                        String eventStatusValue;
                        mEvent = eventStatus.getEvent();
                        eventStatusValue = eventStatus.getStatus();
                        switch (eventStatusValue) {
                            case "coming":
                                eventStatusInfo = mContext.getString(R.string.going_to) + mEvent.getUser().getName() + mContext.getString(R.string.s_place);
                                textViewEventStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "waiting":
                                eventStatusInfo = mContext.getString(R.string.waiting_for) + mEvent.getUser().getName();
                                textViewEventStatus.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                break;
                        }
                    }
                }
            }
            textViewEventStatus.setText(eventStatusInfo);
        }
        return v;
    }

    private String getAddressByLatLng(Location location) {
        String address = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLat(), location.getLng(), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static String printDifference(Date startDate, Date endDate, Context context) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String res = "";
        if (elapsedDays > 0) {
            res = res.concat(elapsedDays + context.getString(R.string.text_days));
        }
        if (elapsedHours > 0) {
            res = res.concat(elapsedHours + context.getString(R.string.text_hours));
        }
        res = res.concat(elapsedMinutes + context.getString(R.string.text_minutes));
        return res;
    }

}
