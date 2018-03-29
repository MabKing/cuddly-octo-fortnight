package com.prpr894.cplayer.adapters.recycleradapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.utils.SPUtil;

import java.util.List;
import java.util.Locale;

public class ChangeBaseRecyclerAdapter extends RecyclerView.Adapter<ChangeBaseRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> mList;
    private Context mContext;

    private int currentChoisePosition = -23333;

    public ChangeBaseRecyclerAdapter(List<String> list, Context context) {
        mList = list;
        mContext = context;
    }

    private RecyclerView mRecyclerView;

    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_base_url, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mTextViewChoose.setOnClickListener(this);
        holder.mTextViewChoose.setTag(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextViewUrl.setText(String.format(Locale.getDefault(), "源 %03d", position + 1));
        choiseJianCeZhong(holder);
        OkGo.<String>head(mList.get(position) + "/xyjk.html")//
//                .tag(this)//
//                .headers("header1", "headerValue1")//
//                .params("param1", "paramValue1")//
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("flag", mList.get(position) + "/xyjk.html  code: " + response.code());
                        if (response.code() == 200) {
                            choiceXuanZe(holder);
                        } else {
                            choiceBuKeYong(holder);
                        }
                        checkDefault(position, holder);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        choiceBuKeYong(holder);
                        checkDefault(position, holder);
                    }
                });
    }


    private void checkDefault(int position, ViewHolder holder) {
        if (!SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
            if (SPUtil.getString(MyApp.getInstance(), "customBase", "").equals(mList.get(position))) {
                choiceYiXuanZe(holder);
                currentChoisePosition = position;
            }
        }
    }

    public void refreshButton(View view, int position) {
        ViewHolder holder = (ViewHolder) mRecyclerView.getChildViewHolder(view);
        choiceYiXuanZe(holder);
        ViewHolder childViewHolder;
        if (currentChoisePosition != position && currentChoisePosition != -23333) {
            childViewHolder = (ViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(currentChoisePosition));
            choiceXuanZe(childViewHolder);
            currentChoisePosition = position;
        }
    }

    private void choiseJianCeZhong(ViewHolder holder) {
        holder.mTextViewChoose.setText("检测中...");
        holder.mTextViewChoose.setTextColor(Color.WHITE);
        holder.mTextViewChoose.setClickable(false);
    }

    private void choiceBuKeYong(ViewHolder holder) {
        holder.mTextViewChoose.setText("不可用");
        holder.mTextViewChoose.setTextColor(Color.RED);
        holder.mTextViewChoose.setClickable(false);
    }

    private void choiceXuanZe(ViewHolder holder) {
        holder.mTextViewChoose.setText("选择");
        holder.mTextViewChoose.setTextColor(Color.BLUE);
        holder.mTextViewChoose.setClickable(true);
    }

    private void choiceYiXuanZe(ViewHolder holder) {
        if (SPUtil.getBoolen(MyApp.getInstance(), "defaultBase", true)) {
            choiceXuanZe(holder);
        } else {
            holder.mTextViewChoose.setText("已选择");
            holder.mTextViewChoose.setTextColor(Color.GREEN);
            holder.mTextViewChoose.setClickable(false);
        }
    }

    public void toDefaultBaseUrl() {
        if (currentChoisePosition != -23333) {
            ViewHolder holder = (ViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(currentChoisePosition));
            choiceXuanZe(holder);
            currentChoisePosition = -23333;
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

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public void onClick(View v) {
        View view = (View) v.getTag();
        if (mOnRecyclerItemClickListener != null && mRecyclerView != null) {
            int position = mRecyclerView.getChildAdapterPosition(view);
//            int position = mRecyclerView.getChildLayoutPosition(v);
            if (position >= 0) {
                mOnRecyclerItemClickListener.onRecyclerItemClick(position, mList.get(position), view);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewUrl, mTextViewChoose;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewUrl = itemView.findViewById(R.id.tv_base_url);
            mTextViewChoose = itemView.findViewById(R.id.tv_base_choose);
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position, String data, View view);
    }
}
