package com.example.bpmnow;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SpotifyAuth {
    public static final String CLIENT_ID = "2d97d1c2dfa94a24819ccab72eb0dfaa";
    public static final String REDIRECT_URI = "bpmnow://callback";
    public static final String SCOPES = "playlist-read-private playlist-read-collaborative user-library-read";

    // Step 1: Generate code verifier (43-128 chars, high-entropy random string)
    // As per Spotify docs: letters, digits, underscores, periods, hyphens, or tildes
    public static String generateCodeVerifier() {
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < 64; i++) {
            sb.append(possible.charAt(random.nextInt(possible.length())));
        }
        return sb.toString();
    }

    // Step 2: Generate code challenge — SHA256 hash of verifier, then base64url encode
    // As per Spotify docs: code_challenge_method = S256
    public static String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes("UTF-8"));
        // base64url encode — remove =, replace + with -, replace / with _
        return Base64.encodeToString(hash, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    // Step 3: Build the Spotify /authorize URL with all required params
    public static String buildAuthUrl(String codeChallenge) {
        return "https://accounts.spotify.com/authorize"
                + "?client_id=" + CLIENT_ID
                + "&response_type=code"
                + "&redirect_uri=" + android.net.Uri.encode(REDIRECT_URI)
                + "&scope=" + android.net.Uri.encode(SCOPES)
                + "&code_challenge_method=S256"
                + "&code_challenge=" + codeChallenge;
    }
}
