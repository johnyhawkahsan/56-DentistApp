package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.johnyhawkdesigns.a56_dentistapp.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    Button btnEditProfile;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreateView: ProfileFragment");

        // Calling this again so we may navigation to edit profile dialog
        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        /*
        // Another method to navigate to destination using NavigationOnClickListener
        btnEditProfile.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_edit_profile_dialog));
        */

         btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_edit_profile_dialog); // Both methods are working to navigate to edit profile dialogue
                //navController.navigate(R.id.nav_edit_profile_dialog);//navigate to edit profile dialog using "NAVCONTROLLER"
            }
        });



        return view;
    }


}
