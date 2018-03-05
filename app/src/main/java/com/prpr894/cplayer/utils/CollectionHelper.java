package com.prpr894.cplayer.utils;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;
import com.prpr894.cplayer.interfaces.observer.ColletionDeleteListener;
import com.prpr894.cplayer.interfaces.observer.ListenerManager;

import java.util.List;

import es.dmoral.toasty.MyToast;

/**
 * 收藏功能的工具类
 *
 * @author prpr894
 */
public class CollectionHelper implements View.OnClickListener, ColletionDeleteListener {
    private LiveRoomItemDataBeanDao mBeanDao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();
    private ImageView mImageView;
    private LiveRoomItemDataBean mBean;

    public CollectionHelper(ImageView imageView, LiveRoomItemDataBean bean) {
        ListenerManager.getInstance().registerListtener(this);
        mImageView = imageView;
        mBean = bean;
        checkState(mImageView);
        mImageView.setOnClickListener(this);
    }

    public void checkState(ImageView imageView) {
        if (checkBean()) {
            imageView.setImageResource(R.drawable.collection_true);
        } else {
            imageView.setImageResource(R.drawable.collection_false);
        }
    }

    private boolean checkBean() {
        List<LiveRoomItemDataBean> beans = mBeanDao.loadAll();
        if (beans == null) {
            return false;
        }
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getUserid().equals(mBean.getUserid())) {
                return true;
            }
        }
        return false;
    }

    private void changeCollectionState() {
        if (checkBean()) {
            Log.d("flag", "执行了删除 " + mBean.getNickname());
            mBeanDao.delete(mBean);
            ListenerManager.getInstance().sendBroadCast(mBean);
            MyToast.successBig("成功删除：\n" + mBean.getNickname());
        } else {
            Log.d("flag", "执行了收藏 " + mBean.getNickname());
            mBeanDao.insert(mBean);
            MyToast.successBig("成功收藏：\n" + mBean.getNickname());
        }
        checkState(mImageView);
    }

    @Override
    public void onClick(View v) {
        Log.d("flag", "点击了收藏按钮");
        changeCollectionState();
    }

    @Override
    public void notifyAllActivity(LiveRoomItemDataBean bean) {

    }
}
