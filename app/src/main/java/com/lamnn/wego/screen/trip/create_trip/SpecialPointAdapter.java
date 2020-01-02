package com.lamnn.wego.screen.trip.create_trip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.place.Point;

import java.util.ArrayList;
import java.util.List;

import static com.lamnn.wego.utils.AppUtils.TYPE_CHECK_IN;

public class SpecialPointAdapter extends RecyclerView.Adapter<SpecialPointAdapter.ViewHolder> {
    private Context mContext;
    private List<Point> mPoints;
    private LayoutInflater mLayoutInflater;

    public SpecialPointAdapter(Context context, List<Point> points) {
        mContext = context;
        mPoints = points;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_special_point, parent, false);
        return new ViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mPoints.get(position));
    }

    @Override
    public int getItemCount() {
        return mPoints != null ? mPoints.size() : 0;
    }

    public void updateData(ArrayList<Point> points) {
        mPoints.clear();
        mPoints.addAll(points);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private TextView mTextViewName;
        private TextView mTextViewType;
        private ImageView mImageViewType;

        public ViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            mContext = context;
            mTextViewName = itemView.findViewById(R.id.text_item_point_name);
            mTextViewType = itemView.findViewById(R.id.text_item_point_type);
            mImageViewType = itemView.findViewById(R.id.image_item_point_type);
        }

        private void onBindData(Point point) {
            if (point != null) {
                if (point.getName().equals(mContext.getString(R.string.unnamed_road))) {
                    mTextViewName.setText(R.string.unknown_place);
                } else {
                    mTextViewName.setText(point.getName());
                }
                if (point.getType().equals(TYPE_CHECK_IN)) {
                    mTextViewType.setText(R.string.check_in);
                    mImageViewType.setImageResource(R.drawable.ic_pin_drop_blue_24dp);
                } else {
                    mTextViewType.setText(R.string.waypoint);
                    mImageViewType.setImageResource(R.drawable.ic_pin_drop_orange_24dp);
                }
            }
        }
    }
}
