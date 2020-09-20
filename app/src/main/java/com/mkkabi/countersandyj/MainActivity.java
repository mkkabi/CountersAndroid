package com.mkkabi.countersandyj;


import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mkkabi.countersandyj.adapters.HouseAdapter;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.controller.Serializer;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import java.util.List;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class MainActivity extends AppCompatActivity {

    private static List<Household> houses = Household.households;
    private static DataController dataController = DataController.getInstance();
    private RecyclerView housesRecyclerView;
    private Context context;
    private HouseAdapter adapter;
    private TextView noHousesText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        initViews();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> {
            addNewHouse();
        });

        if (DataController.debugOn) {
            Toast message = Toast.makeText(context, "onCreate method called in main activity", Toast.LENGTH_LONG);
            message.show();
        }

    }

    private void addNewHouse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.create_household);
        EditText editText = new EditText(context);
        editText.setHintTextColor(getResources().getColor(R.color.secondaryTextColor));
        editText.setHint(R.string.enter_name);


        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(50, 25, 50, 25);
        layout.addView(editText);
        builder.setView(layout);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            DataController.createNewHousehold(context, editText.getText().toString());
            housesRecyclerView.refreshDrawableState();
            housesRecyclerView.getAdapter().notifyDataSetChanged();
        });

        builder.setNegativeButton(R.string.cancel, (d, w) -> {
        });
        AlertDialog dialog = builder.create();

        dialog.show();
        noHousesText.setVisibility(TextView.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoggerToaster.printD(context, DEBUG_TAG, "onResume method called in main activity");

        // refresh recycler view if changes were made in other activities
        if (DataController.needToRefresh()) {
            housesRecyclerView.refreshDrawableState();
            this.adapter.notifyDataSetChanged();
            dataController.refreshDone();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_new) {
            addNewHouse();
            return true;
        }
        if (id == R.id.action_faq) {
            DataController.showInfoDialog(MainActivity.this, R.string.faq_text);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews() {
        housesRecyclerView = findViewById(R.id.housesRecyclerView);
        noHousesText = findViewById(R.id.noHousesText);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        housesRecyclerView.setLayoutManager(mLayoutManager);
        //creating adapter for Houses
        adapter = new HouseAdapter(this, houses);
        //setting up adapter for the houses list
        housesRecyclerView.setAdapter(adapter);
        housesRecyclerView.refreshDrawableState();
        Serializer.getInstance();
        if (houses.size() == 0) {
            noHousesText.setVisibility(TextView.VISIBLE);
        }
    }


}
