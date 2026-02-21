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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.adapters.ClubberDjAdapter;
import com.example.bpmnow.db.DjLikesManager;
import com.example.bpmnow.db.DjProfilesManager;
import com.example.bpmnow.models.clubber.DjCardItem;
import com.example.bpmnow.models.dj.Dj;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class favoritesClubber extends Fragment {

    private static final String TAG = "favoritesClubber";
    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private ClubberDjAdapter clubberDjAdapter;
    private List<DjCardItem> favoriteDjs = new ArrayList<>();
    private final DjLikesManager djLikesManager = DjLikesManager.getInstance();
    private final DjProfilesManager djProfilesManager = DjProfilesManager.getInstance();
    private String currentUid;

    public favoritesClubber() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clubber_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUid = FirebaseAuthConnection.getInstance().getUserId();

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        clubberDjAdapter = new ClubberDjAdapter(favoriteDjs, dj -> {
            Bundle bundle = new Bundle();
            bundle.putString("djId", dj.getUid());
            Navigation.findNavController(view).navigate(R.id.action_favorites_to_djProfile, bundle);
        });
        rvFavorites.setAdapter(clubberDjAdapter);

        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
//       1) Getting docs with matching clubber id, then extracting the dj id's.
        djLikesManager.getLikesByClubber(currentUid)
                .addOnSuccessListener(querySnapshot -> {
                    favoriteDjs.clear();
                    List<String> djIds = new ArrayList<>();
//                    Extracting dj id's from docs.
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String djId = doc.getString("djId");
                        if (djId != null) djIds.add(djId);
                    }
//                    Updating ui if no favorites.
                    if (djIds.isEmpty()) {
                        tvNoFavorites.setVisibility(View.VISIBLE);
                        rvFavorites.setVisibility(View.GONE);
                        clubberDjAdapter.updateData(favoriteDjs);
                        return;
                    }

                    // 2) Load DJ profiles based on dj id's
                    for (String djId : djIds) {
                        djProfilesManager.getDjProfile(djId).addOnSuccessListener(doc -> {
                                    if (doc != null) {
                                        favoriteDjs.add(doc.toObject(DjCardItem.class));
                                        clubberDjAdapter.updateData(favoriteDjs);
                                    }
                                    tvNoFavorites.setVisibility(favoriteDjs.isEmpty() ? View.VISIBLE : View.GONE);
                                    rvFavorites.setVisibility(favoriteDjs.isEmpty() ? View.GONE : View.VISIBLE);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error loading favorites", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading favorites", e));
    }
}
