package com.johnyhawkdesigns.a56_dentistapp.utils;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Utilities {

    public static void makeSnackBarMessage(View parentLayout, String message){
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // initiate sign out process
    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
