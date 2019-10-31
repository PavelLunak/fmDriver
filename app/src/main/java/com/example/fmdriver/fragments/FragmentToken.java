package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.utils.Animators;


public class FragmentToken extends Fragment {

    TextView labelToken,  btnClose;

    MainActivity activity;
    Bundle args;
    String token;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_token, container, false);
        args = getArguments();
        if (args != null) token = args.getString("token");

        labelToken = (TextView) rootView.findViewById(R.id.labelToken);
        btnClose = (TextView) rootView.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(btnClose);
                activity.onBackPressed();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        labelToken.setText(token);
        Log.i("ukaz_token", token);
    }
}
