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
    private OnTimeListener onTimeListener;
    private int selectedPos;

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView time;
        private CardView cardView;
        private OnTimeListener onTimeListener;

        public TimeSlotViewHolder(@NonNull View itemView, OnTimeListener onTimeListener) {
            super(itemView);
            time = itemView.findViewById(R.id.text_time_slot);
            cardView = itemView.findViewById(R.id.card_time_slot);
            this.onTimeListener = onTimeListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(getAdapterPosition() == RecyclerView.NO_POSITION){return;}

            notifyItemChanged(selectedPos);
            selectedPos = getAdapterPosition();
            notifyItemChanged(selectedPos);

            onTimeListener.onTimeClick(v, getAdapterPosition());
        }
    }


    public TimeAdapter(List<TimeSlot> timeSlotList, OnTimeListener onTimeListener){
        this.timeSlotList = timeSlotList;
        this.onTimeListener = onTimeListener;
        this.selectedPos = 0;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot, parent, false);
        TimeSlotViewHolder timeSlotViewHolder = new TimeSlotViewHolder(view, onTimeListener);
        return timeSlotViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlotList.get(position);
        holder.time.setText(timeSlot.getTime());

        if(selectedPos == position){
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.white));
            holder.time.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }else{
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.grey));
            holder.time.setTextColor(holder.itemView.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }
}
