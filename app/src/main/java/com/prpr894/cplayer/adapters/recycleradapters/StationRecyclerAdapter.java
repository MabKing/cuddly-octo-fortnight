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

public class StationRecyclerAdapter extends RecyclerView.Adapter<StationRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private List<StationListItemDataBean> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public StationRecyclerAdapter(List<StationListItemDataBean> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_station, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewStationName.setText(mList.get(position).getTitle());
        holder.mTextViewNum.setText(mList.get(position).getNumber());
        Picasso.with(mContext)
                .load(mList.get(position).getXinimg())
                .placeholder(R.drawable.ic_img_loading)
                .into(holder.mImageViewIcon);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }


    public void addAll(Collection<? extends StationListItemDataBean> collection) {
        int size = mList.size();
        mList.addAll(collection);
        notifyItemRangeInserted(size, collection.size());
    }

    public void remove(StationListItemDataBean data) {
        int i = mList.indexOf(data);
        if (i >= 0) {
            remove(i);
        }
    }

    public void remove(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public void onClick(View v) {
        if (mRecyclerView != null && mOnRecyclerItemClickListener != null) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            if (position >= 0) {
                long id = getItemId(position);
                mOnRecyclerItemClickListener.onRecyclerItemClick(mRecyclerView, v, position, id, mList.get(position));
            }
        }
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

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(RecyclerView recycler, View view, int position, long id, StationListItemDataBean data);
    }
}
