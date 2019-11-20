package com.lamnn.wego.screen.event.create_event;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lamnn.wego.R;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

public class EventPhotoAdapter extends RecyclerView.Adapter<EventPhotoAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mUris;
    private Boolean mIsRemovable;
    private OnPhotoItemClickListener mListener;

    public EventPhotoAdapter(Context context, List<String> uris, OnPhotoItemClickListener listener, Boolean isRemovable) {
        mContext = context;
        mUris = uris;
        mListener = listener;
        mIsRemovable = isRemovable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_event_photo, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mUris.get(position), position, mIsRemovable);
    }

    @Override
    public int getItemCount() {
        return mUris == null ? 0 : mUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private ImageView mImageViewPhoto, mImageViewRemove;
        private Uri mUri;
        private int mPosition;
        private OnPhotoItemClickListener mListener;


        public ViewHolder(Context context, @NonNull View itemView, OnPhotoItemClickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mImageViewPhoto = itemView.findViewById(R.id.image_event_photo);
            mImageViewPhoto.setOnClickListener(this);
            mImageViewRemove = itemView.findViewById(R.id.image_event_remove_photo);
            mImageViewRemove.setOnClickListener(this);
        }

        private void bindData(String uri, int position, Boolean isRemovable) {
            if (uri == null) return;
            mUri = Uri.parse(uri);
            mPosition = position;
            GlideApp.with(mContext)
                    .load(mUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.giphy)
                    .into(mImageViewPhoto);
            if (!isRemovable) {
                mImageViewRemove.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_event_photo:
                    mListener.onPhotoItemClick(mUri);
                    break;
                case R.id.image_event_remove_photo:
                    mListener.onRemoveClick(mPosition);
                    break;
            }
        }
    }

    public interface OnPhotoItemClickListener {
        void onPhotoItemClick(Uri uri);

        void onRemoveClick(int position);
    }
}
