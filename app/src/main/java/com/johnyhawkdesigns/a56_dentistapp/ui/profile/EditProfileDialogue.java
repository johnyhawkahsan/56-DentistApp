package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.johnyhawkdesigns.a56_dentistapp.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditProfileDialogue extends DialogFragment implements View.OnClickListener{

    private static final String TAG = EditProfileDialogue.class.getSimpleName();

    //widgets
    private TextInputLayout fullName;
    private TextView saveProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_edit_profile, container, false);

        fullName = view.findViewById(R.id.tilFullName); // text input layout full name
        saveProfile =  view.findViewById(R.id.saveProfile);
        saveProfile.setOnClickListener(this);

        getDialog().setTitle("Edit Profile");

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            // if saveProfile TextView is clicked inside dialogue_edit_profile
            case R.id.saveProfile:{


                break;
            }
        }
    }
}
