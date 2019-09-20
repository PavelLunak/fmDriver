package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.listeners.OnCallCanceledListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_load)
public class FragmentLoad extends Fragment {

    @ViewById
    ProgressBar load_progress;

    @ViewById
    TextView load_label;

    @ViewById
    Button btnCancel;

    @InstanceState
    @FragmentArg
    String labelText;

    @InstanceState
    @FragmentArg
    int taskType;

    OnCallCanceledListener cancelListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            cancelListener = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        if (labelText != null) load_label.setText(labelText);
    }

    public void setLabel(String text) {
        labelText = text;
        if (load_label != null) load_label.setText(labelText);
    }

    @Click(R.id.btnCancel)
    void btnCancelClick() {
        if (cancelListener != null) cancelListener.callCanceled(taskType);
    }
}
