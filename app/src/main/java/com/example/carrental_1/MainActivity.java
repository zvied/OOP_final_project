package com.example.carrental_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carrental_1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore db;
    Button button;
    TextView userName, userEmail;
    FirebaseUser user;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.userName);
        userEmail = headerView.findViewById(R.id.userEmail);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            userEmail.setText(user.getEmail());
            fetchUserDetailsAndSetupMenu();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchUserDetailsAndSetupMenu() {
        String uid = user.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String surname = documentSnapshot.getString("surname");
                            isAdmin = documentSnapshot.getBoolean("isAdmin") != null && Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"));
                            if (name != null && surname != null) {
                                userName.setText(name + " " + surname);
                            }
                            setupMenu();
                        } else {
                            userName.setText("User Name");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userName.setText("User Name");
                    }
                });
    }

    private void setupMenu() {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();
        if (isAdmin) {
            menu.findItem(R.id.nav_add_car).setVisible(true);
            menu.findItem(R.id.nav_add_admin).setVisible(true);
            menu.findItem(R.id.nav_view_cars_admin).setVisible(true);
            menu.findItem(R.id.nav_show_rents_admin).setVisible(true);
            menu.findItem(R.id.nav_show_users_admin).setVisible(true);
        } else {
            menu.findItem(R.id.nav_view_cars).setVisible(true);
            menu.findItem(R.id.nav_rent_car).setVisible(true);
            menu.findItem(R.id.nav_return_car).setVisible(true);
            menu.findItem(R.id.nav_rentals).setVisible(true);
            menu.findItem(R.id.nav_edit_profile).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                if (item.getItemId() == R.id.nav_add_car) {
                    navController.navigate(R.id.nav_add_car);

                } else if (item.getItemId() == R.id.nav_view_cars_admin) {
                    navController.navigate(R.id.nav_view_cars_admin);
                } else {
                    navController.navigate(item.getItemId());
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        NavigationUI.setupWithNavController(navigationView, Navigation.findNavController(this, R.id.nav_host_fragment_content_main));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
