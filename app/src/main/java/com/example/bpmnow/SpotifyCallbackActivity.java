package com.example.bpmnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bpmnow.models.spotify.SpotifyArtist;
import com.example.bpmnow.models.spotify.SpotifyPlaylist;
import com.example.bpmnow.models.spotify.SpotifyPlaylistTracksResponse;
import com.example.bpmnow.models.spotify.SpotifyPlaylistsResponse;
import com.example.bpmnow.models.spotify.SpotifyTrackItem;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.network.retorfit.SpotifyRetrofitClient;
import com.example.bpmnow.ui.dj.profileDJ;
import com.example.bpmnow.utils.Constants;
import com.example.bpmnow.utils.SpotifyTokenManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotifyCallbackActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyCallback";
    private int totalPlaylists;
    private AtomicInteger count = new AtomicInteger(0);

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
                    SpotifyTokenManager.getInstance().saveTokens(this, accessToken, refreshToken, expiresIn);
                    Log.d(TAG, "Access token received successfully");
                    SpotifyRetrofitClient.getInstance().getApiService()
                            .getMyPlaylists("Bearer " + accessToken)
                            .enqueue(new Callback<SpotifyPlaylistsResponse>() {
                                @Override
                                public void onResponse(Call<SpotifyPlaylistsResponse> call,
                                                       Response<SpotifyPlaylistsResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        if (response.body().getItems() != null) {
                                            List<SpotifyPlaylist> playlists = response.body().getItems();
                                            Log.d(TAG, response.body().getItems().toString());

//                                            Saving the playlists to the database, and get the playlists id's for track fetching
                                            List<String> playlistIds = savePlaylistsToDB(playlists);
                                            savePlaylistsIdsToDB(playlistIds);
                                            totalPlaylists = playlistIds.size();
                                            Log.d("totalPlaylists", "Total playlists: " + totalPlaylists);

//                                            Fetching tracks for each playlist
                                            for (String playlistId : playlistIds) {
                                                SpotifyRetrofitClient.getInstance().getApiService()
                                                        .getPlaylistTracks(playlistId, "Bearer " + accessToken)
                                                        .enqueue(new Callback<SpotifyPlaylistTracksResponse>() {
                                                            @Override
                                                            public void onResponse(Call<SpotifyPlaylistTracksResponse> call,
                                                                                   Response<SpotifyPlaylistTracksResponse> response) {
                                                                if (response.isSuccessful() && response.body() != null) {
                                                                    if (response.body().getItems() != null) {
                                                                        List<SpotifyTrackItem> tracks = response.body().getItems();
                                                                        saveTracksToDB(tracks, playlistId);
                                                                    }
                                                                }
                                                                // Always increment, whether tracks were saved or not
                                                                if (count.incrementAndGet() >= totalPlaylists) {
                                                                    // Navigate back to MainActivity
                                                                    runOnUiThread(() -> {
                                                                        Intent intent = new Intent(SpotifyCallbackActivity.this, MainActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<SpotifyPlaylistTracksResponse> call, Throwable t) {
                                                                Log.e(TAG, "Error loading tracks", t);
                                                                if (count.incrementAndGet() >= totalPlaylists) {
                                                                    // Navigate back to MainActivity
                                                                    runOnUiThread(() -> {
                                                                        Intent intent = new Intent(SpotifyCallbackActivity.this, MainActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<SpotifyPlaylistsResponse> call, Throwable t) {
                                    Log.e(TAG, "Error loading playlists", t);
                                }
                            });


                } else {
                    Log.e(TAG, "Token exchange failed: " + responseCode + " - " + response);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error exchanging code for token", e);
            }
        }).start();
    }

    private List<String> savePlaylistsToDB(List<SpotifyPlaylist> playlists) {
        List<String> playlistIds = new ArrayList<>();
        FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();
        String currentUserId = FirebaseAuthConnection.getInstance().getUserId();
        WriteBatch batch = db.batch();

        for (SpotifyPlaylist playlist : playlists) {
            Map<String, Object> playlistMap = new HashMap<>();
            playlistMap.put("id", playlist.getId());
            playlistMap.put("name", playlist.getName());
            int trackTotal = (playlist.getTracks() != null) ? playlist.getTracks().getTotal() : 0;
            playlistMap.put("total", trackTotal);
            playlistMap.put("uri", playlist.getUri());
            String imageUrl = "";
            if (playlist.getImages() != null && !playlist.getImages().isEmpty()) {
                imageUrl = playlist.getImages().get(0).getUrl();
            }
            playlistMap.put("url", imageUrl);
            playlistMap.put("uid", currentUserId);
            playlistMap.put("requests", 0);
            playlistMap.put("reviews", new ArrayList<String>());

//            Add id of playlist to list, to return from function, inorder to then make requests to fetch tracks.
            playlistIds.add(playlist.getId());
            // Auto-generate ID by calling document() with no arguments
            DocumentReference docRef = db.collection("playlists").document();
            batch.set(docRef, playlistMap);
        }
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "All playlists saved");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error saving playlists", e));

        return playlistIds;
    }

    private void saveTracksToDB(List<SpotifyTrackItem> tracks, String playlistId) {
        FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();
        String currentUserId = FirebaseAuthConnection.getInstance().getUserId();
        WriteBatch batch = db.batch();

        for (SpotifyTrackItem track : tracks) {
            Map<String, Object> tracktMap = new HashMap<>();
            tracktMap.put("id", track.getItem().getId());
            tracktMap.put("idPlaylist", playlistId);
            tracktMap.put("name", track.getItem().getTrackName());
            List<String> artistNames = new ArrayList<>();
            if (track.getItem().getArtists() != null) {
                for (SpotifyArtist artist : track.getItem().getArtists()) {
                    artistNames.add(artist.getName());
                }
            }
            tracktMap.put("artistNames", artistNames);
            tracktMap.put("uri", track.getItem().getUri());
            String imageUrl = "";
            if (track.getItem().getAlbum().getImages() != null && !track.getItem().getAlbum().getImages().isEmpty()) {
                imageUrl = track.getItem().getAlbum().getImages().get(0).getUrl();
            }
            tracktMap.put("url", imageUrl);
            tracktMap.put("uid", currentUserId);
            tracktMap.put("requests", 0);
            tracktMap.put("reviews", new ArrayList<String>());

            // Auto-generate ID by calling document() with no arguments
            DocumentReference docRef = db.collection(Constants.COLLECTION_TRACKS).document();
            batch.set(docRef, tracktMap);
        }
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "All tracks saved");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error saving tracks", e));
    }

    private void savePlaylistsIdsToDB(List<String> playlistIds) {
        FirebaseDBConnection.getInstance().getDB().
                collection(Constants.COLLECTION_DJ_PROFILES).
                document(FirebaseAuthConnection.getInstance().getUserId())
                .update("playlistId", playlistIds)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Log.d(TAG, "Playlists IDs saved to Firestore");
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e(TAG, "Error saving playlists IDs to Firestore", e);
                });
    }
}
