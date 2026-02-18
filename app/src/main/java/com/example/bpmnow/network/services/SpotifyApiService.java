package com.example.bpmnow.network.services;

import com.example.bpmnow.models.spotify.SpotifyPlaylistTracksResponse;
import com.example.bpmnow.models.spotify.SpotifyPlaylistsResponse;
import com.example.bpmnow.models.spotify.SpotifyUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyApiService {

    /**
     * Get the current user's Spotify profile.
     * Used to retrieve the spotifyUserId after OAuth.
     */
    @GET("me")
    Call<SpotifyUser> getCurrentUser(
            @Header("Authorization") String authHeader
    );

    /**
     * Get the current user's playlists.
     * Used by DJ to see their own playlists.
     */
    @GET("me/playlists")
    Call<SpotifyPlaylistsResponse> getMyPlaylists(
            @Header("Authorization") String authHeader
//            @Query("limit") int limit,
//            @Query("offset") int offset
    );

    /**
     * Get a public user's playlists by user ID.
     * Used by clubbers to browse a DJ's public playlists without auth.
     */
    @GET("users/{user_id}/playlists")
    Call<SpotifyPlaylistsResponse> getUserPlaylists(
            @Path("user_id") String userId,
            @Header("Authorization") String authHeader,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Get tracks from a specific playlist.
     * Used to display tracks in a playlist detail view.
     */
    @GET("playlists/{playlist_id}/items")
    Call<SpotifyPlaylistTracksResponse> getPlaylistTracks(
            @Path("playlist_id") String playlistId,
            @Header("Authorization") String authHeader
//            @Query("limit") int limit,
//            @Query("offset") int offset
    );
}
