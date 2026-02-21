package com.example.bpmnow.ui.clubber;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.adapters.ClubberClubAdapter;
import com.example.bpmnow.adapters.ClubberDjAdapter;
import com.example.bpmnow.db.DjProfilesManager;
import com.example.bpmnow.models.clubber.Club;
import com.example.bpmnow.models.clubber.DjCardItem;
import com.example.bpmnow.models.dj.Dj;
import com.example.bpmnow.models.dj.DjTopTrack;
import com.example.bpmnow.utils.Constants;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class homeClubber extends Fragment {

    private static final String TAG = "homeClubber";
    private RecyclerView clubberRecyclerView;
    private RecyclerView DJRecyclerView;
    private ClubberClubAdapter clubsAdapter;
    private ClubberDjAdapter DJsAdapter;
    private List<Club> clubItems = new ArrayList<>();
    private List<DjCardItem> DJItems = new ArrayList<>();

    public homeClubber() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubber_home, container, false);
        ((MainActivity) requireActivity()).setClubberBottomNavigationVisible();
        setupClubsRecyclerView(view);
        setupDJsRecyclerView(view);
        loadClubs();
        loadPopularDJs();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupClubsRecyclerView(View view) {
        clubberRecyclerView = view.findViewById(R.id.clubsRecyclerView);
        clubberRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        clubsAdapter = new ClubberClubAdapter(clubItems, club -> {
            // Navigate to club detail
            Bundle bundle = new Bundle();
            bundle.putString("clubName", club.getName());
            Navigation.findNavController(view).navigate(R.id.action_home_to_clubDetail, bundle);
        });
        clubberRecyclerView.setAdapter(clubsAdapter);
    }

    private void setupDJsRecyclerView(View view) {
        DJRecyclerView = view.findViewById(R.id.DJsRecyclerView);
        DJRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        DJsAdapter = new ClubberDjAdapter(DJItems, dj -> {
            // Navigate to DJ profile
            Bundle bundle = new Bundle();
            bundle.putString("djId", dj.getUid());
            Navigation.findNavController(view).navigate(R.id.action_home_to_djProfile, bundle);
        });
        DJRecyclerView.setAdapter(DJsAdapter);
    }

    private void loadClubs() {
        clubItems.clear();
        // Load predefined clubs from Constants, then for each, check if any DJ is playing
        for (String[] clubData : Constants.CLUBS) {
            String clubName = clubData[0];
            List<String> genres = Arrays.asList(clubData[1].split(","));
            String imageUrl = clubData.length > 2 ? clubData[2] : "";
            Club club = new Club(clubName, genres, "", "", imageUrl);
            clubItems.add(club);
        }
        // Update the adapter with the new data
        clubsAdapter.updateData(clubItems);
    }

    private void loadPopularDJs() {
        DjProfilesManager.getInstance().getPopularDJs(20)
                .addOnSuccessListener(querySnapshot -> {
                    DJItems.clear();
                    DJItems.addAll(querySnapshot.toObjects(DjCardItem.class));
                    DJsAdapter.updateData(DJItems);
//                    Chache DJs inorder to use in search
                    DjProfilesManager.getInstance().setCachedDjs(DJItems);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading DJs", e));
    }
}
