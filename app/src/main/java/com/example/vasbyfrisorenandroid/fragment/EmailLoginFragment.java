package com.example.vasbyfrisorenandroid.fragment;

import android.content.Intent;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmailLoginFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private Button emailRegButton, loginButton;
    private TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth auth;


    private CircleImageView googleIcon, facebookIcon;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.email_login, container, false);
        emailRegButton = rootView.findViewById(R.id.email_reg_button);
        loginButton = rootView.findViewById(R.id.email_login_button);

        emailEditText = rootView.findViewById(R.id.email_edittext);
        passwordEditText = rootView.findViewById(R.id.email_password_edittext);

        googleIcon = rootView.findViewById(R.id.google_icon);
        facebookIcon = rootView.findViewById(R.id.facebook_icon);

        auth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("314945777569-mn0s0nv336ktqptnd72ur782re9cjkb3.apps.googleusercontent.com")
                .requestEmail()
                .build();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        emailRegButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        googleIcon.setOnClickListener(this);
        facebookIcon.setOnClickListener(this);
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
                loginUserViaEmail();
            break;

            case R.id.google_icon:

                createGoogleLoginRequest();
            break;

            case R.id.facebook_icon:
                loginUserViaFacebook();
            break;
        }
    }


    private void loginUserViaEmail(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(rootView.getContext(), "Inloggningen lyckades", Toast.LENGTH_LONG).show();
                            getFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                    .replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });
    }

    private void createGoogleLoginRequest(){
        mGoogleSignInClient = GoogleSignIn.getClient(rootView.getContext(), gso);
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);

                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(rootView.getContext(), "you are on!", Toast.LENGTH_LONG).show();

                    }
                });

                Toast.makeText(rootView.getContext(), "you are ok", Toast.LENGTH_LONG).show();

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void loginUserViaFacebook(){

    }
}
