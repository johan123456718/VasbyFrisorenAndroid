package com.example.vasbyfrisorenandroid.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;

public class NotificationFragment extends Fragment {


    private View rootView;
    private TextView test;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notification, container, false);

        test = rootView.findViewById(R.id.test);

        Bundle b = getArguments();

        if(b != null)
            test.setText(String.valueOf(b.getInt("lol")));

        return rootView;
    }
}
