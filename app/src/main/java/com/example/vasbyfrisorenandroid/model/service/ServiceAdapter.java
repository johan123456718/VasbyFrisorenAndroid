package com.example.vasbyfrisorenandroid.model.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.fragment.BookingFragment;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceListener onServiceListener;

    public static class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img;
        private TextView serviceTitle, servicePrice;
        private CardView serviceCardView;
        private OnServiceListener onServiceListener;

        public ServiceViewHolder(View itemView, OnServiceListener onServiceListener) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            serviceTitle = itemView.findViewById(R.id.title);
            servicePrice = itemView.findViewById(R.id.price);
            serviceCardView = itemView.findViewById(R.id.serviceCardView);
            this.onServiceListener = onServiceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onServiceListener.onServiceClick(v, getAdapterPosition());
        }
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceListener onServiceListener){
        this.serviceList = serviceList;
        this.onServiceListener = onServiceListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service, parent, false);
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(view, onServiceListener);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.img.setImageResource(service.getImgResource());
        holder.serviceTitle.setText(service.getServiceTitle());
        holder.servicePrice.setText(String.valueOf(service.getPrice()) + ":-");
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


}
