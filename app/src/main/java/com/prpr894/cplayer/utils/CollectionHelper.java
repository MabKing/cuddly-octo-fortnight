package com.prpr894.cplayer.utils;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;

import java.util.List;

import es.dmoral.toasty.MyToast;

/**
 * 收藏功能的工具类
 *
 * @author prpr894
 */
public class CollectionHelper implements View.OnClickListener {
    private LiveRoomItemDataBeanDao mBeanDao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();
    private ImageView mImageView;
    private LiveRoomItemDataBean mBean;

    public CollectionHelper(ImageView imageView, LiveRoomItemDataBean bean) {
        mImageView = imageView;
        mBean = bean;
        checkState();
        mImageView.setOnClickListener(this);
    }

    private void checkState() {
        if (checkBean()) {
            mImageView.setImageResource(R.drawable.collection_true);
        } else {
            mImageView.setImageResource(R.drawable.collection_false);
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
            MyToast.successBig("成功删除：\n" + mBean.getNickname());
        } else {
            Log.d("flag", "执行了收藏 " + mBean.getNickname());
            mBeanDao.insert(mBean);
            MyToast.successBig("成功收藏：\n" + mBean.getNickname());
        }
        checkState();
    }

    @Override
    public void onClick(View v) {
        Log.d("flag", "点击了收藏按钮");
        changeCollectionState();
    }
}
