package com.example.vasbyfrisorenandroid.model.timeslot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.example.vasbyfrisorenandroid.R;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeSlotViewHolder> {


    private List<TimeSlot> timeSlotList;
    private int selectedItem;

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder{
        private TextView time;
        private CardView cardView;
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.text_time_slot);
            cardView = itemView.findViewById(R.id.card_time_slot);
        }
    }


    public TimeAdapter(List<TimeSlot> timeSlotList){
        this.timeSlotList = timeSlotList;
        this.selectedItem = 0;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot, parent, false);
        TimeSlotViewHolder timeSlotViewHolder = new TimeSlotViewHolder(view);
        return timeSlotViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlotList.get(position);
        holder.time.setText(timeSlot.getTime());

      if(selectedItem == position) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.white));
        }else{
            holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.grey));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(selectedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }
}
