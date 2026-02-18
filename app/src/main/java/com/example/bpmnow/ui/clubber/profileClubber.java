package com.example.bpmnow.ui.clubber;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.ui.dj.profileDJ;
import com.example.bpmnow.utils.Constants;
import com.example.bpmnow.utils.SpotifyTokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class profileClubber extends Fragment {

    private static final String TAG = "profileClubber";
    private ShapeableImageView ivProfileImage;
    private TextView tvNickname, tvAge;
    private ChipGroup chipGroupGenres;
    private FirebaseFirestore db;
    private String currentUid;

    public profileClubber() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_clubber, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvNickname = view.findViewById(R.id.tvNickname);
        tvAge = view.findViewById(R.id.tvAge);
        chipGroupGenres = view.findViewById(R.id.chipGroupGenres);

        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireContext().getSharedPreferences(Constants.PREFS_NAME, 0).edit().clear().apply();
            SpotifyTokenManager.clearTokens(requireContext());
            ((MainActivity) requireActivity()).setBottomClubberNavigationInvisible();
            NavController navController = NavHostFragment.findNavController(profileClubber.this);
            navController.setGraph(R.navigation.nav_graph);
            navController.navigate(R.id.signIn);
        });

        loadProfile();
    }

    private void loadProfile() {
        db.collection(Constants.COLLECTION_USERS).document(currentUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvNickname.setText(doc.getString("nickname"));
                        Long age = doc.getLong("age");
                        tvAge.setText(age != null ? age + " years old" : "");

                        chipGroupGenres.removeAllViews();
                        List<String> genres = (List<String>) doc.get("genres");
                        if (genres != null) {
                            for (String genre : genres) {
                                Chip chip = new Chip(requireContext());
                                chip.setText(genre);
                                chip.setClickable(false);
                                chipGroupGenres.addView(chip);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading profile", e));
    }
}
