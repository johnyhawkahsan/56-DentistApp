package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.johnyhawkdesigns.a56_dentistapp.IMainActivity;
import com.johnyhawkdesigns.a56_dentistapp.R;
import com.johnyhawkdesigns.a56_dentistapp.models.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditProfileDialogue extends DialogFragment implements View.OnClickListener{

    private static final String TAG = EditProfileDialogue.class.getSimpleName();

    //widgets
    private TextInputLayout TILfullName, TILdescription, TILmobileNo, TILemail, TILaddress;
    private TextView btnSaveProfile, btnCancelDialogue;
    private ImageView addProfileImage;

    // Reference to our Interface
    private IMainActivity mIMainActivity;

    private Profile mProfile;

    // Constructor for this dialogue using singleton pattern - We are using this pattern because we will later return data using arguments
    public static EditProfileDialogue newInstance(Profile profile){
        EditProfileDialogue dialogue = new EditProfileDialogue();

        Bundle args = new Bundle();
        args.putParcelable("profile", profile);
        dialogue.setArguments(args);

        Log.d(TAG, "newInstance: send profile object in args form. args = " + args);
        return dialogue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

        // We get this profile data from ProfileFragment and "cast" it into our mProfile object.
        mProfile = getArguments().getParcelable("profile");
        Log.d(TAG, "onCreate: received arguments from ProfileFragment in onCreate method.  mProfile.getFullname()= " + mProfile.getFullname());


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_edit_profile, container, false);

        // widgets : text input layouts and buttons
        TILfullName = view.findViewById(R.id.TILfullName);
        TILdescription = view.findViewById(R.id.TILDescription);
        TILmobileNo = view.findViewById(R.id.TILmobileNo);
        TILemail = view.findViewById(R.id.TILemail);
        TILaddress = view.findViewById(R.id.TILAddress);
        btnSaveProfile =  view.findViewById(R.id.saveProfile);
        addProfileImage = view.findViewById(R.id.addProfileImage);
        btnCancelDialogue = view.findViewById(R.id.cancelDialogue);
        btnCancelDialogue.setOnClickListener(this);
        btnSaveProfile.setOnClickListener(this);

        getDialog().setTitle("Edit Profile");

        //Set initial properties of our widgets
        TILfullName.getEditText().setText(mProfile.getFullname());
        TILdescription.getEditText().setText(mProfile.getDescription());
        TILmobileNo.getEditText().setText(mProfile.getMobileNo());
        TILemail.getEditText().setText(mProfile.getEmail());
        TILaddress.getEditText().setText(mProfile.getAddress());

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            // if saveProfile TextView is clicked inside dialogue_edit_profile
            case R.id.saveProfile:{

                String fullname = TILfullName.getEditText().getText().toString();
                String description = TILdescription.getEditText().getText().toString();
                String mobileNo = TILmobileNo.getEditText().getText().toString();
                String email = TILemail.getEditText().getText().toString();
                String address = TILaddress.getEditText().getText().toString();

                if (!TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(mobileNo) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(address)){
                    Log.d(TAG, "onClick: Saving data to Firebase");
                    Toast.makeText(getActivity(), "Saving data to firebase", Toast.LENGTH_SHORT).show();

                    // Send this data to MainActivity using interface IMAinActivity
                    mIMainActivity.createNewProfile(fullname, description, mobileNo, email, address);

                    getDialog().dismiss(); // close dialog once all fields are saved to firebase
                }
                else {
                    Log.d(TAG, "onClick: Fill all fields");
                    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                }


                break;
            }

            case R.id.cancelDialogue:{
                getDialog().dismiss();
                break;
            }

        }
    }

    // We need to initialize our Interface method inside onAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }
}
