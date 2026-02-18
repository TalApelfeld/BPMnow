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
import com.example.bpmnow.adapters.DjAdapter;
import com.example.bpmnow.models.Dj;
import com.example.bpmnow.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class favoritesClubber extends Fragment {

    private static final String TAG = "favoritesClubber";
    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private DjAdapter djAdapter;
    private List<Dj> favoriteDjs = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUid;

    public favoritesClubber() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites_clubber, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        djAdapter = new DjAdapter(favoriteDjs, dj -> {
            Bundle bundle = new Bundle();
            bundle.putString("djId", dj.getDjId());
            Navigation.findNavController(view).navigate(R.id.action_favorites_to_djProfile, bundle);
        });
        rvFavorites.setAdapter(djAdapter);

        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        db.collection(Constants.COLLECTION_DJ_LIKES)
                .whereEqualTo("clubberId", currentUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    favoriteDjs.clear();
                    List<String> djIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String djId = doc.getString("djId");
                        if (djId != null) djIds.add(djId);
                    }

                    if (djIds.isEmpty()) {
                        tvNoFavorites.setVisibility(View.VISIBLE);
                        rvFavorites.setVisibility(View.GONE);
                        djAdapter.updateData(favoriteDjs);
                        return;
                    }

                    // Load DJ profiles for each liked DJ
                    for (String djId : djIds) {
                        db.collection(Constants.COLLECTION_DJ_PROFILES).document(djId)
                                .get()
                                .addOnSuccessListener(djDoc -> {
                                    if (djDoc.exists()) {
                                        Dj dj = djDoc.toObject(Dj.class);
                                        if (dj != null) {
                                            dj.setDjId(djDoc.getId());
                                            favoriteDjs.add(dj);
                                            djAdapter.updateData(favoriteDjs);
                                        }
                                    }
                                    tvNoFavorites.setVisibility(favoriteDjs.isEmpty() ? View.VISIBLE : View.GONE);
                                    rvFavorites.setVisibility(favoriteDjs.isEmpty() ? View.GONE : View.VISIBLE);
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading favorites", e));
    }
}
