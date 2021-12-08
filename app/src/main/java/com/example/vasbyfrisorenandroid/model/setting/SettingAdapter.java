package com.example.vasbyfrisorenandroid.model.setting;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.fragment.CustomDialog;
import com.example.vasbyfrisorenandroid.fragment.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {

    private List<Setting> settingList;

    public static class SettingViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView typeOfSetting, description;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.settingImg);
            typeOfSetting = itemView.findViewById(R.id.typeOfSetting);
            description = itemView.findViewById(R.id.setting_description);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(setting.getTypeOfSetting()){

                    case "Email":
                        CustomDialog dialog = new CustomDialog();
                        dialog.show(((AppCompatActivity)view.getContext()).getSupportFragmentManager(), "Lololo");
                    break;

                    case "Logga ut":
                        signOut(view);
                    break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

    private void signOut(View view){
        FirebaseAuth.getInstance().signOut();

        GoogleSignIn.getClient(view.getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                .signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(view.getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((AppCompatActivity)view.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
    }

}
