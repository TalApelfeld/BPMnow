package com.example.bpmnow;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private NavController navController;

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        // Set initial graph
        navController.setGraph(R.navigation.nav_graph);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and navigate accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is not signed in, navigate to signIn fragment
        if (currentUser != null) {
            navController.navigate(R.id.roleSelection);
        } else {
            navController.navigate(R.id.signIn);
        }
    }

    // Method to switch navigation graphs
    public void switchToGraphClubber() {
        navController.setGraph(R.navigation.nav_graph_clubber);
        findViewById(R.id.bottom_navigation_clubber).setVisibility(View.GONE);
    }

    //    Set the visibility of the bottom navigation bar & connect it to the NavController
//    To be able to navigate between fragments
    public void setClubberBottomNavigationVisible() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_clubber);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setVisibility(View.VISIBLE);
    }

    public void setDJBottomNavigationVisible() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_dj);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setVisibility(View.VISIBLE);
    }

    public void setBottomNavigationInvisible() {
        findViewById(R.id.bottom_navigation_clubber).setVisibility(View.GONE);
    }

    public void switchToGraphDJ() {
        navController.setGraph(R.navigation.nav_graph_dj);
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


}