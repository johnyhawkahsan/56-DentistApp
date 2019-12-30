package com.johnyhawkdesigns.a56_dentistapp.utils;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Utilities {

    // Static fields in FireStore database
    public static String profiles = "profiles";
    public static String user_id = "user_id";
    public static String profile_id = "profile_id";
    public static String fullname = "fullname";
    public static String description = "description";
    public static String mobileNo = "mobileNo";
    public static String email = "email";
    public static String address = "address";


    public static void makeSnackBarMessage(View parentLayout, String message){
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // initiate sign out process
    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
