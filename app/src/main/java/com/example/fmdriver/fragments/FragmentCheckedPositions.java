package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.adapters.AdapterCheckedPositions;
import com.example.fmdriver.customViews.DialogYesNo;
import com.example.fmdriver.listeners.OnAllItemsDeletedListener;
import com.example.fmdriver.listeners.OnYesNoDialogSelectedListener;
import com.example.fmdriver.objects.PositionChecked;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;

import java.util.ArrayList;


public class FragmentCheckedPositions extends Fragment implements AppConstants {

    RecyclerView recyclerView;
    TextView labelPagesCount;
    ImageView imgArrowLeft, imgArrowRight, imgSortByDate, imgSortByLine, imgDeleteAllItems;

    MainActivity activity;
    AdapterCheckedPositions adapter;

    Bundle args;
    ArrayList<PositionChecked> items;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checked_positions, container, false);
        args = getArguments();
        if (args != null) items = args.getParcelableArrayList("itemsCheckedPositions");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        labelPagesCount = (TextView) rootView.findViewById(R.id.labelPagesCount);
        imgArrowLeft = (ImageView) rootView.findViewById(R.id.imgArrowLeft);
        imgArrowRight = (ImageView) rootView.findViewById(R.id.imgArrowRight);
        imgDeleteAllItems = (ImageView) rootView.findViewById(R.id.imgDeleteAllItems);

        imgArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgDeleteAllItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(imgDeleteAllItems);

                DialogYesNo
                        .createDialog(activity)
                        .setTitle("!!!").setMessage("Opravdu vymazat všechny získané polohy z databáze?")
                        .setListener(new OnYesNoDialogSelectedListener() {
                            @Override
                            public void onYesSelected() {
                                activity.deleteAllItems(new OnAllItemsDeletedListener() {
                                    @Override
                                    public void onAllItemsDeletedListener() {
                                        items.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onNoSelected() {}
                        }).show();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    private void setViews() {
        updateFragment(null);
    }

    public void updateFragment(ArrayList<PositionChecked> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            updateAdapter();
        }
    }

    public void updateAdapter() {
        if (items != null) {
            adapter = new AdapterCheckedPositions(activity, items);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
    }
}
