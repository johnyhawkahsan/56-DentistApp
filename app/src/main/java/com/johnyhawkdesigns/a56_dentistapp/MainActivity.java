package com.johnyhawkdesigns.a56_dentistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.johnyhawkdesigns.a56_dentistapp.account.LoginActivity;
import com.johnyhawkdesigns.a56_dentistapp.models.Profile;
import com.johnyhawkdesigns.a56_dentistapp.ui.dashboard.DashboardFragment;
import com.johnyhawkdesigns.a56_dentistapp.utils.Utilities;


public class MainActivity extends AppCompatActivity
                implements IMainActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    //FireBase
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    // widgets
    private ImageView navHeaderBackground, navHeaderProfilePic;
    private TextView profileName, profileInfo;
    private View mParentLayout;

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;

    NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentLayout = findViewById(R.id.content_main);

        // Setup toolbar for app
        Toolbar toolbar = findViewById(R.id.toolbar);  // Note: Inside styles.xml, we defined AppTheme.NoActionBar and used inside AndroidMANIFEST for MainActivity's theme @style/AppTheme.NoActionBar
        setSupportActionBar(toolbar); // I had error at this line after using splashscreen, so I used Theme.AppCompat.Light.NoActionBar in styles for AppTheme

        // Setup DrawerLayout and NavigationView
        drawer = findViewById(R.id.drawer_layout); // androidx.drawerlayout.widget.DrawerLayout inside activity_main.xml which also includes (AppBarMain + NavigationView)
        navigationView = findViewById(R.id.nav_view);  // com.google.android.material.navigation.NavigationView inside activity_main.xml
        headerView = navigationView.getHeaderView(0); // this is used to get TextView's and ImageView's inside our NavigationView


        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_profile
                )
                .setDrawerLayout(drawer)
                .build();

        // Setup Navigation Component controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController); // Register NavigationView with NavController so we may use menu id's in navigation_menu



        // -------------Setup Widgets inside NavigationView -> HeaderView ------------------ //
        profileName = headerView.findViewById(R.id.profileName); // instead of using only (findViewByID), we use (headerView.findViewByID)
        profileInfo = headerView.findViewById(R.id.profileInfo);
        navHeaderBackground = headerView.findViewById(R.id.img_header_bg);
        navHeaderProfilePic = headerView.findViewById(R.id.profilePic);

        // redirect to Profile fragment once someone click on profile icon
        navHeaderProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_profile); //navigate to profile fragment using "NAVCONTROLLER"
                drawer.closeDrawer(GravityCompat.START); // close drawer programmatically
            }
        });

        profileName.setText("Muhammad Ahsan");
        profileInfo.setText("johnyHawkAhsan@gmail.com");



        // loading header background image
        Glide.with(this).load(R.drawable.nav_menu_header)
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navHeaderBackground);

        // Loading profile image
        Glide.with(this).load(R.mipmap.dentist_icon_adaptive_round)
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navHeaderProfilePic);


        setupFirebaseAuth();

    }


    // ======================Setup FireBase and check if user is already logged in========================//
    private void setupFirebaseAuth() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // if user is not NULL means user is signed in
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in, Uid is: " + user.getUid());
                    Utilities.makeSnackBarMessage(mParentLayout,"Authenticated with: " + user.getUid());

                } else { // if user == null
                    // User is signed out / hasn't signed in yet
                    Log.d(TAG, "onAuthStateChanged: signed_out / hasn't signed in yet ");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }


    @Override
    public void createNewProfile(final Profile newProfile) {
        Log.d(TAG, "createNewProfile: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // NOTE NOTE NOTE: I am adding a custom doc ID using .set method, instead of add method which automatically generates a key.
        // .set also generates a key if we use .document() only
        //You can think of DocumentReference as an object and CollectionReference as a list of objects.
        final DocumentReference newProfileRef = db.collection(Utilities.profiles) //Create database named "profiles"
                                                                    .document(newProfile.getUser_id()); //Tell FireStore you're inserting a new document with custom document ID


        // Now upload object to FireStore
        newProfileRef.set(newProfile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Created new profile = " + task.getResult() );
                            Utilities.makeSnackBarMessage(mParentLayout, "Created new Profile");
                            Log.d(TAG, "onComplete: new doc ID is = " + newProfileRef.getId() + ", UserID is = " + newProfile.getUser_id());

                            // Navigate to Dashboard
                            navController.navigate(R.id.nav_dashboard);


                        } else {
                            Utilities.makeSnackBarMessage(mParentLayout, "Failed, Check log");
                            Log.d(TAG, "Failed: Failed to create profile" );
                        }
                    }
                });
    }

    @Override
    public void updateProfile(Profile updateProfile) {
        Log.d(TAG, "updateProfile: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference profileRef = db.collection("profiles")
                .document(updateProfile.getUser_id()); // because user id is same as this document's id

        // update relevant fields in FireStore Database
        profileRef.update(Utilities.fullname , updateProfile.getFullname(),
                Utilities.description, updateProfile.getDescription(),
                Utilities.mobileNo, updateProfile.getMobileNo(),
                Utilities.email, updateProfile.getEmail(),
                Utilities.address, updateProfile.getAddress())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Utilities.makeSnackBarMessage(mParentLayout, "Updated Profile");
                            Log.d(TAG, "onComplete: SUCCESSFULLY UPDATED");

                            // Navigate to Dashboard
                            navController.navigate(R.id.nav_dashboard);

                        } else {
                            Log.d(TAG, "onComplete: FAILED");
                            // Navigate to Dashboard
                            navController.navigate(R.id.nav_dashboard);
                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {

            Log.d(TAG, "onOptionsItemSelected: logout");
            Utilities.signOut();
            finish(); // Finish this activity

            //Redirect the signed out user to Login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This method is called whenever the user chooses to navigate Up within your application's activity hierarchy from the action bar.
    //If a parent was specified in the manifest for this activity or an activity-alias to it, default Up navigation will be handled automatically.
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
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
