package com.example.vasbyfrisorenandroid.model.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> implements View.OnClickListener {

    private List<Setting> settingList;

    public static class SettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView img;
        private TextView typeOfSetting, description;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.settingImg);
            typeOfSetting = itemView.findViewById(R.id.typeOfSetting);
            description = itemView.findViewById(R.id.setting_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), typeOfSetting.getText(), Toast.LENGTH_LONG).show();
        }
    }


    public SettingAdapter(List<Setting> settingList){
        this.settingList = settingList;
    }


    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting, parent, false);
        SettingViewHolder settingViewHolder = new SettingViewHolder(view);
        return settingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        Setting setting = settingList.get(position);
        holder.img.setImageResource(setting.getImgResource());
        holder.typeOfSetting.setText(setting.getTypeOfSetting());
        holder.description.setText(setting.getDescription());
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }


    @Override
    public void onClick(View v) {
        //not used for anything
    }
}
