package com.example.bpmnow.ui.dj;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bpmnow.R;
import com.example.bpmnow.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link formDJ#newInstance} factory method to
 * create an instance of this fragment.
 */
public class formDJ extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> allGenres = Constants.GENRES;

    private Set<String> selectedGenres = new HashSet<>();
    private Uri selectedImageUri;
    private ShapeableImageView profileImageView;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImageView.setImageURI(uri); // works because it's a field
                }
            });
    // correct - permission launcher should be <String> for input but the callback receives Boolean
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted != null && isGranted) {
                    pickImageLauncher.launch("image/*");
                } else {
                    // on Android 14, partial access still allows picking
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        pickImageLauncher.launch("image/*");
                    } else {
                        Toast.makeText(requireContext(),
                                "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public formDJ() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment formDJ.
     */
    // TODO: Rename and change types and number of parameters
    public static formDJ newInstance(String param1, String param2) {
        formDJ fragment = new formDJ();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_d_j, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get views manually since you're not using ViewBinding
        ChipGroup genreChipGroup = view.findViewById(R.id.genreChipGroup);
        TextInputEditText genreSearchInput = view.findViewById(R.id.genreSearchInput);

        setupGenreChips(genreChipGroup, allGenres);
        setupSearch(genreSearchInput, genreChipGroup);

        profileImageView = view.findViewById(R.id.profileImageView);

        // in onViewCreated replace the click listener with this
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

        MaterialButton btnFormContinue = view.findViewById(R.id.btnFormContinueDJ);
        btnFormContinue.setOnClickListener(v -> {
            // Handle continue button click
            Navigation.findNavController(v).navigate(R.id.action_formDJ_to_homeDJ);
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