package com.mkkabi.countersandyj.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.consumer.SlidingConsumer;
import com.mkkabi.countersandyj.CounterActivity;
import com.mkkabi.countersandyj.CounterDayNight;
import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.fragments.EditCounterFragment;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mkkabi.countersandyj.controller.Const.COUNTER_LIST_POSITION;
import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.ViewHolder> {

    FragmentManager fragmentManager;
    private LayoutInflater inflater;
    private List<Counter> counters;
    private Context context;
    private ConstraintLayout houseConstraintLayout;
    private RecyclerView recyclerView;
    private SmartSwipeWrapper swipe;

    public CounterAdapter(Context context, FragmentManager fragMan, List<Counter> counters, RecyclerView recyclerView) {
        this.context = context;
        this.counters = counters;
        this.inflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.fragmentManager = fragMan;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.counteradapterconstrained, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(counters.get(position));
    }

    @Override
    public int getItemCount() {
        return counters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView counterImage, deleteCounterImage, editCounterImage;
        //        final TextView counterName, counterRate;
        TextView counterTypeText, accountNumberText, overallSpentTextView, lastPaymentText, lastDataText, savedTotalText,
                overallSpentValue, lastPaymentValue, lastDataValue, savedTotalValue;
        ConstraintLayout constrained;

        ViewHolder(View view) {
            super(view);
            counterTypeText = view.findViewById(R.id.counterTypeText);
            accountNumberText = view.findViewById(R.id.accountNumberText);
            overallSpentTextView = view.findViewById(R.id.overallSpentTextView);
            lastPaymentText = view.findViewById(R.id.lastPaymentText);
            lastDataText = view.findViewById(R.id.lastDataText);
            savedTotalText = view.findViewById(R.id.savedTotalText);

            overallSpentValue = view.findViewById(R.id.overallSpentTextValue);
            lastPaymentValue = view.findViewById(R.id.lastPaymentValue);
            lastDataValue = view.findViewById(R.id.lastDataValue);
            savedTotalValue = view.findViewById(R.id.savedTotalValue);

            counterImage = view.findViewById(R.id.counterImage);
            deleteCounterImage = view.findViewById(R.id.deleteCounterImage);
            editCounterImage = view.findViewById(R.id.editCounterImage);
            constrained = view.findViewById(R.id.constrainedCounter);
            swipe = view.findViewById(R.id.main_ui_wrap_view2);
        }

        void bind(Counter counter) {
            counterImage.setImageResource(counter.getImage());
            counterTypeText.setText(context.getResources().getString(counter.getCounterType().getNameResource()));
//            counterTypeText.append(context.getResources().getString(counter.getCounterType().getNameResource()));
            accountNumberText.setText(" " + counter.getAccountNumber());

            overallSpentValue.setText(" " + counter.getOverallSpendings());
            lastPaymentValue.setText(" " + counter.getLastPayment());
            lastDataValue.setText(" " + counter.getOverallConsumption());
            if(counter.getOverallSavings()>0){
                savedTotalText.setVisibility(TextView.VISIBLE);
                savedTotalValue.setVisibility(TextView.VISIBLE);
                savedTotalValue.setText(" "+counter.getOverallSavings());
            }

            constrained.setOnClickListener(v -> {
                DataController.activeCounter = counter;
                if (counter.getCounterType() == CounterType.DAYNIGHT) {
                    LoggerToaster.printD(context, DEBUG_TAG, "Day night counter selected");
                    Intent counterIntent = new Intent(context, CounterDayNight.class);
                    counterIntent.putExtra(COUNTER_LIST_POSITION, counter.getCounterHouse().getCounters().indexOf(counter));
                    context.startActivity(counterIntent);
                } else {
                    Intent counterIntent = new Intent(context, CounterActivity.class);
                    counterIntent.putExtra(COUNTER_LIST_POSITION, counter.getCounterHouse().getCounters().indexOf(counter));
                    context.startActivity(counterIntent);
                }
            });

            constrained.setOnLongClickListener(v -> {
                LoggerToaster.printD(context, DEBUG_TAG, "long click from CounterAdapter");
                return true;
            });


            deleteCounterImage.setOnClickListener(v -> DataController.deleteCounter(counter, context, CounterAdapter.this::notifyDataSetChanged));
            // TODO replace with edit Counter Fragment
            editCounterImage.setOnClickListener(v -> showEditCounterFragment(counter));

            swipe.addConsumer(new SlidingConsumer()).setRelativeMoveFactor(SlidingConsumer.FACTOR_FOLLOW);

        }

        private void showEditCounterFragment(Counter counter) {
            EditCounterFragment editCounterFragment = EditCounterFragment.newInstance(counter);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_placeholder, editCounterFragment);
            fragmentTransaction.addToBackStack("editCounterFragment").commit();

        }
    }
}
