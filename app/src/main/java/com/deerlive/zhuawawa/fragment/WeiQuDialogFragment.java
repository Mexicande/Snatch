package com.deerlive.zhuawawa.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.intf.WeiQuInterface;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeiQuDialogFragment extends DialogFragment {


    @Bind(R.id.tv_text)
    TextView tvText;
    @Bind(R.id.view)
    View view;
    private WeiQuInterface  anInterface;
    private static String BLANCE="blance";
    private static String NU="nu";

    public static WeiQuDialogFragment newInstance(String nu,String blance) {
        WeiQuDialogFragment instance = new WeiQuDialogFragment();
        Bundle args = new Bundle();
        args.putString(NU, nu);
        args.putString(BLANCE, blance);
        instance.setArguments(args);
        return instance;
    }

    public WeiQuDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wei_qu_dialog, container, false);
        ButterKnife.bind(this, view);
        String nu = getArguments().getString(NU);
        String blance = getArguments().getString(BLANCE);
        tvText.setText("共选中"+nu+"件奖品，可兑换"+blance+"金币");
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        anInterface= (WeiQuInterface) context;
    }

    @OnClick({R.id.cancel, R.id.sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                anInterface.requestCancel();
                break;
            case R.id.sure:
                dismiss();
                anInterface.requestSure();
                break;
            default:
                break;
        }
    }
}
