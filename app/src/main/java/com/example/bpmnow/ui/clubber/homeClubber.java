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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.adapters.ClubAdapter;
import com.example.bpmnow.adapters.DjAdapter;
import com.example.bpmnow.models.Club;
import com.example.bpmnow.models.Dj;
import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class homeClubber extends Fragment {

    private static final String TAG = "homeClubber";
    private RecyclerView clubberRecyclerView;
    private RecyclerView DJRecyclerView;
    private ClubAdapter clubsAdapter;
    private DjAdapter DJsAdapter;
    private List<Club> clubItems = new ArrayList<>();
    private List<Dj> DJItems = new ArrayList<>();

    public homeClubber() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_clubber, container, false);
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
        clubsAdapter = new ClubAdapter(clubItems, club -> {
            // Navigate to club detail
            Bundle bundle = new Bundle();
            bundle.putString("clubName", club.getName());
            Navigation.findNavController(view).navigate(R.id.action_home_to_clubDetail, bundle);
        });
        clubberRecyclerView.setAdapter(clubsAdapter);
    }

    private void setupDJsRecyclerView(View view) {
        DJRecyclerView = view.findViewById(R.id.DJsRecyclerView);
        DJRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        DJsAdapter = new DjAdapter(DJItems, dj -> {
            // Navigate to DJ profile
            Bundle bundle = new Bundle();
            bundle.putString("uid", dj.getDjId());
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
            Club club = new Club(clubName, genres, "", "");
            clubItems.add(club);
        }

//        // For each club, query the current DJ
//        for (int i = 0; i < clubItems.size(); i++) {
//            final int index = i;
//            Club club = clubItems.get(index);
//            FirebaseFirestore.getInstance()
//                    .collection(Constants.COLLECTION_DJ_PROFILES)
//                    .whereEqualTo("currentClub", club.getName())
//                    .limit(1)
//                    .get()
//                    .addOnSuccessListener(querySnapshot -> {
//                        if (!querySnapshot.isEmpty()) {
//                            String djName = querySnapshot.getDocuments().get(0).getString("stageName");
//                            club.setCurrentDJ(djName != null ? djName : "");
//                        }
//                        clubsAdapter.updateData(clubItems);
//                    });
//        }

        clubsAdapter.updateData(clubItems);
    }

    private void loadPopularDJs() {
        FirebaseDBConnection.getInstance().getDB()
                .collection(Constants.COLLECTION_DJ_PROFILES)
                .limit(20)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    DJItems.clear();
                    DJItems.addAll(querySnapshot.toObjects(Dj.class));
                    DJsAdapter.updateData(DJItems);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading DJs", e));
    }
}
