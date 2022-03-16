package com.example.e_culturetoolbakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culturetoolbakers.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Locale locale;
    private Configuration config;
    private FloatingActionButton fab;
    private DatabaseHelper db_helper;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Locale current = getResources().getConfiguration().locale;
        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        if (preferences.getString("My_Lang", "") == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("My_Lang", String.valueOf(current));
            editor.apply();
        } else {
            locale = new Locale(preferences.getString("My_Lang", ""));
            Locale.setDefault(locale);
            config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }*/

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        ImageView imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(this);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        /* Logout */
        navigationView.getMenu().findItem(R.id.menu_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
        /* QR */
        navigationView.getMenu().findItem(R.id.menu_QRScanner).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(getApplication(), QRScanner.class));
            return true;
        });

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* Set dynamic title */
        TextView textTitle = findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        TextView mUsername = navigationView.getHeaderView(0).findViewById(R.id.usernameHeader);
        TextView mEmail = navigationView.getHeaderView(0).findViewById(R.id.emailHeader);
        fab = findViewById(R.id.add_fab);

        if (mUser != null) {
            db_helper = new DatabaseHelper(this);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /* Show items by user type */
                    //navigationView.getMenu().findItem(R.id.menu_profile).setVisible(true);
                    if (dataSnapshot.child("type").getValue().toString().equals("cur")) {
                        navigationView.getMenu().findItem(R.id.menu_museumRegistration).setVisible(true);
                    }

                    /* Set header info */
                    mUsername.setText(dataSnapshot.child("username").getValue().toString());
                    mEmail.setText(dataSnapshot.child("email").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, ""+error.toException(), Toast.LENGTH_LONG).show();
                }
            });

        } else {

            mUsername.setText(getResources().getString(R.string.header_guest));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_logout), Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}