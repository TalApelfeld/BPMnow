package com.example.bpmnow.utils;

import java.util.Arrays;
import java.util.List;

public final class Constants {
    private Constants() {}

    // Spotify credentials
    public static final String SPOTIFY_CLIENT_ID = "2d97d1c2dfa94a24819ccab72eb0dfaa";
    public static final String SPOTIFY_CLIENT_SECRET = "b1975f6ea8894da1a00e8410dcdaecd7";

    // Firestore collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DJ_PROFILES = "djProfiles";
    public static final String COLLECTION_DJ_PLAYLISTS = "playlists";
    public static final String COLLECTION_TRACKS = "tracks";
    public static final String COLLECTION_TRACK_FEEDBACK = "trackFeedback";
    public static final String COLLECTION_DJ_LIKES = "djLikes";
    public static final String COLLECTION_CLUBS = "clubs";

    // Roles
    public static final String ROLE_CLUBBER = "clubber";
    public static final String ROLE_DJ = "dj";

    // Song request statuses
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_PLAYED = "played";

    // Intent extras
    public static final String EXTRA_DJ_ID = "extra_dj_id";
    public static final String EXTRA_SESSION_ID = "extra_session_id";
    public static final String EXTRA_USER_ROLE = "extra_user_role";

    // SharedPreferences
    public static final String PREFS_NAME = "dj_crowd_connect_prefs";
    public static final String PREF_USER_ROLE = "user_role";
    public static final String PREF_USER_UID = "user_uid";
    public static final String PREF_USER_NAME = "user_display_name";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";

    // Genres
    public static final List<String> GENRES = Arrays.asList(
            "House", "Techno", "EDM", "Hip-Hop", "R&B", "Pop",
            "Drum & Bass", "Trance", "Afrobeats", "Latin",
            "Reggaeton", "Deep House", "Progressive", "Minimal",
            "Dubstep", "Trap", "Funk", "Disco"
    );

    // Predefined Clubs
    public static final String[][] CLUBS = {
            {"Velvet Underground", "House,Deep House,Techno"},
            {"Neon Nights", "EDM,Progressive,Trance"},
            {"The Bassment", "Drum & Bass,Dubstep,Trap"},
            {"Studio 54", "Disco,Funk,House"},
            {"Warehouse Project", "Techno,Minimal,Deep House"},
            {"Paradise Garage", "House,Disco,R&B"},
            {"Berghain TLV", "Techno,Minimal,Progressive"},
            {"Rhythm Factory", "Hip-Hop,Trap,Afrobeats"},
    };

    // Helper to get club names list
    public static List<String> getClubNames() {
        String[] names = new String[CLUBS.length];
        for (int i = 0; i < CLUBS.length; i++) {
            names[i] = CLUBS[i][0];
        }
        return Arrays.asList(names);
    }

    // Helper to get genres for a club
    public static List<String> getClubGenres(String clubName) {
        for (String[] club : CLUBS) {
            if (club[0].equals(clubName)) {
                return Arrays.asList(club[1].split(","));
            }
        }
        return Arrays.asList();
    }
}
