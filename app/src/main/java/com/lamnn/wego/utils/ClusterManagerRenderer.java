package com.lamnn.wego.utils;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.ClusterMarker;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final ImageView mImageView;
    private final IconGenerator mIconGenerator;
    private final Context mContext;
    private ClusterManager mClusterManager;
    private static final String TAG = "RENDERER";
    Bitmap mBitmap;

    public ClusterManagerRenderer(Context context, GoogleMap map,
                                  ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mIconGenerator = new IconGenerator(context);
        mClusterManager = clusterManager;
        mImageView = new ImageView(context);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        mImageView.setPadding(2, 2, 2, 2);
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarker item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        mBitmap = mIconGenerator.makeIcon("lamnn");
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mBitmap)).title(item.getUser().getName());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }

    @Override
    protected void onClusterItemRendered(final ClusterMarker clusterItem, final Marker marker) {
        GlideApp.with(mContext)
                .load(clusterItem.getUser().getPhotoUri())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //on load failed
                        Log.d(TAG, "onLoadFailed: ");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //on load success
                        mImageView.setImageDrawable(resource);
                        mBitmap = mIconGenerator.makeIcon();
                        if (marker != null) {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(mBitmap));
                        }
                        Log.d(TAG, "onResourceReady: ");
                        return false;
                    }
                })
                .into(mImageView);
        marker.setTag(clusterItem.getUser());

    }

    @Override
    protected void onClusterRendered(Cluster<ClusterMarker> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    public void setUpdateMarker(ClusterMarker updateMarker) {
        Marker marker = getMarker(updateMarker);
        if (marker != null) {
            marker.setPosition(updateMarker.getPosition());
            marker.setTag(updateMarker.getUser());
        }
    }

    private Bitmap createCustomMarker(Context context, String photoUrl) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom, null);
        final ImageView markerImage = marker.findViewById(R.id.user_dp);
        GlideApp.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_sort_up)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //on load failed
                        Log.d(TAG, "onLoadFailed: ");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //on load success
                        mImageView.setImageDrawable(resource);
                        markerImage.setImageDrawable(resource);
                        mClusterManager.cluster();
                        Log.d(TAG, "onResourceReady: ");
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
}

