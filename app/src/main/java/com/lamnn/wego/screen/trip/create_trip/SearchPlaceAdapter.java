package com.lamnn.wego.screen.trip.create_trip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Result;

import java.util.List;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.ViewHolder> {
    private List<Result> mResults;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SearchPlaceAdapter.OnItemSearchPlaceClickListener mListener;

    public SearchPlaceAdapter(Context context, List<Result> results, SearchPlaceAdapter.OnItemSearchPlaceClickListener listener) {
        mContext = context;
        mResults = results;
        mListener = listener;
    }

    @NonNull
    @Override
    public SearchPlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_search_place, parent, false);
        return new SearchPlaceAdapter.ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPlaceAdapter.ViewHolder holder, int position) {
        holder.bindData(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults != null ? mResults.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextNamePlace;
        private Context mContext;
        private SearchPlaceAdapter.OnItemSearchPlaceClickListener mClickListener;
        private Result mResult;

        public ViewHolder(Context context, @NonNull View itemView, SearchPlaceAdapter.OnItemSearchPlaceClickListener listener) {
            super(itemView);
            mContext = context;
            mClickListener = listener;
            mTextNamePlace = itemView.findViewById(R.id.text_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemSearchPlaceClick(mResult);
            }
        }

        private void bindData(Result result) {
            if (result == null) {
                return;
            }
            mResult = result;
            mTextNamePlace.setText(result.getName());
        }
    }

    interface OnItemSearchPlaceClickListener {
        void onItemSearchPlaceClick(Result result);
    }
}
