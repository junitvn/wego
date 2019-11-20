package com.lamnn.wego.screen.trip.create_trip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Photo;
import com.lamnn.wego.data.model.Result;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    public static final int SIZE_IMAGE = 400;
    private List<Result> mResults;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnPlaceClickListener mListener;

    public PlaceAdapter(Context context, List<Result> results, OnPlaceClickListener listener) {
        mContext = context;
        mResults = results;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_place, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults != null ? mResults.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImagePlace;
        private TextView mTextNamePlace;
        private Context mContext;
        private OnPlaceClickListener mClickListener;
        private Result mResult;

        public ViewHolder(Context context, @NonNull View itemView, OnPlaceClickListener listener) {
            super(itemView);
            mContext = context;
            mClickListener = listener;
            mImagePlace = itemView.findViewById(R.id.image_place);
            mImagePlace.setOnClickListener(this);
            mTextNamePlace = itemView.findViewById(R.id.text_name_place);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onPlaceClick(mResult);
            }
        }

        private void bindData(Result result) {
            if (result == null) {
                return;
            }
            mResult = result;

            Photo photo = result.getPhotos().get(0);
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photo.getPhotoReference() + "&key=AIzaSyBC8ugpOmCpMNUwUv7S82wuBYgzjSlOoxY";
            GlideApp.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_up_arrow))
                    .into(mImagePlace);
            mTextNamePlace.setText(result.getName());
        }
    }

    interface OnPlaceClickListener {
        void onPlaceClick(Result result);
    }
}
