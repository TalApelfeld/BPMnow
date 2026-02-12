package com.example.bpmnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
public class SpotifyCallbackActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyCallback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the redirect URI that opened this activity
        Uri uri = getIntent().getData();

        if (uri != null && uri.getScheme().equals("bpmnow")) {
            String code = uri.getQueryParameter("code");
            String error = uri.getQueryParameter("error");

            if (error != null) {
                // User denied access or error occurred
                Log.e(TAG, "Authorization error: " + error);
                finish();
                return;
            }

            if (code != null) {
                // Retrieve the code verifier we stored before launching the browser
                String codeVerifier = getSharedPreferences("spotify_prefs", MODE_PRIVATE)
                        .getString("code_verifier", null);

                if (codeVerifier != null) {
                    // Exchange the code for an access token on a background thread
                    exchangeCodeForToken(code, codeVerifier);
                }
            }
        }
    }

    // POST to https://accounts.spotify.com/api/token
    // As per Spotify docs: grant_type, code, redirect_uri, client_id, code_verifier
    // Content-Type must be application/x-www-form-urlencoded
    private void exchangeCodeForToken(String code, String codeVerifier) {
        new Thread(() -> {
            try {
                URL url = new URL("https://accounts.spotify.com/api/token");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // Build request body exactly as Spotify docs specify
                String body = "grant_type=authorization_code"
                        + "&code=" + URLEncoder.encode(code, "UTF-8")
                        + "&redirect_uri=" + URLEncoder.encode(SpotifyAuth.REDIRECT_URI, "UTF-8")
                        + "&client_id=" + URLEncoder.encode(SpotifyAuth.CLIENT_ID, "UTF-8")
                        + "&code_verifier=" + URLEncoder.encode(codeVerifier, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                if (responseCode == 200) {
                    JSONObject json = new JSONObject(response.toString());
                    String accessToken = json.getString("access_token");
                    String refreshToken = json.getString("refresh_token");
                    int expiresIn = json.getInt("expires_in"); // 3600 seconds = 1 hour

                    // Save tokens securely in SharedPreferences
                    getSharedPreferences("spotify_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("access_token", accessToken)
                            .putString("refresh_token", refreshToken)
                            .putLong("expires_at", System.currentTimeMillis() + (expiresIn * 1000L))
                            .apply();

                    Log.d(TAG, "Access token received successfully");

                    // Navigate back to MainActivity
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.e(TAG, "Token exchange failed: " + responseCode + " - " + response);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error exchanging code for token", e);
            }
        }).start();
    }
}
