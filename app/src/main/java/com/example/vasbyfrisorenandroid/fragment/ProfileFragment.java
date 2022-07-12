package com.example.vasbyfrisorenandroid.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.setting.OnSettingListener;
import com.example.vasbyfrisorenandroid.model.setting.Setting;
import com.example.vasbyfrisorenandroid.model.setting.SettingAdapter;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener, OnSettingListener {
    private View rootView;
    private List<Setting> settingList;
    private RecyclerView settingRecyclerView;
    private RecyclerView.Adapter settingAdapter;
    private LinearLayoutManager layoutManager;
    private FirebaseAuth auth;
    private TextView profileName, profileEmail, accountTitle;
    private ImageView backButton;
    private Button bookingButton, settingButton;
    private EditText currentPassword;
    private SearchView searchSettings;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);
        auth = FirebaseAuth.getInstance();
        profileName = rootView.findViewById(R.id.profile_name);
        profileEmail = rootView.findViewById(R.id.profile_email);
        backButton = rootView.findViewById(R.id.backbutton);
        bookingButton = rootView.findViewById(R.id.profile_booking);
        settingButton = rootView.findViewById(R.id.profile_settings);
        accountTitle = rootView.findViewById(R.id.account_title);
        searchSettings = rootView.findViewById(R.id.search_settings);
        creatingSettingList();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            profileName.setText(user.getDisplayName());
            profileEmail.setText(user.getEmail());
        }

        backButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        bookingButton.setOnClickListener(this);

        searchSettings.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        settingList.clear();
    }

    private void creatingSettingList() {
        settingList = new ArrayList<>();
        DatabaseReference dbUsersReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        dbUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                settingList.add(new Setting(R.drawable.email_icon, "Email", Objects.requireNonNull(auth.getCurrentUser()).getEmail()));
                settingList.add(new Setting(R.drawable.lock_icon, "Lösenord", "Här kan du ändra ditt lösenord"));
                settingList.add(new Setting(R.drawable.contact, "Telefon", (String) snapshot.child("phone_nr").getValue()));
                settingList.add(new Setting(R.drawable.person_stroke, "Radera konto", "Här kan du ta bort ditt konto"));
                settingList.add(new Setting(0, "Logga ut", null));
                settingRecyclerView = rootView.findViewById(R.id.recyclerView);
                settingRecyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(rootView.getContext());
                settingAdapter = new SettingAdapter(settingList, ProfileFragment.this);
                settingRecyclerView.setLayoutManager(layoutManager);
                settingRecyclerView.setAdapter(settingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.backbutton:

                Fragment fragment = new HomeFragment();

                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                break;

            case R.id.profile_booking:

                accountTitle.setText(R.string.my_booking);
                break;

            case R.id.profile_settings:

                accountTitle.setText(R.string.account_settings);
                break;
        }
    }

    @Override
    public void onSettingClick(View view, int position) {
        Setting setting = settingList.get(position);

        switch (setting.getTypeOfSetting()) {
            case "Email":
            case "Telefon":
            case "Lösenord":
                showUpdateDialog(setting.getTypeOfSetting());
                break;

            case "Radera konto":
                    openQuestionDialog();
                break;

            case "Logga ut":
                Log.d("test", "onSettingClick: 123");
                auth.signOut();
                Fragment fragment = new HomeFragment();

                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                break;
        }
    }

    private void showUpdateDialog(String typeOfSetting) {
        Dialog dialog = new Dialog(rootView.getContext(), R.style.DialogStyle);
        dialog.setContentView(R.layout.layout_custom_dialog);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        EditText txtBox1 = dialog.findViewById(R.id.edit_txtBox1);
        EditText txtBox2 = dialog.findViewById(R.id.edit_txtBox2);

        if (typeOfSetting.equals("Email") || typeOfSetting.equals("Lösenord")) {
            showOtherTxtBoxes(dialog);
        }

        ImageView closeButton = dialog.findViewById(R.id.btn_dialog_close);
        Button confirmButton = dialog.findViewById(R.id.confirm_dialog_button);

        dialogTitle.setText("Ändra på " + typeOfSetting.toLowerCase());
        setHintTxt(typeOfSetting, txtBox1, txtBox2);
        closeButton.setOnClickListener(view -> dialog.dismiss());
        confirmButton.setOnClickListener(view -> {
            if (txtBox1.getText().toString().equalsIgnoreCase(txtBox2.getText().toString())) {
                updateDataToDb(dialog, typeOfSetting, txtBox1);
            } else {
                Toast.makeText(getActivity(), "Du behöver ange samma innehåll på båda fälten", Toast.LENGTH_LONG).show();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.color.eerie_black2);
        dialog.show();
    }

    private void showDeleteDialog(){
        Dialog dialog = new Dialog(rootView.getContext(), R.style.DialogStyle);
        dialog.setContentView(R.layout.layout_custom_dialog);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        EditText txtBox1 = dialog.findViewById(R.id.edit_txtBox1);
        EditText txtBox2 = dialog.findViewById(R.id.edit_txtBox2);

        dialogTitle.setText("Radera konto");
        setHintTxt(dialogTitle.getText().toString(), txtBox1, txtBox2);
        ImageView closeButton = dialog.findViewById(R.id.btn_dialog_close);
        Button confirmButton = dialog.findViewById(R.id.confirm_dialog_button);

        confirmButton.setOnClickListener(view -> {
            removeUser(dialog, txtBox1, txtBox2);
        });

        closeButton.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawableResource(R.color.eerie_black2);
        dialog.show();
    }

    private void setHintTxt(String typeOfSetting, EditText txtBox1, EditText txtBox2) {
        switch (typeOfSetting) {
            case "Email":
                txtBox1.setHint("Nytt mail");
                txtBox2.setHint("Bekräfta mail");
                break;

            case "Lösenord":
                txtBox1.setHint("Nytt lösenord");
                txtBox2.setHint("Bekräfta lösenord");
                break;

            case "Telefon":
                txtBox1.setHint("Nytt nummer");
                txtBox2.setHint("Bekräfta numret");
                break;

            case "Radera konto":
                txtBox1.setHint("Ange ditt email");
                txtBox2.setHint("Ange ditt lösenord");
            break;
        }
    }

    private void updateDataToDb(Dialog dialog, String typeOfSetting, EditText txtBox1) {
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()), currentPassword.getText().toString());

        switch (typeOfSetting) {
            case "Email":
                updateEmail(credential, txtBox1.getText().toString());
                dialog.dismiss();
                break;

            case "Lösenord":
                updatePassword(credential, txtBox1.getText().toString());
                dialog.dismiss();
                break;

            case "Telefon":
                FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("phone_nr").setValue(txtBox1.getText().toString());
                dialog.dismiss();
                break;

        }
    }

    private void showOtherTxtBoxes(Dialog dialog) {
        currentPassword = dialog.findViewById(R.id.edit_txtBox3);
        currentPassword.setVisibility(View.VISIBLE);
        currentPassword.setHint("Ange ditt lösenord");
    }

    private void updateEmail(AuthCredential credential, String value){
        Objects.requireNonNull(auth.getCurrentUser()).reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                auth.getCurrentUser().updateEmail(value)
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                Toast.makeText(getActivity(), "Lyckades ändra e-post adressen", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Lyckades inte e-post adressen", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(getActivity(), "Autentiseringen misslyckades", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("email").setValue(value);
    }

    private void updatePassword(AuthCredential credential, String value){
        Objects.requireNonNull(auth.getCurrentUser()).reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                auth.getCurrentUser().updatePassword(value)
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                Toast.makeText(getActivity(), "Lyckades ändra lösenordet", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Lyckades inte ändra lösenordet", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(getActivity(), "Autentiseringen misslyckades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeUser(Dialog dialog, EditText txtBox1, EditText txtBox2) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(txtBox1.getText().toString(), txtBox2.getText().toString());
        DatabaseReference userBooking = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(auth.getUid())).child("bookings");
        Objects.requireNonNull(auth.getCurrentUser()).reauthenticate(credential).addOnCompleteListener(task -> {
            userBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int nrOfBookings = (int)snapshot.getChildrenCount();
                    if(nrOfBookings == 0) {
                        auth.getCurrentUser().delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Fragment fragment = new HomeFragment();
                                getFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Ditt konto har nu tagits bort!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Du har angett fel email eller lösenord! Försök igen", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Du behöver ta bort dina bokningar först!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void openQuestionDialog() {
        Dialog dialog = new Dialog(rootView.getContext(), R.style.DialogStyle);
        dialog.setContentView(R.layout.layout_question_dialog);
        Button confirmButton = dialog.findViewById(R.id.confirm_question_button);
        Button declineButton = dialog.findViewById(R.id.decline_question_button);
        ImageView closeButton = dialog.findViewById(R.id.btn_dialog_close);
        confirmButton.setOnClickListener(view -> {
            showDeleteDialog();
            dialog.dismiss();
        });
        declineButton.setOnClickListener(view -> dialog.dismiss());
        closeButton.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawableResource(R.color.eerie_black2);
        dialog.show();
    }

    private void search(String searchQuery){
        List<Setting> settings = new ArrayList<>();
        for(Setting setting : settingList){
            if(setting.getTypeOfSetting().toLowerCase().contains(searchQuery.toLowerCase())){
                settings.add(setting);
            }
        }
        settingAdapter = new SettingAdapter(settings, ProfileFragment.this);
        settingRecyclerView.setAdapter(settingAdapter);
    }
}
