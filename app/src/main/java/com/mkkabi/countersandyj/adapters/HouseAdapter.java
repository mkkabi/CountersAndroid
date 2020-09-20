package com.mkkabi.countersandyj.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.consumer.SlidingConsumer;
import com.mkkabi.countersandyj.CounterActivity;
import com.mkkabi.countersandyj.CounterDayNight;
import com.mkkabi.countersandyj.HouseActivity;
import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.UtilitiesAccounting;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.controller.Serializer;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mkkabi.countersandyj.controller.Const.COUNTER_LIST_POSITION;
import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;
import static com.mkkabi.countersandyj.controller.Const.HOUSEHOLD_LIST_POSITION;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Household> houses;

    public HouseAdapter(Context context, List<Household> houses) {
        this.context = context;
        this.houses = houses;
        this.inflater = LayoutInflater.from(context);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.houseadapterconstrained, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(houses.get(position));
    }

    @Override
    public int getItemCount() {
        return houses.size() <= 0 ? 0 : houses.size();
    }

    private void showEditHousePopUp(Household house) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_name);
        EditText editText = new EditText(context);
        editText.setText(house.getName());
        editText.setHintTextColor(context.getResources().getColor(R.color.secondaryTextColor));
        editText.setHint(R.string.enter_name);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(50, 25, 50, 25);
        layout.addView(editText);
        builder.setView(layout);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            house.setName(editText.getText().toString());
            Serializer.saveApp(context);
            notifyDataSetChanged();
        });

        builder.setNegativeButton(R.string.cancel, (d, w) -> {
        });
        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView houseImage, deleteHouseholdImage, editHouseholdImage;
        final TextView houseName, countersNumber;
        LinearLayout houseCounters;
        SmartSwipeWrapper swipe;
        ConstraintLayout constrained;

        ViewHolder(View view) {
            super(view);
            houseName = view.findViewById(R.id.houseName);
            countersNumber = view.findViewById(R.id.countersNumber);
            houseCounters = view.findViewById(R.id.houseCounters);
            houseImage = view.findViewById(R.id.houseImage);
            deleteHouseholdImage = view.findViewById(R.id.deleteHouseholdImage);
            editHouseholdImage = view.findViewById(R.id.editHouseholdImage);
            swipe = view.findViewById(R.id.main_ui_wrap_view2);
            constrained = view.findViewById(R.id.constrained);
        }

        void bind(Household house) {
            System.out.println("bind method called");
            houseCounters.removeAllViews();
            houseName.setText(house.getName().toUpperCase());
            countersNumber.setText(String.format("%s", house.getCounters().size()));
            houseImage.setImageResource(R.drawable.greenhome);

            constrained.setOnClickListener(v -> {
                System.out.println("clicked " + house.getName());
                DataController.activeHouse = house;
                Intent householdActivity = new Intent(context, HouseActivity.class);
                householdActivity.putExtra(HOUSEHOLD_LIST_POSITION, DataController.households.indexOf(house));
                context.startActivity(householdActivity);
            });

            deleteHouseholdImage.setOnClickListener(v -> DataController.removeHousehold(house, context, HouseAdapter.this::notifyDataSetChanged));

            editHouseholdImage.setOnClickListener(v -> showEditHousePopUp(house));

            swipe.addConsumer(new SlidingConsumer()).setRelativeMoveFactor(SlidingConsumer.FACTOR_FOLLOW);

            if (house.getCounters() != null) {
                for (int i = 0; i < house.getCounters().size(); i++) {
                    int counterNumber = i;
                    Counter counter = (Counter) house.getCounters().get(counterNumber);
                    ImageView image = new ImageView(context);

                    image.setImageResource(counter.getImage());
                    image.setClickable(true);
                    houseCounters.addView(image);

                    image.setOnClickListener(v -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            TypedValue outValue = new TypedValue();
                            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                            image.setBackgroundResource(outValue.resourceId);
                        }

                        LoggerToaster.printD(context, DEBUG_TAG, "from House Adapter \n selected counter ID: " + counter.getID() +
                                "\nHouseID: " + counter.getCounterHouse().getID());
                        DataController.activeHouse = house;
                        DataController.activeCounter = counter;
                        if (counter.getCounterType() == CounterType.DAYNIGHT){
                            LoggerToaster.printD(context, DEBUG_TAG, "Day night counter selected");
                            Intent counterIntent = new Intent(context, CounterDayNight.class);
                            counterIntent.putExtra(COUNTER_LIST_POSITION, counter.getCounterHouse().getCounters().indexOf(counter));
                            context.startActivity(counterIntent);
                        }else {
                            Intent counterIntent = new Intent(UtilitiesAccounting.getAppContext(), CounterActivity.class);
                            counterIntent.putExtra(COUNTER_LIST_POSITION, counterNumber);
                            context.startActivity(counterIntent);
                        }
                    });
                }
            }
        }
    }

}
