package com.example.bpmnow.ui.clubber;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bpmnow.R;
import com.example.bpmnow.db.UsersManager;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class formClubber extends Fragment {

    private static final String TAG = "formClubber";
    private List<String> allGenres = Constants.GENRES;
    private Set<String> selectedGenres = new HashSet<>();
    private Uri selectedImageUri;
    private ShapeableImageView profileImageView;
    private TextInputEditText nicknameInput;
    private TextInputEditText ageInput;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImageView.setImageURI(uri);
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted != null && isGranted) {
                    pickImageLauncher.launch("image/*");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        pickImageLauncher.launch("image/*");
                    } else {
                        Toast.makeText(requireContext(),
                                "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public formClubber() {
        // Required empty public constructor
    }

    public static formClubber newInstance(String param1, String param2) {
        formClubber fragment = new formClubber();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_clubber, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nicknameInput = view.findViewById(R.id.nicknameInput);
        TextInputEditText genreSearchInput = view.findViewById(R.id.genreSearchInput);
        ChipGroup genreChipGroup = view.findViewById(R.id.genreChipGroup);
        ageInput = view.findViewById(R.id.ageInput);
        profileImageView = view.findViewById(R.id.profileImageView);
        MaterialButton btnFormContinue = view.findViewById(R.id.btnFormContinueClubber);

        setupSearch(genreSearchInput, genreChipGroup);
        setupGenreChips(genreChipGroup, allGenres);


        profileImageView.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*");
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*");
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*");
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });

        btnFormContinue.setOnClickListener(v -> {
            if (validateForm()) {
                saveToFirestoreAndNavigate(v);
            }
        });
    }

    private boolean validateForm() {
        String nickname = nicknameInput.getText() != null ? nicknameInput.getText().toString().trim() : "";
        String ageStr = ageInput.getText() != null ? ageInput.getText().toString().trim() : "";

        if (nickname.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a nickname", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ageStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your age", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedGenres.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one genre", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveToFirestoreAndNavigate(View v) {
        String uid = FirebaseAuthConnection.getInstance().getUserId();
        String nickname = nicknameInput.getText().toString().trim();
        int age = Integer.parseInt(ageInput.getText().toString().trim());

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("nickname", nickname);
        userData.put("age", age);
        userData.put("genres", new ArrayList<>(selectedGenres));
        userData.put("role", Constants.ROLE_CLUBBER);
        userData.put("profileImageUrl", selectedImageUri != null ? selectedImageUri.toString() : "");
        userData.put("createdAt", Timestamp.now());

        UsersManager.getInstance().saveUserProfile(uid, userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Clubber profile saved to Firestore");
                    // Save role to SharedPreferences for quick access
                    requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
                            .edit()
                            .putString(Constants.PREF_USER_ROLE, Constants.ROLE_CLUBBER)
                            .putString(Constants.PREF_USER_UID, uid)
                            .putString(Constants.PREF_USER_NAME, nickname)
                            .putBoolean(Constants.PREF_IS_LOGGED_IN, true)
                            .apply();
                    Navigation.findNavController(v).navigate(R.id.action_formClubber_to_homeClubber);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving clubber profile", e);
                    Toast.makeText(requireContext(), "Failed to save profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setupGenreChips(ChipGroup chipGroup, List<String> genres) {
        chipGroup.removeAllViews();
        for (String genre : genres) {
            Chip chip = new Chip(requireContext(), null,
                    com.google.android.material.R.attr.chipStyle);
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChecked(selectedGenres.contains(genre));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedGenres.add(genre);
                } else {
                    selectedGenres.remove(genre);
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void setupSearch(TextInputEditText searchInput, ChipGroup chipGroup) {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGenres(s.toString(), chipGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterGenres(String query, ChipGroup chipGroup) {
        List<String> filtered = new ArrayList<>();
        for (String genre : allGenres) {
            if (genre.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(genre);
            }
        }
        setupGenreChips(chipGroup, filtered);
    }

    public Set<String> getSelectedGenres() {
        return selectedGenres;
    }
}
