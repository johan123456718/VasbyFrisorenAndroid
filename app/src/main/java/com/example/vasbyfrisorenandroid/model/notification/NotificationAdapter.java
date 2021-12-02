package com.example.vasbyfrisorenandroid.model.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> implements View.OnClickListener {


    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView description, time;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.notification_img);
            description = itemView.findViewById(R.id.notification_description);
            time = itemView.findViewById(R.id.notification_time);
        }
    }

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList){
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        NotificationViewHolder notificationViewHolder = new NotificationViewHolder(view);
        return notificationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.img.setImageResource(notification.getImgResource());
        holder.description.setText(notification.getDescription());
        holder.time.setText(notification.getTime());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    @Override
    public void onClick(View v) {

    }
}
