package com.example.bpmnow.ui.clubber;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.bpmnow.adapters.ClubberDjAdapter;
import com.example.bpmnow.adapters.ClubberClubAdapter;
import com.example.bpmnow.models.clubber.Club;
import com.example.bpmnow.models.clubber.DjCardItem;
import com.example.bpmnow.models.dj.Dj;
import com.example.bpmnow.db.DjProfilesManager;
import com.example.bpmnow.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private ClubberClubAdapter clubberClubAdapter;
    private ClubberDjAdapter clubberDjAdapter;
    private List<Club> clubResults = new ArrayList<>();
    private List<DjCardItem> djResults = new ArrayList<>();

    public searchClubber() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clubber_search, container, false);
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
        clubberClubAdapter = new ClubberClubAdapter(clubResults, club -> {
            Bundle bundle = new Bundle();
            bundle.putString("clubName", club.getName());
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_clubDetail, bundle);
        });
        rvSearchResults.setAdapter(clubberClubAdapter);
        tvNoResults.setVisibility(View.GONE);
    }

    private void setupDjAdapter() {
        clubberDjAdapter = new ClubberDjAdapter(djResults, dj -> {
            Bundle bundle = new Bundle();
            bundle.putString("djId", dj.getUid());
            Navigation.findNavController(requireView()).navigate(R.id.action_search_to_djProfile, bundle);
        });
        rvSearchResults.setAdapter(clubberDjAdapter);
    }

    private void searchDjs(String query) {
        if (query.isEmpty()) {
            djResults.clear();
            clubberDjAdapter.updateData(djResults);
            tvNoResults.setVisibility(View.GONE);
            return;
        }
        djResults.clear();
        djResults.addAll(DjProfilesManager.getInstance().searchByName(query));
        clubberDjAdapter.updateData(djResults);
        tvNoResults.setVisibility(djResults.isEmpty() ? View.VISIBLE : View.GONE);
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
        djResults.clear();
        djResults.addAll(DjProfilesManager.getInstance().searchByGenre(genre));
        clubberDjAdapter.updateData(djResults);
        tvNoResults.setVisibility(djResults.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
