package com.johnyhawkdesigns.a56_dentistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.johnyhawkdesigns.a56_dentistapp.account.LoginActivity;
import com.johnyhawkdesigns.a56_dentistapp.models.Profile;

public class MainActivity extends AppCompatActivity
                implements IMainActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    //FireBase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private AppBarConfiguration mAppBarConfiguration;

    // widgets
    private ImageView navHeaderBackground, navHeaderProfilePic;
    private TextView profileName, profileInfo;

    private View mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentLayout = findViewById(R.id.content_main);

        // Setup toolbar for app
        Toolbar toolbar = findViewById(R.id.toolbar);  // Note: Inside styles.xml, we defined AppTheme.NoActionBar and used inside AndroidMANIFEST for MainActivity's theme @style/AppTheme.NoActionBar
        setSupportActionBar(toolbar); // I had error at this line after using splashscreen, so I used Theme.AppCompat.Light.NoActionBar in styles for AppTheme

        // Setup DrawerLayout and NavigationView
        final DrawerLayout drawer = findViewById(R.id.drawer_layout); // androidx.drawerlayout.widget.DrawerLayout inside activity_main.xml wich also includes (AppBarMain + NavigationView)
        NavigationView navigationView = findViewById(R.id.nav_view);  // com.google.android.material.navigation.NavigationView inside activity_main.xml
        View headerView = navigationView.getHeaderView(0); // this is used to get TextView's and ImageView's inside our NavigationView


        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_profile
                )
                .setDrawerLayout(drawer)
                .build();

        // Setup Navigation Component controller
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
                    makeSnackBarMessage("Authenticated with: " + user.getUid());

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
    public void createNewProfile(String fullname, String description, String mobileNo, String email, String address) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getUid();

        //You can think of DocumentReference as an object and CollectionReference as a list of objects.
        DocumentReference newProfileRef = db.collection("profiles") //Create database named "profiles"
                                                                    .document(); //Tell FireStore you're inserting a new document

        Profile profile = new Profile();
        profile.setUser_id(userID);
        profile.setFullname(fullname);
        profile.setDescription(description);
        profile.setMobileNo(mobileNo);
        profile.setEmail(email);
        profile.setAddress(address);

        // Now upload object to FireStore
        newProfileRef.set(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Created new profile");
                            makeSnackBarMessage("Created new Profile");
                        } else {
                            makeSnackBarMessage("Failed, Check log");
                            Log.d(TAG, "Failed: Failed to create profile" );
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
            signOut();
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


    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    // initiate sign out process
    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
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
