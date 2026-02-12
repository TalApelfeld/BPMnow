package com.example.bpmnow.ui.clubber;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.adapters.ClubAdapter;
import com.example.bpmnow.adapters.DjAdapter;
import com.example.bpmnow.models.Club;
import com.example.bpmnow.models.Dj;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeClubber#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeClubber extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView clubberRecyclerView;
    private RecyclerView DJRecyclerView;
    private ClubAdapter clubsAdapter;
    private DjAdapter DJsAdapter;
    private List<Club> clubItems = new ArrayList<>();
    private List<Dj> DJItems = new ArrayList<>();

    public homeClubber() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeClubber.
     */
    // TODO: Rename and change types and number of parameters
    public static homeClubber newInstance(String param1, String param2) {
        homeClubber fragment = new homeClubber();
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
        View view = inflater.inflate(R.layout.fragment_home_clubber, container, false);

        ((MainActivity) requireActivity()).setClubberBottomNavigationVisible();
        setupClubsRecyclerView(view);
        setupDJsRecyclerView(view);
        loadDataClubber();
        loadDataDJs();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupClubsRecyclerView(View view) {
        clubberRecyclerView = view.findViewById(R.id.clubsRecyclerView);
        clubberRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false));
        clubsAdapter = new ClubAdapter(clubItems);
        clubberRecyclerView.setAdapter(clubsAdapter);
    }

    private void setupDJsRecyclerView(View view) {
        DJRecyclerView = view.findViewById(R.id.DJsRecyclerView);
//        If we want grid layout we do it like this (and need to switch the width of the item in the recycler view
//        to wrap_content, so other items can be side by side)
//        contactsRecView.setLayoutManager(new GridLayoutManager(this,2));
        DJRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false));
        DJsAdapter = new DjAdapter(DJItems);
        DJRecyclerView.setAdapter(DJsAdapter);
    }

    private void loadDataClubber() {
        clubItems.add(new Club(
                "Velvet Underground",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal")),
                "0.5 km",
                "DJ Shadow"
        ));

        clubItems.add(new Club(
                "Neon Nights",
                new ArrayList<>(Arrays.asList("EDM", "Trance", "Progressive")),
                "1.2 km",
                "Armin Van Buuren"
        ));

        clubItems.add(new Club(
                "The Bassment",
                new ArrayList<>(Arrays.asList("Drum & Bass", "Dubstep", "Jungle")),
                "2.0 km",
                "DJ Fresh"
        ));

        clubItems.add(new Club(
                "Studio 54",
                new ArrayList<>(Arrays.asList("Disco", "Funk", "Soul")),
                "0.8 km",
                "Nile Rodgers"
        ));

        clubItems.add(new Club(
                "Warehouse Project",
                new ArrayList<>(Arrays.asList("Techno", "Industrial", "Acid")),
                "3.5 km",
                "Charlotte de Witte"
        ));

        clubItems.add(new Club(
                "Paradise Garage",
                new ArrayList<>(Arrays.asList("Deep House", "Garage", "Soulful")),
                "1.8 km",
                "Kerri Chandler"
        ));

        clubItems.add(new Club(
                "Berghain TLV",
                new ArrayList<>(Arrays.asList("Techno", "Hard Techno", "EBM")),
                "4.2 km",
                "Ben Klock"
        ));

        clubItems.add(new Club(
                "Rhythm Factory",
                new ArrayList<>(Arrays.asList("Hip Hop", "R&B", "Afrobeats")),
                "0.3 km",
                "DJ Khaled"
        ));

        clubsAdapter.updateData(clubItems);
    }

    private void loadDataDJs() {
        DJItems.add(new Dj(
                "Red Axeses",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));
        DJItems.add(new Dj(
                "DJ Elon Matana",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));
        DJItems.add(new Dj(
                "DJ Khaled",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));
        DJItems.add(new Dj(
                "DJ Malka",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));
        DJItems.add(new Dj(
                "DJ Shadow",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));
        DJItems.add(new Dj(
                "DJ BL3SS",
                new ArrayList<>(Arrays.asList("Techno", "House", "Minimal"))));

        DJsAdapter.updateData(DJItems);
    }

}