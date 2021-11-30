package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;

public class SignQuestionFragment extends Fragment implements View.OnClickListener {


    private View rootView;
    private ImageView backButton;
    private Button phonePickButton, emailPickButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sign_question, container, false);
        backButton = rootView.findViewById(R.id.backbutton);
        phonePickButton = rootView.findViewById(R.id.telephone_nr_button);
        emailPickButton = rootView.findViewById(R.id.email_button);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        backButton.setOnClickListener(this);
        phonePickButton.setOnClickListener(this);
        emailPickButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()){

            case R.id.backbutton:
                fragment = new HomeFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            break;

            case R.id.email_button:

                fragment = new EmailLoginFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            break;

            case R.id.telephone_nr_button:

                fragment = new PhoneLoginFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            break;
        }
    }
}
