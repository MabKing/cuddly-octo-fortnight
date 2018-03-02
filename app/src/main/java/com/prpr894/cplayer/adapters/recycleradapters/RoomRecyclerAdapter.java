package com.prpr894.cplayer.adapters.recycleradapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.cloud.videoplayer.demo.AdvancedPlayActivity;
import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.bean.StationListItemDataBean;
import com.prpr894.cplayer.ui.activities.MainPlayerActivity;
import com.prpr894.cplayer.utils.CollectionHelper;
import com.prpr894.cplayer.utils.SPUtil;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_BAI_DU;
import static com.prpr894.cplayer.utils.AppConfig.PLAY_TYPE_SAO_ZI;

public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private List<LiveRoomItemDataBean> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public RoomRecyclerAdapter(List<LiveRoomItemDataBean> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_room, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectionHelper collectionHelper = new CollectionHelper(holder.mImageViewCollection, mList.get(position));
        collectionHelper.checkState(holder.mImageViewCollection);
        holder.mImageViewCollection.setTag(collectionHelper);
        holder.mTextViewStationName.setText(mList.get(position).getNickname());
        Picasso.with(mContext)
                .load(mList.get(position).getLogourl())
                .placeholder(R.drawable.ic_img_loading)
                .into(holder.mImageViewIcon);
    }


    public void checkCollectionState() {
        if (mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                View child = mRecyclerView.getChildAt(i);
                if (child != null) {
                    ViewHolder childViewHolder = (ViewHolder) mRecyclerView.getChildViewHolder(child);
                    CollectionHelper helper = (CollectionHelper) childViewHolder.mImageViewCollection.getTag();
                    helper.checkState(childViewHolder.mImageViewCollection);
                }
            }
        }
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


    public void addAll(Collection<? extends LiveRoomItemDataBean> collection) {
        int size = mList.size();
        mList.addAll(collection);
        notifyItemRangeInserted(size, collection.size());
        checkCollectionState();
    }

    public void remove(LiveRoomItemDataBean data) {
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

    public void playNow(LiveRoomItemDataBean data) {
        Intent intent;
        switch (SPUtil.getString(MyApp.getInstance(), PLAY_TYPE, PLAY_TYPE_BAI_DU)) {
            case PLAY_TYPE_BAI_DU:
                intent = new Intent(mContext, AdvancedPlayActivity.class);
                break;
            case PLAY_TYPE_SAO_ZI:
                intent = new Intent(mContext, MainPlayerActivity.class);
                break;
            default:
                intent = new Intent(mContext, AdvancedPlayActivity.class);
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("videoUrl", data.getPlay_url());
        bundle.putString("imgUrl", data.getLogourl());
        bundle.putString("title", data.getNickname());
        bundle.putString("id", data.getUserid());
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewIcon, mImageViewCollection;
        private TextView mTextViewStationName;

        ViewHolder(View itemView) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.img_room_icon);
            mImageViewCollection = itemView.findViewById(R.id.img_room_collection);
            mTextViewStationName = itemView.findViewById(R.id.tv_room_name);
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(RecyclerView recycler, View view, int position, long id, LiveRoomItemDataBean data);
    }
}
