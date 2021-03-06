package com.johnyhawkdesigns.a56_dentistapp;


import com.johnyhawkdesigns.a56_dentistapp.models.Profile;

/**
 Because of this IMainActivity Interface, we are able to perform all the task in MainActivity.
 */
public interface IMainActivity {

    void createNewProfile(Profile newProfile);
    void updateProfile(Profile updateProfile);
}
