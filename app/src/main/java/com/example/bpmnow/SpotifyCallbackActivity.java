package com.example.bpmnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bpmnow.models.spotify.SpotifyArtist;
import com.example.bpmnow.models.spotify.SpotifyPlaylist;
import com.example.bpmnow.models.spotify.response.SpotifyPlaylistTracksResponse;
import com.example.bpmnow.models.spotify.response.SpotifyPlaylistsResponse;
import com.example.bpmnow.models.spotify.SpotifyTrackItem;
import com.example.bpmnow.db.DjProfilesManager;
import com.example.bpmnow.db.PlaylistsManager;
import com.example.bpmnow.db.TracksManager;
import com.example.bpmnow.models.token.ResponseRefreshToken;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.example.bpmnow.network.retorfit.SpotifyRetrofitClient;
import com.example.bpmnow.network.retorfit.SpotifyRetrofitClientToken;
import com.example.bpmnow.utils.SpotifyTokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // POST to https://accounts.spotify.com/api/token with PKCE parameters
    private void exchangeCodeForToken(String code, String codeVerifier) {
        SpotifyRetrofitClientToken.getInstance().getApiService()
                .exchangeCode(
                        "authorization_code",
                        code,
                        SpotifyAuth.REDIRECT_URI,
                        SpotifyAuth.CLIENT_ID,
                        codeVerifier
                )
                .enqueue(new Callback<ResponseRefreshToken>() {
                    @Override
                    public void onResponse(Call<ResponseRefreshToken> call,
                                           Response<ResponseRefreshToken> tokenResponse) {
                        if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                            ResponseRefreshToken body = tokenResponse.body();
                            String accessToken = body.getAccess_token();
                            String refreshToken = body.getRefresh_token();
                            int expiresIn = body.getExpires_in();

                            // Save tokens securely in SharedPreferences
                            SpotifyTokenManager.getInstance().saveTokens(
                                    SpotifyCallbackActivity.this, accessToken, refreshToken, expiresIn);
                            Log.d(TAG, "Access token received successfully");

                            // Fetch playlists with the new token
                            fetchAndSavePlaylists(accessToken);
                        } else {
                            Log.e(TAG, "Token exchange failed: " + tokenResponse.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRefreshToken> call, Throwable t) {
                        Log.e(TAG, "Error exchanging code for token", t);
                    }
                });
    }

//    Playlists are fetched and saved to the database
    private void fetchAndSavePlaylists(String accessToken) {
        fetchPlaylists(accessToken);
//        based on playlists id's we get, we then can fetch the tracks.
    }
    private void fetchPlaylists(String accessToken) {
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

//                              Saving the playlists to the database, and get the playlists id's for track fetching
                                List<String> playlistIds = savePlaylistsToDB(playlists);
                                savePlaylistsIdsToDB(playlistIds);
                                totalPlaylists = playlistIds.size();
                                Log.d("totalPlaylists", "Total playlists: " + totalPlaylists);

//                              Fetching tracks for each playlist
                                for (String playlistId : playlistIds) {
                                    fetchTracks(playlistId, accessToken);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SpotifyPlaylistsResponse> call, Throwable t) {
                        Log.e(TAG, "Error loading playlists", t);
                    }
                });
    }
    private void savePlaylistsIdsToDB(List<String> playlistIds) {
        DjProfilesManager.getInstance().savePlaylistIds(
                        FirebaseAuthConnection.getInstance().getUserId(), playlistIds)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Playlists IDs saved to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving playlists IDs to Firestore", e));
    }
    private List<String> savePlaylistsToDB(List<SpotifyPlaylist> playlists) {
        List<String> playlistIds = new ArrayList<>();
        String currentUserId = FirebaseAuthConnection.getInstance().getUserId();
        List<Map<String, Object>> playlistMaps = new ArrayList<>();

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

            playlistIds.add(playlist.getId());
            playlistMaps.add(playlistMap);
        }

        PlaylistsManager.getInstance().batchSavePlaylists(playlistMaps)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "All playlists saved"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving playlists", e));

        return playlistIds;
    }

//    Tracks are fetched and saved to the database
    private void fetchTracks(String playlistId, String accessToken) {
        SpotifyRetrofitClient.getInstance().getApiService()
                .getPlaylistTracks(playlistId, "Bearer " + accessToken)
                .enqueue(new Callback<SpotifyPlaylistTracksResponse>() {
                    @Override
                    public void onResponse(Call<SpotifyPlaylistTracksResponse> call,
                                           Response<SpotifyPlaylistTracksResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getItems() != null) {
                                List<SpotifyTrackItem> tracks = response.body().getItems();
//                                Save tracks to the database
                                saveTracksToDB(tracks, playlistId);
                            }
                        }
                        // Always increment, whether tracks were saved or not
                        if (count.incrementAndGet() >= totalPlaylists) {
                            // Navigate back to MainActivity
                            Intent intent = new Intent(SpotifyCallbackActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<SpotifyPlaylistTracksResponse> call, Throwable t) {
                        Log.e(TAG, "Error loading tracks", t);
                        if (count.incrementAndGet() >= totalPlaylists) {
                            // Navigate back to MainActivity
                            Intent intent = new Intent(SpotifyCallbackActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
    private void saveTracksToDB(List<SpotifyTrackItem> tracks, String playlistId) {
        String currentUserId = FirebaseAuthConnection.getInstance().getUserId();
        List<Map<String, Object>> trackMaps = new ArrayList<>();

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

            trackMaps.add(tracktMap);
        }

        TracksManager.getInstance().batchSaveTracks(trackMaps)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "All tracks saved"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving tracks", e));
    }
}
