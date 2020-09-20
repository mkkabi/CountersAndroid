package com.mkkabi.countersandyj.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.adapters.CounterAdapter;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class CountersReciclerViewFragment extends Fragment {
    private FragmentActivity listener;
    private List<Counter> counters;
    private RecyclerView countersRecyclerView;
    private Context context;
    private CounterAdapter adapter;
    private TextView noCountersText;

    public static CountersReciclerViewFragment newInstance(int someInt, String someTitle) {
        CountersReciclerViewFragment fragmentDemo = new CountersReciclerViewFragment();
        Bundle args = new Bundle();
        args.putInt("houseNumber", someInt);
        args.putString("someTitle", someTitle);
        fragmentDemo.setArguments(args);

        return fragmentDemo;
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.listener = (FragmentActivity) context;
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counters = DataController.activeHouse.getCounters();
        setHasOptionsMenu(true);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.available_counters);
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        initViews(view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        countersRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new CounterAdapter(context, getActivity().getSupportFragmentManager(), counters, countersRecyclerView);
        countersRecyclerView.setAdapter(adapter);
        countersRecyclerView.refreshDrawableState();

    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.counterslist_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_create_counter:
                showAddCounterFragment();
                return true;
            case R.id.action_faq:
                DataController.showInfoDialog(getActivity(), R.string.faq_text);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(View view) {
        countersRecyclerView = view.findViewById(R.id.recyclerView);
        noCountersText = view.findViewById(R.id.noCountersText);
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        Toast.makeText(context, "from Counteers Fragment, fab clicked", Toast.LENGTH_SHORT).show();
    }

    private void showAddCounterFragment(){
        CreateCounterFragment testFragment = new CreateCounterFragment();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, testFragment);
        fragmentTransaction.addToBackStack("createcounter").commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(counters.size() > 0){
            noCountersText.setVisibility(TextView.GONE);
        }
//        countersRecyclerView.refreshDrawableState();
//        countersRecyclerView.refreshDrawableState();

        LoggerToaster.printD(context, DEBUG_TAG, "onResume from CounterRecycler Fragment");
    }

}