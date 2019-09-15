package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.utils.Animators;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_token)
public class FragmentToken extends Fragment {

    MainActivity activity;

    @FragmentArg
    String token;

    @ViewById
    TextView labelToken;

    @ViewById
    TextView btnClose;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        labelToken.setText(token);
        Log.i("ukaz_token", token);
    }

    @Click(R.id.btnClose)
    void clickBack() {
        Animators.animateButtonClick(btnClose);
        activity.onBackPressed();
    }
}
