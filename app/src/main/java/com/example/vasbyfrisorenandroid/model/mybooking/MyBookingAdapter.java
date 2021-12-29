package com.example.vasbyfrisorenandroid.model.mybooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;

import java.util.List;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyBookingViewHolder> implements View.OnClickListener {


    public static class MyBookingViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView description, time, service;

        public MyBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.notification_img);
            description = itemView.findViewById(R.id.mybooking_description);
            time = itemView.findViewById(R.id.mybooking_time);
            service = itemView.findViewById(R.id.mybooking_service);
        }
    }

    private List<MyBooking> myBookingList;

    public MyBookingAdapter(List<MyBooking> myBookingList){
        this.myBookingList = myBookingList;
    }

    @NonNull
    @Override
    public MyBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybooking_item, parent, false);
        MyBookingViewHolder myBookingViewHolder = new MyBookingViewHolder(view);
        return myBookingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyBookingViewHolder holder, int position) {
        MyBooking myBooking = myBookingList.get(position);
        holder.img.setImageResource(myBooking.getService().getImgResource());
        holder.description.setText("Tack för din bokning med " + myBooking.getBarber());
        holder.time.setText(myBooking.getBookedTime().getBookedDate() + ", " + myBooking.getBookedTime().getTimeTaken());
        holder.service.setText(myBooking.getService().getServiceTitle());
    }

    @Override
    public int getItemCount() {
        return myBookingList.size();
    }

    @Override
    public void onClick(View v) {

    }
}
