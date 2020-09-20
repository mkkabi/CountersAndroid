package com.mkkabi.countersandyj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mkkabi.countersandyj.adapters.OnSwipeTouchListener;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.fragments.CountersReciclerViewFragment;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import java.util.List;
import java.util.Objects;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;
import static com.mkkabi.countersandyj.controller.Const.HOUSEHOLD_LIST_POSITION;
import static com.mkkabi.countersandyj.controller.Const.POSITION_NOT_SET;

public class HouseActivity extends AppCompatActivity {
    int houseNubmer = POSITION_NOT_SET;
    List<Counter> counters;
    Context context;
    Activity activity;
    String COUNTERS_FRAGMENT = "COUNTERS_FRAGMENT";
    FragmentManager fragmentManager;
    Fragment countersRecyclerFragment;
    View houseConstraintLayout;
//    private TextView noCountersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle(DataController.activeHouse.getName());

        activity = this;
        context = HouseActivity.this;
        Intent intent = getIntent();

        fragmentManager = getSupportFragmentManager();

//        noCountersText = findViewById(R.id.noCountersText);
        houseNubmer = intent.getIntExtra(HOUSEHOLD_LIST_POSITION, POSITION_NOT_SET);

        if (houseNubmer != POSITION_NOT_SET) {
            counters = Household.households.get(houseNubmer).getCounters();
            displayCounters();
        } else {
            LoggerToaster.printD(context, DEBUG_TAG, "house number is not set, value " + houseNubmer);
        }

        houseConstraintLayout = findViewById(R.id.houseConstraintLayout);
        houseConstraintLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                LoggerToaster.printD(context, DEBUG_TAG, "swiped left");
                onSupportNavigateUp();
            }
        });
    }


    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void displayCounters() {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            countersRecyclerFragment = CountersReciclerViewFragment.newInstance(houseNubmer, "Counters List");
            transaction.replace(R.id.fragment_placeholder, countersRecyclerFragment, COUNTERS_FRAGMENT).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCounters();
        LoggerToaster.printD(context, DEBUG_TAG, "onResume method called in House activity");
    }

    private Fragment getCurrentFragment() {
        // default fragment
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(countersRecyclerFragment.getId());
        if (fragmentManager.getBackStackEntryCount() >= 0) {
            activeFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
        }
        return activeFragment;
    }
}
