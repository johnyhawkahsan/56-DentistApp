package com.johnyhawkdesigns.a56_dentistapp.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.johnyhawkdesigns.a56_dentistapp.MainActivity;
import com.johnyhawkdesigns.a56_dentistapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private ImageView mLogo;
    private TextInputLayout mEmail, mPassword;
    private Button mLogin;
    private TextView mRegister;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); //Uses activity_login layout

        // defining our widgets
        mLogo = findViewById(R.id.logo);
        mEmail =  findViewById(R.id.emailTextInputLayout);
        mPassword = findViewById(R.id.passwordTextInputLayout);
        mLogin = findViewById(R.id.btn_login);
        mRegister =  findViewById(R.id.link_register);
        mProgressBar = findViewById(R.id.progressBarLogin);
        mProgressBar.setVisibility(View.INVISIBLE); // Initially we want progress bar to be invisible


        // ======================Setup FireBase and check if user is already logged in========================//
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // if user is not NULL
                if (user != null){

                    //if email is verified
                    if (user.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: signed in, Uid is: " + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    //if email is NOT verified, user need to verify it first
                    } else {
                        Toast.makeText(LoginActivity.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                } else { // if user == null
                    // User is signed out / hasn't signed in yet
                    Log.d(TAG, "onAuthStateChanged: signed_out / hasn't signed in yet ");
                }
            }
        };


        // loading logo into our imageView using glide
        Glide.with(this).load(R.drawable.dentist_app_icon)
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mLogo);


        // If register tv is clicked, we are redirected to RegisterActivity
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigating to Register Screen");

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // if login button is clicked, we check text entered and then use FirebaseAuth to sign in user
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard();

                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                //if both email and password is filled out
                if (!email.isEmpty() && !password.isEmpty()) {
                    Log.d(TAG, "onClick: attempting to authenticate.");

                    showProgressBar();

                    // Try to sign in user with entered username and password
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideProgressBar();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hideProgressBar();
                                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                // If any or both fields are empty, display error message
                } else if (email.isEmpty()){
                    mEmail.setError("Please enter your email id");
                    mEmail.requestFocus();
                } else if (password.isEmpty()){
                    mPassword.setError("Please enter your password");
                    mPassword.requestFocus();
                } else {
                    mEmail.setError("Please enter your email id and password");
                    mEmail.requestFocus();
                    Toast.makeText(LoginActivity.this, "Please enter username and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    // Method to show progress bar
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    // Method to show hide progress bar
    private void hideProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Method to hide software keyboard
    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    // We instantiate our FireBaseAuth in onStart
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Check if mAuthStateListener is null or not
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}
