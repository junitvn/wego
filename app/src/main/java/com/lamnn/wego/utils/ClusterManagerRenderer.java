package com.lamnn.wego.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.ClusterMarker;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.util.ArrayList;
import java.util.List;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final ImageView mImageView;
    private final IconGenerator mIconGenerator;
    private final Context mContext;
    private UserLocation mUserLocation;
    private static final String TAG = "RENDERER";
    Bitmap mBitmap;

    public ClusterManagerRenderer(Context context, GoogleMap map,
                                  ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mIconGenerator = new IconGenerator(context);
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom, null);
        mImageView = marker.findViewById(R.id.user_dp);
        mIconGenerator.setContentView(marker);
    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarker item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if (item.getUserLocation().getUser() != null) {
            mBitmap = createCustomMarker(mContext, item.getUserLocation().getUser().getPhotoUri());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mBitmap));
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }

    @Override
    protected void onClusterItemRendered(final ClusterMarker clusterItem, final Marker marker) {
        if (clusterItem.getUserLocation().getUser() != null) {
            GlideApp.with(mContext)
                    .load(clusterItem.getUserLocation().getUser().getPhotoUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            Log.d(TAG, "onLoadFailed: on rendered");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success
                            mImageView.setImageDrawable(resource);
                            if (marker != null) {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(mBitmap));
                            }
                            Log.d(TAG, "onResourceReady: cluster item rendered");
                            return false;
                        }
                    })
                    .into(mImageView);
            marker.setTag(clusterItem.getUserLocation());
        }
        if (mUserLocation != null && mUserLocation.getUid().equals(clusterItem.getUserLocation().getUid())) {
            marker.showInfoWindow();
        }
    }

    @Override
    protected void onClusterRendered(Cluster<ClusterMarker> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    public void setUpdateMarker(final ClusterMarker updateMarker) {
        final Marker marker = getMarker(updateMarker);
        if (marker != null) {
            final UserLocation userLocation = updateMarker.getUserLocation();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (userLocation.getUser() != null) {
                db.collection("events")
                        .whereEqualTo("trip_id", userLocation.getUser().getActiveTrip())
                        .whereEqualTo("user_id", userLocation.getUser().getUid())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                List<Event> events = new ArrayList<>();
                                Event event;
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Gson gson = new Gson();
                                    JsonElement jsonElement = gson.toJsonTree(doc.getData());
                                    event = gson.fromJson(jsonElement, Event.class);
                                    Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                                    event.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                                    event.setEventId(doc.getId());
                                    if (event.getStatus().equals("waiting")) {
                                        events.add(event);
                                    }
                                }
                                userLocation.setEvents(events);
                                marker.setPosition(updateMarker.getPosition());
                                marker.setTag(userLocation);
                            }
                        });
            }
        }
    }

    private Bitmap createCustomMarker(Context context, String photoUrl) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom, null);
        final ImageView markerImage = marker.findViewById(R.id.user_dp);
        GlideApp.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.ic_user_wait)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_sort_up)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //on load failed
                        Log.d(TAG, "onLoadFailed: on create bitmap");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        mClusterManager.cluster();
                        Log.d(TAG, "onResourceReady: con create bitmap");
                        return false;
                    }
                })
                .into(markerImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }

    public void showUserInfoWindow(UserLocation userLocation) {
        mUserLocation = userLocation;
    }
}

