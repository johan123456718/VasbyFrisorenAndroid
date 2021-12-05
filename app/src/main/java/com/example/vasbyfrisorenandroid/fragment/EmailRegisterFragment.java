package com.example.vasbyfrisorenandroid.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vasbyfrisorenandroid.R;
import com.example.vasbyfrisorenandroid.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class EmailRegisterFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private FirebaseAuth auth;
    private Button regButton;
    private TextInputEditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.email_register, container, false);

        regButton = rootView.findViewById(R.id.email_register_button);
        firstNameEditText = rootView.findViewById(R.id.firstname_edittext);
        lastNameEditText = rootView.findViewById(R.id.secondname_edittext);
        emailEditText = rootView.findViewById(R.id.email_edittext);
        passwordEditText = rootView.findViewById(R.id.email_password_edittext);
        confPasswordEditText = rootView.findViewById(R.id.email_confPass_edittext);

        auth = FirebaseAuth.getInstance();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        regButton.setOnClickListener(this);


        /*FirebaseUser user = auth.getCurrentUser(); //för att se om en användare är redan inloggad
        if(user == null){

        }*/

         /*FirebaseDatabase.getInstance().getReference("Users")
                                .child(auth.getCurrentUser().getUid())
                                .child("phoneNr")
                                .setValue("1234")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(rootView.getContext(), "Ditt telefon skapades!", Toast.LENGTH_LONG).show(); }
                                }); //Till senare*/


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.email_register_button:

                createUser();

            break;

        }
    }

    private void createUser(){
        String firstname = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confPassword = confPasswordEditText.getText().toString().trim();

        if(firstname.isEmpty()){
            firstNameEditText.setError("Förnamn får inte vara tom");
            firstNameEditText.requestFocus();
        }else if(lastName.isEmpty()){
            lastNameEditText.setError("Efternamn får inte vara tom");
            lastNameEditText.requestFocus();
        }else if(email.isEmpty() || !isValidEmail(email)){
            emailEditText.setError("Email är inte giltigt!");
            emailEditText.requestFocus();
        }else if(password.isEmpty()){
            passwordEditText.setError("Lösenord får inte vara tom");
            passwordEditText.requestFocus();
        }else{

            if(password.equals(confPassword) && (!password.isEmpty() && !confPassword.isEmpty())){

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    User user = new User.UserBuilder(firstname, lastName)
                                            .email(email)
                                            .build();

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(auth.getCurrentUser().getUid())
                                            .setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(rootView.getContext(), "Ditt konto skapades!", Toast.LENGTH_LONG).show();
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                                            .replace(R.id.fragment_container, new EmailLoginFragment())
                                                            .addToBackStack(null).commit();
                                                }
                                            });

                                }else{
                                    Toast.makeText(rootView.getContext(), "Detta konto misslyckades att skapas", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }else{

                Toast.makeText(rootView.getContext(), "Lösenordet är inte samma", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
