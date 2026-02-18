package com.example.bpmnow.ui.clubber;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.adapters.ClubDjAdapter;
import com.example.bpmnow.adapters.ClubAdapter;
import com.example.bpmnow.models.Club;
import com.example.bpmnow.models.Dj;
import com.example.bpmnow.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class searchClubber extends Fragment {

    private static final String TAG = "searchClubber";
    private RecyclerView rvSearchResults;
    private TextInputLayout searchInputLayout;
    private TextInputEditText etSearch;
    private View genreScrollView;
    private ChipGroup genreChipGroup;
    private TextView tvNoResults;
    private String currentMode = "club";

    // Adapters for different modes
    private ClubAdapter clubAdapter;
    private ClubDjAdapter djAdapter;
    private List<Club> clubResults = new ArrayList<>();
    private List<Dj> djResults = new ArrayList<>();

    public searchClubber() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_clubber, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        searchInputLayout = view.findViewById(R.id.searchInputLayout);
        etSearch = view.findViewById(R.id.etSearch);
        genreScrollView = view.findViewById(R.id.genreScrollView);
        genreChipGroup = view.findViewById(R.id.genreChipGroup);
        tvNoResults = view.findViewById(R.id.tvNoResults);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup filter tabs
        ChipGroup filterChipGroup = view.findViewById(R.id.filterChipGroup);
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipByClub) switchToClubMode();
            else if (id == R.id.chipByDj) switchToDjMode();
            else if (id == R.id.chipByGenre) switchToGenreMode();
        });

        // Setup search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentMode.equals("dj")) searchDjs(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Default: club mode
        switchToClubMode();
    }

    private void switchToClubMode() {
        currentMode = "club";
        searchInputLayout.setVisibility(View.GONE);
        genreScrollView.setVisibility(View.GONE);
        loadClubs();
    }

    private void switchToDjMode() {
        currentMode = "dj";
        searchInputLayout.setVisibility(View.VISIBLE);
        genreScrollView.setVisibility(View.GONE);
        djResults.clear();
        setupDjAdapter();
    }

    private void switchToGenreMode() {
        currentMode = "genre";
        searchInputLayout.setVisibility(View.GONE);
        genreScrollView.setVisibility(View.VISIBLE);
        setupGenreChips();
    }

    private void loadClubs() {
        clubResults.clear();
        for (String[] clubData : Constants.CLUBS) {
            clubResults.add(new Club(clubData[0], Arrays.asList(clubData[1].split(",")), "", ""));
        }
        clubAdapter = new ClubAdapter(clubResults, club -> {
            Bundle bundle = new Bundle();
            bundle.putString("clubName", club.getName());
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_clubDetail, bundle);
        });
        rvSearchResults.setAdapter(clubAdapter);
        tvNoResults.setVisibility(View.GONE);
    }

    private void setupDjAdapter() {
        djAdapter = new ClubDjAdapter(djResults, dj -> {
            Bundle bundle = new Bundle();
            bundle.putString("djId", dj.getDjId());
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_djProfile, bundle);
        });
        rvSearchResults.setAdapter(djAdapter);
    }

    private void searchDjs(String query) {
        if (query.isEmpty()) {
            djResults.clear();
            djAdapter.updateData(djResults);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_DJ_PROFILES)
                .orderBy("stageName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .addOnSuccessListener(qs -> {
                    djResults.clear();
                    djResults.addAll(qs.toObjects(Dj.class));
                    djAdapter.updateData(djResults);
                    tvNoResults.setVisibility(djResults.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void setupGenreChips() {
        genreChipGroup.removeAllViews();
        for (String genre : Constants.GENRES) {
            Chip chip = new Chip(requireContext());
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) searchByGenre(genre);
            });
            genreChipGroup.addView(chip);
        }
        djResults.clear();
        setupDjAdapter();
    }

    private void searchByGenre(String genre) {
        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_DJ_PROFILES)
                .whereArrayContains("genres", genre)
                .limit(20)
                .get()
                .addOnSuccessListener(qs -> {
                    djResults.clear();
                    djResults.addAll(qs.toObjects(Dj.class));
                    djAdapter.updateData(djResults);
                    tvNoResults.setVisibility(djResults.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}
