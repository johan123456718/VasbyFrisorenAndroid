package com.example.vasbyfrisorenandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;

public class EmailLoginFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private Button emailRegButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.email_login, container, false);
        emailRegButton = rootView.findViewById(R.id.email_reg_button);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        emailRegButton.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch(v.getId()){

            case R.id.email_reg_button:

                fragment = new EmailRegisterFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            break;
        }
    }
}
