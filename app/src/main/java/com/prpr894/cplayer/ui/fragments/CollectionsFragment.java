package com.prpr894.cplayer.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prpr894.cplayer.MyApp;
import com.prpr894.cplayer.R;
import com.prpr894.cplayer.adapters.recycleradapters.RoomRecyclerAdapter;
import com.prpr894.cplayer.base.BaseFragment;
import com.prpr894.cplayer.bean.LiveRoomItemDataBean;
import com.prpr894.cplayer.greendao.gen.LiveRoomItemDataBeanDao;
import com.prpr894.cplayer.interfaces.observer.ColletionDeleteListener;
import com.prpr894.cplayer.interfaces.observer.ListenerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsFragment extends BaseFragment implements ColletionDeleteListener, RoomRecyclerAdapter.OnRecyclerItemClickListener {

    private RecyclerView mRecyclerView;
    private RoomRecyclerAdapter mAdapter;
    private List<LiveRoomItemDataBean> mList;
    private LiveRoomItemDataBeanDao mBeanDao = MyApp.getInstance().getDaoSession().getLiveRoomItemDataBeanDao();

    public CollectionsFragment() {
        ListenerManager.getInstance().registerListtener(this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_collection);
        mList = new ArrayList<>();
        mList.addAll(mBeanDao.loadAll());
        mAdapter = new RoomRecyclerAdapter(mList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter.setOnRecyclerItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mList.clear();
        mList.addAll(mBeanDao.loadAll());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyAllActivity(LiveRoomItemDataBean bean) {
        mAdapter.remove(bean);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ListenerManager.getInstance().unRegisterListener(this);
    }

    @Override
    public void onRecyclerItemClick(RecyclerView recycler, View view, int position, long id, LiveRoomItemDataBean data) {
        mAdapter.playNow(data);
    }
}
