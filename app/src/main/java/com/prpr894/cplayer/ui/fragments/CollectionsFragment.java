package com.prpr894.cplayer.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prpr894.cplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsFragment extends Fragment {


    public CollectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections_fragment2, container, false);
        return view;
    }

}
