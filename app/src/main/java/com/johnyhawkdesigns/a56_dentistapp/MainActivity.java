package com.johnyhawkdesigns.a56_dentistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.johnyhawkdesigns.a56_dentistapp.account.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();



    private AppBarConfiguration mAppBarConfiguration;

    private ImageView navHeaderBackground, navHeaderProfilePic;
    private TextView profileName, profileInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        

        // Setup toolbar for app
        Toolbar toolbar = findViewById(R.id.toolbar);  // Note: Inside styles.xml, we defined AppTheme.NoActionBar and used inside AndroidMANIFEST for MainActivity's theme @style/AppTheme.NoActionBar
        setSupportActionBar(toolbar); // I had error at this line after using splashscreen, so I used Theme.AppCompat.Light.NoActionBar in styles for AppTheme

        // Setup DrawerLayout and NavigationView
        DrawerLayout drawer = findViewById(R.id.drawer_layout); // androidx.drawerlayout.widget.DrawerLayout inside activity_main.xml wich also includes (AppBarMain + NavigationView)
        NavigationView navigationView = findViewById(R.id.nav_view);  // com.google.android.material.navigation.NavigationView inside activity_main.xml
        View headerView = navigationView.getHeaderView(0); // this is used to get TextView's and ImageView's inside our NavigationView


        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_profile
                )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // -------------Setup Widgets inside NavigationView -> HeaderView ------------------ //
        profileName = headerView.findViewById(R.id.profileName); // instead of using only (findViewByID), we use (headerView.findViewByID)
        profileInfo = headerView.findViewById(R.id.profileInfo);
        navHeaderBackground = headerView.findViewById(R.id.img_header_bg);
        navHeaderProfilePic = headerView.findViewById(R.id.profilePic);


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
            FirebaseAuth.getInstance().signOut();
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



}
