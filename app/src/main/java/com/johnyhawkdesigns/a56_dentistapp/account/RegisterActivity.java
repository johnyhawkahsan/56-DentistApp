package com.johnyhawkdesigns.a56_dentistapp.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.johnyhawkdesigns.a56_dentistapp.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private EditText mEmail, mName, mPassword, mConfirmPassword;
    private Button mRegister;
    private ProgressBar mProgressBar;

    //vars
    private Context mContext;
    private String email, name, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // defining our widgets
        mName = findViewById(R.id.input_name);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.input_confirm_password);
        mRegister = findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
        mProgressBar.setVisibility(View.INVISIBLE);

        mContext = RegisterActivity.this;



        // ======================Setup FireBase and check if user is already logged in========================//
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // if user is not NULL
                if (user != null) {
                    // User is authenticated
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else { // if user == null
                    // User is signed out / hasn't signed in yet
                    Log.d(TAG, "onAuthStateChanged: signed_out / hasn't signed in yet ");
                }
            }
        };


        // When Register button is clicked
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                name = mName.getText().toString();
                password = mPassword.getText().toString();

                // check if all fields are filled
                if (checkInputs(email, name, password, mConfirmPassword.getText().toString())){

                    // check if password = confirmedPassword
                    if (doStringsMatch(password, mConfirmPassword.getText().toString())){

                        registerNewEmail(email, password);

                    } else { // If password != confirmedPassword
                        Toast.makeText(mContext, "passwords do not match", Toast.LENGTH_SHORT).show();

                    }

                } else { // if any of the field is empty
                    Toast.makeText(mContext, "All fields must be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    public void registerNewEmail(final String email, String password){

        showProgressBar();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password) // create user with entered email and password
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "registerNewEmail: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()){
                            //send email verification
                            sendVerificationEmail();

                            redirectLoginScreen(); // redirect user to login screen so he may login AFTER verifying his email
                            //add user details to firebase database
                            //addNewUser();
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Someone with that email already exists",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressBar();

                        }
                        hideProgressBar();
                        // ...
                    }
                });
    }


    /**
     * sends an email verification link to the user
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: email sent successfully");
                            }
                            else{
                                Toast.makeText(mContext, "couldn't send varification email", Toast.LENGTH_SHORT).show();
                                hideProgressBar();
                            }
                        }
                    });
        }

    }


    /**
     * Adds data to the node: "users" inside firebase database
     */
    public void addNewUser(){

        //add data to the "users" node
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "addNewUser: Adding new User: \n user_id:" + userid);
        //mUser.setName(name);
        //mUser.setUser_id(userid);

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //insert into users node
        //reference.child(getString(R.string.node_users)).child(userid).setValue(mUser);

        FirebaseAuth.getInstance().signOut();
        redirectLoginScreen();
    }



    /**
     * Return true if confirmPassword is same as password
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean doStringsMatch(String password, String confirmPassword){
        return confirmPassword.equals(password);
    }


    /**
     * Checks all the input fields for null
     * @param email
     * @param username
     * @param password
     * @return
     */
    private boolean checkInputs(String email, String username, String password, String confirmPassword){
        Log.d(TAG, "checkInputs: checking inputs for null values");
        if(email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")){
            Toast.makeText(mContext, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Redirects the user to the login screen
     */
    private void redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

}
