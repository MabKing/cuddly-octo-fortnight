package com.prpr894.cplayer.adapters.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.bean.StationListItemDataBean;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

public class StationRecyclerAdapter extends RecyclerView.Adapter<StationRecyclerAdapter.ViewHolder> {

    private List<StationListItemDataBean> mList;
    private Context mContext;

    public StationRecyclerAdapter(List<StationListItemDataBean> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_station, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewStationName.setText(mList.get(position).getName());
        holder.mTextViewNum.setText(mList.get(position).getFjs());
        Picasso.with(mContext)
                .load(mList.get(position).getImg())
                .into(holder.mImageViewIcon);

    }

    public void addAll(Collection<? extends StationListItemDataBean> collection) {
        int size = mList.size();
        mList.addAll(collection);
        notifyItemRangeInserted(size, collection.size());
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewIcon;
        private TextView mTextViewStationName, mTextViewNum;

        ViewHolder(View itemView) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.img_station_icon);
            mTextViewStationName = itemView.findViewById(R.id.tv_station_name);
            mTextViewNum = itemView.findViewById(R.id.tv_station_num);
        }
    }
}
