package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.johnyhawkdesigns.a56_dentistapp.IMainActivity;
import com.johnyhawkdesigns.a56_dentistapp.R;
import com.johnyhawkdesigns.a56_dentistapp.models.Profile;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private Boolean creatingNewProfile;

    private static final int PICKFILE_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    public Uri selectedImageUri;
    public Bitmap selectedImageBitmap;

    private Profile profile;

    // Constructor for this dialogue using singleton pattern - We are using this pattern because we will later return data using arguments
    public static EditProfileDialogue newInstance(Profile profile){
        EditProfileDialogue dialogue = new EditProfileDialogue();

        // If we are editing a current profile
        Bundle args = new Bundle();
        args.putParcelable("profile", profile);
        dialogue.setArguments(args);

        Log.d(TAG, "newInstance: send profile object in args form. args = " + args);
        return dialogue;
    }

    // Constructor if we are creating new profile
    public static EditProfileDialogue newInstance(){
        EditProfileDialogue dialogue = new EditProfileDialogue();

        Log.d(TAG, "newInstance: NO ARGUMENTS because we are creating new profile." );
        return dialogue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("profile")){
            creatingNewProfile = false;
            // We get this profile data from ProfileFragment and "cast" it into our mProfile object.
            mProfile = arguments.getParcelable("profile");
            Log.d(TAG, "onCreate: received arguments from ProfileFragment in onCreate method.  mProfile.getFullname()= " + mProfile.getFullname());
        }
        // if no arguments, then we are creating a new profile
        else {
            creatingNewProfile = true;
            Log.d(TAG, "onCreate: NO Arguments. creatingNewProfile = true");
        }

        // Initialize Profile object
        profile = new Profile();

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
        addProfileImage.setOnClickListener(this);

        if (creatingNewProfile){
            getDialog().setTitle("Create new profile");
        }
        else {
            getDialog().setTitle("Edit Profile");

            //Set initial properties of our widgets
            TILfullName.getEditText().setText(mProfile.getFullname());
            TILdescription.getEditText().setText(mProfile.getDescription());
            TILmobileNo.getEditText().setText(mProfile.getMobileNo());
            TILemail.getEditText().setText(mProfile.getEmail());
            TILaddress.getEditText().setText(mProfile.getAddress());

        }

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

                // If those textViews are NOT empty
                if (!TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(mobileNo) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(address)){
                    Log.d(TAG, "onClick: Saving data to Firebase");
                    Toast.makeText(getActivity(), "Saving data to firebase", Toast.LENGTH_SHORT).show();

                    profile.setFullname(fullname);
                    profile.setDescription(description);
                    profile.setMobileNo(mobileNo);
                    profile.setEmail(email);
                    profile.setAddress(address);
                    profile.setUser_id(FirebaseAuth.getInstance().getUid());
                    

                    // if creatingNewProfile == true, then launch create method
                    if (creatingNewProfile){
                        // Send this data to MainActivity using interface IMAinActivity
                        mIMainActivity.createNewProfile(profile);
                    }
                    // creatingNewProfile == false, then launch update method
                    else {
                        mIMainActivity.updateProfile(profile);
                    }


                    getDialog().dismiss(); // close dialog once all fields are saved to firebase
                }
                else {// if fields are empty
                    Log.d(TAG, "onClick: Fill all fields");
                    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                }


                break;
            }

            case R.id.cancelDialogue:{
                getDialog().dismiss();
                break;
            }

            case R.id.addProfileImage:{

                Log.d(TAG, "onClick: addProfileImage clicked");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT); //Allow the user to select a particular kind of data and return it. This is different than ACTION_PICK in that here we just say what kind of data is desired, not a URI of existing data from which the user can pick.
                startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICKFILE_REQUEST_CODE);


                break;
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Results when selecting a new image from memory
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            try{
                // translate this data into imageUri
                selectedImageUri = data.getData();
                // now we need to convert this image uri into a bitmap
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
            } catch (IOException e){
                Log.e(TAG, "onActivityResult: " + e.getMessage());
            }

            if (selectedImageUri != null){
                Log.d(TAG, "onActivityResult: image uri: " + selectedImageUri);

                if (checkPermissionREAD_EXTERNAL_STORAGE(getActivity())){
                    // if permission is granted, below lines will get executed.
                    Log.d(TAG, "onActivityResult: PERMISSIONS EXIST");
                    Glide
                            .with(getActivity())
                            .load(selectedImageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(addProfileImage);
                }

                // Set this image uri as a parameter of our profile object
                profile.setProfileImageUri(selectedImageUri);

            }

        }

    }

    // We need to initialize our Interface method inside onAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }






    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT; // Get current sdk version
        Log.d(TAG, "checkPermissionREAD_EXTERNAL_STORAGE: currentAPIVersion = " + currentAPIVersion);

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) { // android M api is 23

            Log.d(TAG, "checkPermissionREAD_EXTERNAL_STORAGE: APP DOES NOT YET HAVE PERMISSIONS");

            // checks if the app does not have permission needed
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // shows an explanation of why permission is needed
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            // if app already has permission to write to external storage
            Log.d(TAG, "checkPermissionREAD_EXTERNAL_STORAGE: APP ALREADY HAS PERMISSIONS");
            return true;
        }
    }

    // Show dialog for permissions
    public void showDialog(final String msg, final Context context, final String permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    // called by the system when the user either grants or denies the permission for saving an image
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // switch chooses appropriate action based on which feature requested permission
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: PackageManager.PERMISSION_GRANTED");
                    // when user first time grants the permissions, these lines get executed
                    Glide
                            .with(getActivity())
                            .load(selectedImageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(addProfileImage);

                    profile.setProfileImageUri(selectedImageUri);

                } else {
                    Toast.makeText(getActivity(), "Get read permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
