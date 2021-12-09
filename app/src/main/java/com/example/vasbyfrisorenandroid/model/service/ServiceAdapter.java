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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements View.OnClickListener {

    private List<Service> serviceList;

    public static class ServiceViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView serviceTitle, servicePrice;
        private CardView serviceCardView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            serviceTitle = itemView.findViewById(R.id.title);
            servicePrice = itemView.findViewById(R.id.price);
            serviceCardView = itemView.findViewById(R.id.serviceCardView);
        }
    }

    public ServiceAdapter(List<Service> serviceList){
        this.serviceList = serviceList;
    }
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service, parent, false);
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(view);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.img.setImageResource(service.getImgResource());
        holder.serviceTitle.setText(service.getServiceTitle());
        holder.servicePrice.setText(String.valueOf(service.getPrice()) + ":-");
        holder.serviceCardView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }



    @Override
    public void onClick(View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        switch (v.getId()){

            case R.id.serviceCardView:
                Fragment fragment = new BookingFragment();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            break;
        }
    }
}
