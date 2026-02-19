package com.example.bpmnow;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.bpmnow.db.UsersManager;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.example.bpmnow.utils.ClubsSeeder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //    private FirebaseAuth mAuth;
    private NavController navController;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT)
        );
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            Bottom padding is 0 because it messes up the bottom navigation bar
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();


        // Set initial graph
//        navController.setGraph(R.navigation.nav_graph);

        // Seed clubs collection if needed
        ClubsSeeder.seedIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();
        setInitialNavigation();
    }

    // Method to switch navigation graphs
    public void switchToGraphClubber() {
        navController.setGraph(R.navigation.nav_graph_clubber);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_clubber);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Force highlight sync — NavigationUI sometimes fails on popBackStack to start destination
        bottomNav.setOnItemSelectedListener(item -> {
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (!handled) {
                navController.popBackStack(item.getItemId(), false);
            }
            item.setChecked(true);
            return true;
        });
    }

    public void switchToGraphDJ() {
        navController.setGraph(R.navigation.nav_graph_dj);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_dj);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Force highlight sync — NavigationUI sometimes fails on popBackStack to start destination
        bottomNav.setOnItemSelectedListener(item -> {
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (!handled) {
                navController.popBackStack(item.getItemId(), false);
            }
            item.setChecked(true);
            return true;
        });
    }

    //    Because we want to set the home fragment of dj to be the "dashboard",
//    Upon entering the app after you closed it ('create' methods runs again), we need this function
//    in the "roleSelection" fragment to specifically move to the role "formDj" after roleSelection
//    Because if we just set the graph after the role selection, then it will automatically will go to 'dashboard'
    public void switchToFormDJ() {
        navController.setGraph(R.navigation.nav_graph_dj);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_dj);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemSelectedListener(item -> {
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (!handled) {
                navController.popBackStack(item.getItemId(), false);
            }
            item.setChecked(true);
            return true;
        });
        navController.navigate(R.id.formDJ);
    }

    public void switchToFormClubber() {
        navController.setGraph(R.navigation.nav_graph_clubber);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_clubber);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemSelectedListener(item -> {
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (!handled) {
                navController.popBackStack(item.getItemId(), false);
            }
            item.setChecked(true);
            return true;
        });
        navController.navigate(R.id.formClubber);
    }


    //    Set the visibility of the bottom navigation bar & connect it to the NavController
//    To be able to navigate between fragments
    public void setClubberBottomNavigationVisible() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_clubber);
        bottomNav.setVisibility(View.VISIBLE);
    }

    public void setDJBottomNavigationVisible() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_dj);
        bottomNav.setVisibility(View.VISIBLE);
    }

    public void setBottomDJNavigationInvisible() {
        findViewById(R.id.bottom_navigation_dj).setVisibility(View.GONE);
    }
    public void setBottomClubberNavigationInvisible() {
        findViewById(R.id.bottom_navigation_clubber).setVisibility(View.GONE);
    }

    public void startSpotifyLogin() {
        try {
            // Generate and store the code verifier
            String codeVerifier = SpotifyAuth.generateCodeVerifier();
            // Save it to SharedPreferences so SpotifyCallbackActivity can use it
            getSharedPreferences("spotify_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("code_verifier", codeVerifier)
                    .apply();

            // Build the auth URL
            String codeChallenge = SpotifyAuth.generateCodeChallenge(codeVerifier);
            String authUrl = SpotifyAuth.buildAuthUrl(codeChallenge);

            // Open in Chrome Custom Tabs (recommended over WebView for OAuth)
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            customTabsIntent.launchUrl(this, Uri.parse(authUrl));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitialNavigation() {
        // Check if user is signed in (non-null) and navigate accordingly
        if (FirebaseAuthConnection.getInstance().getAuth().getCurrentUser() != null) {
            Log.d("MainActivity", "There is a user");
            setNavigationOnUserRole();
        } else {
            // If user is not signed in, navigate to signIn fragment
            navController.setGraph(R.navigation.nav_graph);
            navController.navigate(R.id.signIn);
        }
    }

    private void setNavigationOnUserRole() {
        UsersManager.getInstance().getUserDocument(FirebaseAuthConnection.getInstance().getUserId())
                .addOnSuccessListener(document -> {
                    Log.d("MainActivity", FirebaseAuthConnection.getInstance().getUserId());
                    String role = document.getString("role");

                    // use role here
                    userRole = role;

                    if (userRole.equals("dj")) {
                        switchToGraphDJ();
                    } else {
                        switchToGraphClubber();
                    }
                })
                .addOnFailureListener(e -> {
                    // handle error
                    Toast.makeText(this, "Error getting user role", Toast.LENGTH_SHORT).show();
                });
    }
}