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

    // Predefined Clubs — {name, genres, imageUrl}
    public static final String[][] CLUBS = {
            {"Velvet Underground", "House,Deep House,Techno",
                    "https://images.unsplash.com/photo-1566737236500-c8ac43014a67?w=400"},
            {"Neon Nights", "EDM,Progressive,Trance",
                    "https://images.unsplash.com/photo-1571266028243-3716f02d2d25?w=400"},
            {"The Bassment", "Drum & Bass,Dubstep,Trap",
                    "https://images.unsplash.com/photo-1598387993441-a364f854c3e1?w=400"},
            {"Studio 54", "Disco,Funk,House",
                    "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400"},
            {"Warehouse Project", "Techno,Minimal,Deep House",
                    "https://images.unsplash.com/photo-1574391884720-bbc3740c59d1?w=400"},
            {"Paradise Garage", "House,Disco,R&B",
                    "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400"},
            {"Berghain TLV", "Techno,Minimal,Progressive",
                    "https://images.unsplash.com/photo-1571151424460-2a1405e3a2e4?w=400"},
            {"Rhythm Factory", "Hip-Hop,Trap,Afrobeats",
                    "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400"},
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
