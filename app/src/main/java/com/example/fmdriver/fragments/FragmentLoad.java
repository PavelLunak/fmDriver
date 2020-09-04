package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.listeners.OnCallCanceledListener;
import com.example.fmdriver.utils.Animators;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

//@EFragment(R.layout.fragment_load)
public class FragmentLoad extends Fragment {

    //@ViewById
    ProgressBar load_progress;

    //@ViewById
    TextView load_label;

    //@ViewById
    Button btnCancel;

    //@InstanceState
    //@FragmentArg
    String labelText;

    Bundle args;
    OnCallCanceledListener cancelListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            cancelListener = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_load, container, false);
        args = getArguments();
        if (args != null) labelText = args.getString("labelText");

        load_label = (TextView) rootView.findViewById(R.id.load_label);
        load_progress = (ProgressBar) rootView.findViewById(R.id.load_progress);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) cancelListener.callCanceled();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (labelText != null) load_label.setText(labelText);
    }

    /*
    @AfterViews
    void afterViews() {
        if (labelText != null) load_label.setText(labelText);
    }
    */

    public void setLabel(String text) {
        labelText = text;
        if (load_label != null) load_label.setText(labelText);
    }

    public void updateLabel(String text) {
        if (text == null) return;
        this.labelText = text;
        if (load_label != null) load_label.setText(this.labelText);
    }

    /*
    @Click(R.id.btnCancel)
    void btnCancelClick() {
        if (cancelListener != null) cancelListener.callCanceled(taskType);
    }
    */
}
