package com.example.vasbyfrisorenandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailLoginFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private Button emailRegButton, loginButton;
    private TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.email_login, container, false);
        emailRegButton = rootView.findViewById(R.id.email_reg_button);
        loginButton = rootView.findViewById(R.id.email_login_button);

        emailEditText = rootView.findViewById(R.id.email_edittext);
        passwordEditText = rootView.findViewById(R.id.email_password_edittext);
        auth = FirebaseAuth.getInstance();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        emailRegButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
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

            case R.id.email_login_button:
                loginUser();
            break;
        }
    }


    private void loginUser(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(rootView.getContext(), "Login success", Toast.LENGTH_LONG).show();
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                    }
                });
    }
}
