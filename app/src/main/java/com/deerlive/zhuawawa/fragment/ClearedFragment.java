package com.deerlive.zhuawawa.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deerlive.zhuawawa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClearedFragment extends Fragment {


    public ClearedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cleared, container, false);
    }

}
