package com.lamnn.wego.screen.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Location;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private User mUser;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private String TAG = "INFO WINDOW";

    public InfoWindowAdapter() {
    }

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
        User user = (User) marker.getTag();
        mUser = user;

        String status = "";
        if (user.getStatus().equals("online")) {
            status = "Now online";
        } else {
            MyTimeStamp myTimeStamp = user.getTimeStamp();
            status = printDifference(new Date(Long.parseLong(myTimeStamp.getSeconds()) * 1000), new Date());
        }
        String address = getAddressByLatLng(user.getLocation()).equals("")
                ? "Unknown place"
                : getAddressByLatLng(user.getLocation());
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_info_window_linear, null);
        v.setClipToOutline(true);
        TextView textName = v.findViewById(R.id.text_info_name);
        if (mUser.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            textName.setText("Me");
        } else textName.setText(mUser.getName());
        TextView textAddress = v.findViewById(R.id.text_info_address);
        textAddress.setText(address);
        TextView textStatus = v.findViewById(R.id.text_info_status);
        textStatus.setText(status);
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

    public static String printDifference(Date startDate, Date endDate) {
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
            res = res.concat(elapsedDays + " days, ");
        }
        if (elapsedHours > 0) {
            res = res.concat(elapsedHours + " hours, ");
        }
        res = res.concat(elapsedMinutes + " minutes ago");
        return res;
    }

}
