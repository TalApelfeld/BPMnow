package com.example.bpmnow.utils;

import java.util.Arrays;
import java.util.List;

public final class Constants {
    private Constants() {}

    // Firestore collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DJ_PROFILES = "djProfiles";
    public static final String COLLECTION_LIVE_SESSIONS = "liveSessions";
    public static final String COLLECTION_SONG_REQUESTS = "songRequests";
    public static final String COLLECTION_REACTIONS = "reactions";
    public static final String COLLECTION_EVENTS = "events";
    public static final String COLLECTION_FOLLOWS = "follows";

    // Roles
    public static final String ROLE_CLUBBER = "clubber";
    public static final String ROLE_DJ = "dj";

    // Song request statuses
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_PLAYED = "played";

    // Reaction types
    public static final String REACTION_FIRE = "fire";
    public static final String REACTION_HEART = "heart";
    public static final String REACTION_CLAP = "clap";
    public static final String REACTION_MIND_BLOWN = "mind_blown";
    public static final String REACTION_DANCE = "dance";
    public static final String REACTION_THUMBS_UP = "thumbs_up";

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
}
