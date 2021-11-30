package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;

public class PhoneVerificationFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button phoneContinueBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.telephone_verification, container, false);
        phoneContinueBtn = rootView.findViewById(R.id.phone_continue_btn);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        phoneContinueBtn.setOnClickListener(this);
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

        switch (v.getId()){



        }
    }
}
