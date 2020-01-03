package com.johnyhawkdesigns.a56_dentistapp.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.johnyhawkdesigns.a56_dentistapp.R;
import com.johnyhawkdesigns.a56_dentistapp.models.Profile;
import com.johnyhawkdesigns.a56_dentistapp.utils.Utilities;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private Profile currentUserProfile =new Profile(); // This is an array because our result is returned in array form
    private Boolean currentProfileExists = false;


    // Widgets
    ImageView ivProfile;
    TextView tvName, tvDescription, tvMobile2, tvEmail2, tvAddress2;
    Button btnEditProfile;
    RelativeLayout RelLayoutProfileFragment;
    private ProgressBar mProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreateView: ProfileFragment");

        // Calling this again so we may navigation to edit profile dialog
        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment); // right now I'm not using this

        // Initialize widgets
        RelLayoutProfileFragment = view.findViewById(R.id.RelLayoutProfileFragment);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvMobile2 = view.findViewById(R.id.tvMobile2);
        tvEmail2 = view.findViewById(R.id.tvEmail2);
        tvAddress2 = view.findViewById(R.id.tvAddress2);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        mProgressBar = view.findViewById(R.id.progressBarProfileFragment);

        // Initially, we only want ProgressBar to be visible because or Views are not yet been populated yet from FireStore query
        RelLayoutProfileFragment.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /*
        // You can think of DocumentReference as an object and CollectionReference as a list of objects.
        // Create a reference to the profiles collection
        CollectionReference profilesCollectionReference = db.collection(Utilities.profiles);

        // Searching functionality is a lot better in FireStore, it indexes all the data = First need to "Index" data in Console
        // Create a query against the collection
        Query profileQuery = profilesCollectionReference
                .whereEqualTo("user_id", currentUserID);

        profileQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: task.isSuccessful() = " + task.isSuccessful());

                    //Loop through all the received data and add to our list of objects
                    for (DocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                        currentUserProfile[0] = queryDocumentSnapshot.toObject(Profile.class); // convert queryDocumentSnapshot to an object
                        Log.d(TAG, "onComplete: userProfile[0] = " + currentUserProfile[0].getFullname());

                        // Get id of current document.
                        queryDocID[0] = queryDocumentSnapshot.getId();
                        Log.d(TAG, "onComplete: queryDocID = " + queryDocID[0]); // WORKING

                        // Once data is received, we want Progress bar to disappear and layout to appear.
                        hideProgressBar()
                        showRelativeLayout();

                    }

                } else {
                    Utilities.makeSnackBarMessage(view, "Query Failed. Check Logs.");
                    Log.d(TAG, "failed: task not successful = " + task.isSuccessful());
                }
            }
        });
         */

        DocumentReference documentReference = db.collection(Utilities.profiles).document(currentUserID);

        // Search for current document and check if it even exists
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");

                        currentProfileExists = true;

                        // Cast this document into object
                        currentUserProfile = document.toObject(Profile.class);

                        String currentDocumentID = document.getId();
                        Log.d(TAG, "onComplete: currentDocumentID = " + currentDocumentID);


                        String name = currentUserProfile.getFullname();
                        String description = currentUserProfile.getDescription();
                        String mobile = currentUserProfile.getMobileNo();
                        String email = currentUserProfile.getEmail();
                        String address = currentUserProfile.getAddress();

                        tvName.setText(name);
                        tvDescription.setText(description);
                        tvMobile2.setText(mobile);
                        tvEmail2.setText(email);
                        tvAddress2.setText(address);

                        // Once data is received, we want Progress bar to disappear and layout to appear.
                        hideProgressBar();
                        showRelativeLayout();

                    } else {
                        Log.d(TAG, "Document does not exist!");
                        btnEditProfile.setText("Add NEW Profile");
                        currentProfileExists = false;

                        // we want Progress bar to disappear and layout to appear.
                        hideProgressBar();
                        showRelativeLayout();

                    }
                } else { // If task NOT Successful
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });



        // Another method to navigate to destination using NavigationOnClickListener
        // btnEditProfile.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_edit_profile_dialog));
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Navigation.findNavController(view).navigate(R.id.nav_edit_profile_dialog); // Both methods are working to navigate to edit profile dialogue
                //navController.navigate(R.id.nav_edit_profile_dialog);//navigate to edit profile dialog using "NAVCONTROLLER"


                // We are editing profile
                if (currentProfileExists){
                    EditProfileDialogue dialogue = EditProfileDialogue.newInstance(currentUserProfile); // In this instance, we are passing profile object
                    dialogue.show(getFragmentManager(), "Profile");
                }
                // IF we are adding a new profile
                else {
                    EditProfileDialogue dialogue = EditProfileDialogue.newInstance(); // In this instance, we are not passing any object
                    dialogue.show(getFragmentManager(), "Profile");
                }



            }
        });


        return view;
    }


    public void hideProgressBar(){
        // Once data is received, we want Progress bar to disappear and layout to appear.
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void showRelativeLayout(){
        if (RelLayoutProfileFragment.getVisibility() == View.INVISIBLE){
            RelLayoutProfileFragment.setVisibility(View.VISIBLE);
        }
    }


}
