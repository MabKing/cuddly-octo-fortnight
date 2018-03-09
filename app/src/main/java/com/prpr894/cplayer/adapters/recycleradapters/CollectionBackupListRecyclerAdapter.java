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

public class CollectionBackupListRecyclerAdapter extends RecyclerView.Adapter<CollectionBackupListRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private List<CollectionBackupItemDataBean> list;
    private Context mContext;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private RecyclerView recyclerView;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public CollectionBackupListRecyclerAdapter(List<CollectionBackupItemDataBean> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_colletions_backup, parent, false);
        view.setOnClickListener(this);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView=recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView=null;
    }

    @Override
    public void onClick(View view) {
        if (recyclerView!=null&&onRecyclerItemClickListener!=null) {
            int position = recyclerView.getChildAdapterPosition(view);
            if (position>=0) {
                onRecyclerItemClickListener.onRecyclerItemClick(position,list.get(position),view);
            }
        }

    }

    public void remove(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(CollectionBackupItemDataBean data){
        int i = list.indexOf(data);
        if (i>=0) {
            remove(i);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewCollectionDate;

        ViewHolder(View itemView) {
            super(itemView);
            mTextViewCollectionDate = itemView.findViewById(R.id.tv_collection_date);
        }
    }

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(int position,CollectionBackupItemDataBean data,View view);
    }

}
