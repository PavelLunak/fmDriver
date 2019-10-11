package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.adapters.AdapterCheckedPositions;
import com.example.fmdriver.customViews.DialogYesNo;
import com.example.fmdriver.listeners.OnAllCheckedPositionsLoadedListener;
import com.example.fmdriver.listeners.OnAllItemsDeletedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.listeners.OnYesNoDialogSelectedListener;
import com.example.fmdriver.objects.PositionChecked;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;


@EFragment(R.layout.fragment_checked_positions)
public class FragmentCheckedPositions extends Fragment implements AppConstants {

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView labelPagesCount;

    @ViewById
    ImageView imgArrowLeft, imgArrowRight, imgSortByDate, imgSortByLine, imgDeleteAllItems;

    @InstanceState
    int pageNumber;

    @InstanceState
    long pagesCount;

    MainActivity activity;
    AdapterCheckedPositions adapter;

    @FragmentArg
    ArrayList<PositionChecked> items;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {

        updateFragment(null);

        /*
        float tempPagesCountDouble = 0;
        if (activity.getMaxItemsPerPage() > 0) {
            if (items != null) {
                if (!items.isEmpty()) {
                    tempPagesCountDouble = items.size() / activity.getMaxItemsPerPage();
                }
            }
        }

        //Test, jestli počet vychází na celé stránky
        boolean isEntireNumber = (tempPagesCountDouble % activity.getMaxItemsPerPage() == 0);
        pagesCount = (long) tempPagesCountDouble;

        if (!isEntireNumber) {
            pagesCount++;
        }

        labelPagesCount.setText("" + (pageNumber + 1) + "/" + pagesCount);
        */
    }

    @Click(R.id.imgArrowRight)
    void clickArrowRight() {
        Animators.animateButtonClick(imgArrowRight);

        /*
        activity.showLoadDialog(true);
        if ((pageNumber + 1) < pagesCount) {
            trackerDataSource.getCheckedPositions(
                    activity.getMaxItemsPerPage() * (pageNumber + 1),
                    activity.getMaxItemsPerPage(),
                    new TrackerDataSource.OnCheckedPositionsLoadedListener() {
                        @Override
                        public void onLoaded(List<PositionChecked> positions) {
                            MainActivity.itemsCheckedPositions = new ArrayList<>(positions);
                            items = new ArrayList<PositionChecked>(positions);
                            pageNumber ++;
                            labelPagesCount.setText("" + (pageNumber + 1) + "/" + pagesCount);
                            updateAdapter();
                            activity.showLoadDialog(false);
                        }
                    });
        } else {
            Toast.makeText(activity, "Jsi na poslední straně", Toast.LENGTH_LONG).show();
            activity.showLoadDialog(false);
        }
        */
    }

    @Click(R.id.imgArrowLeft)
    void clickArrowLeft() {
        Animators.animateButtonClick(imgArrowLeft);

        /*
        if (pageNumber > 0) {
            activity.showLoadDialog(true);
            trackerDataSource.getCheckedPositions(
                    activity.getMaxItemsPerPage() * (pageNumber) - activity.getMaxItemsPerPage(),
                    activity.getMaxItemsPerPage(),
                    new TrackerDataSource.OnCheckedPositionsLoadedListener() {
                        @Override
                        public void onLoaded(List<PositionChecked> positions) {
                            MainActivity.itemsCheckedPositions = new ArrayList<>(positions);
                            items = new ArrayList<PositionChecked>(positions);
                            pageNumber --;
                            labelPagesCount.setText("" + (pageNumber + 1) + "/" + pagesCount);
                            updateAdapter();
                            activity.showLoadDialog(false);
                        }
                    });
        } else {
            Toast.makeText(activity, "Jsi na první straně", Toast.LENGTH_LONG).show();
            activity.showLoadDialog(false);
        }
        */
    }

    @Click(R.id.imgSortByDate)
    void clickSortByDate() {
        Animators.animateButtonClick(imgSortByDate);
        //prepareDataByDate();
    }

    @Click(R.id.imgSortByLine)
    void clickSortByLine() {
        Animators.animateButtonClick(imgSortByLine);
        //prepareDataByLine();
    }

    @Click(R.id.imgDeleteAllItems)
    void clickDeleteAll() {
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

    /*
    public void prepareDataByDate() {

        activity.showFrgmentLoad("Stahuji data", new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                activity.getAllCheckedPositions(new OnAllCheckedPositionsLoadedListener() {
                    @Override
                    public void onAllCheckedPositionsLoaded(ArrayList<PositionChecked> items) {
                        if (items == null) {
                            activity.closeFragmentLoad(null);
                            activity.showPositionsGroups(null);
                            return;
                        }

                        if (items.isEmpty()) {
                            activity.closeFragmentLoad(null);
                            activity.showPositionsGroups(null);
                            return;
                        }

                        ArrayList<PositionsCheckedGroup> outputData = new ArrayList<>();
                        PositionsCheckedGroup group = new PositionsCheckedGroup();

                        PositionChecked position = items.get(0);
                        group.addPosition(position);

                        String lastDate = DateTimeUtils.getDate(position.getDate());

                        if (items.size() > 1) {
                            for (int i = 1, count = items.size(); i < count; i ++) {
                                position = items.get(i);
                                String positionDate = DateTimeUtils.getDate(position.getDate());

                                if (!positionDate.equals(lastDate)) {
                                    outputData.add(group);

                                    group = new PositionsCheckedGroup();
                                    group.addPosition(position);
                                } else {
                                    group.addPosition(position);

                                    if (i == count - 1) {
                                        outputData.add(group);
                                    }
                                }

                                lastDate = positionDate;
                            }
                        }

                        outputData.add(group);

                        final ArrayList<PositionsCheckedGroup> result = new ArrayList<>(outputData);

                        activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
                            @Override
                            public void onFragmentLoadClosed() {
                                activity.showPositionsGroups(result);
                            }
                        });
                    }
                });
            }
        });
    }
    */

    /*
    private void prepareDataByLine() {

        activity.showFrgmentLoad("Stahuji data", new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                activity.getAllCheckedPositions(new OnAllCheckedPositionsLoadedListener() {
                    @Override
                    public void onAllCheckedPositionsLoaded(ArrayList<PositionChecked> items) {
                        if (items == null) {
                            activity.closeFragmentLoad(null);
                            activity.showPositionsGroups(null);
                            return;
                        }

                        if (items.isEmpty()) {
                            activity.closeFragmentLoad(null);
                            activity.showPositionsGroups(null);
                            return;
                        }

                        ArrayList<PositionsCheckedGroup> outputData = new ArrayList<>();

                        PositionsCheckedGroup group = new PositionsCheckedGroup();
                        group.addPosition(items.get(0));

                        PositionChecked position;

                        for (int i = items.size() - 2; i >= 0; i --) {
                            position = items.get(i);

                            long timeFrom = items.get(i + 1).getDate();
                            long timeTo = position.getDate();
                            int currentDelay = (int) ((timeTo - timeFrom) / 1000 / 60);

                            if (currentDelay >= activity.appPrefs.stopDelay().get()) {
                                outputData.add(group);
                                group = new PositionsCheckedGroup();
                                group.addPosition(position);
                            } else {
                                group.addPosition(position);
                            }
                        }

                        outputData.add(group);
                        Collections.reverse(outputData);

                        final ArrayList<PositionsCheckedGroup> result = new ArrayList<>(outputData);

                        activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
                            @Override
                            public void onFragmentLoadClosed() {
                                activity.showPositionsGroups(result);
                            }
                        });
                    }
                });
            }
        });
    }
    */
}
