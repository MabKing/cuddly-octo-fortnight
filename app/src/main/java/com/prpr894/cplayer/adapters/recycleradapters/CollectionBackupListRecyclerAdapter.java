package com.prpr894.cplayer.adapters.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prpr894.cplayer.R;
import com.prpr894.cplayer.bean.CollectionBackupItemDataBean;

import java.util.List;

public class CollectionBackupListRecyclerAdapter extends RecyclerView.Adapter<CollectionBackupListRecyclerAdapter.ViewHolder> {

    private List<CollectionBackupItemDataBean> list;
    private Context mContext;

    public CollectionBackupListRecyclerAdapter(List<CollectionBackupItemDataBean> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_colletions_backup, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewCollectionDate.setText(list.get(position).getBackupDate());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewCollectionDate;

        ViewHolder(View itemView) {
            super(itemView);
            mTextViewCollectionDate = itemView.findViewById(R.id.tv_collection_date);
        }
    }
}
