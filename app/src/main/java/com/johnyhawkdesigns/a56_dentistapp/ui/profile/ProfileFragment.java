package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.johnyhawkdesigns.a56_dentistapp.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreateView: ProfileFragment" );

        return root;
    }


}
