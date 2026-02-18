package com.example.bpmnow.ui.auth;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link roleSelection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class roleSelection extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String selectedRole = null;
    private MaterialCardView cardClubber, cardDJ;
    private MaterialButton btnContinue;

    public roleSelection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment roleSelection.
     */
    // TODO: Rename and change types and number of parameters
    public static roleSelection newInstance(String param1, String param2) {
        roleSelection fragment = new roleSelection();
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

        // Handle back button press - exit app instead of going back
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Exit the app when back is pressed
                requireActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_role_selection, container, false);

        cardClubber = view.findViewById(R.id.cardClubber);
        cardDJ = view.findViewById(R.id.cardDJ);
        btnContinue = view.findViewById(R.id.btnContinue);

        cardClubber.setOnClickListener(v -> selectRole(Constants.ROLE_CLUBBER));
        cardDJ.setOnClickListener(v -> selectRole(Constants.ROLE_DJ));
        btnContinue.setOnClickListener(v -> confirmRole());
        return view;
    }

    private void selectRole(String role) {
        selectedRole = role;
        btnContinue.setEnabled(true);
        btnContinue.setAlpha(1f);
        if (Constants.ROLE_CLUBBER.equals(role)) {
            cardClubber.setStrokeWidth(3);
            cardDJ.setStrokeWidth(1);
            cardClubber.setAlpha(1f);
            cardDJ.setAlpha(0.6f);
        } else {
            cardDJ.setStrokeWidth(3);
            cardClubber.setStrokeWidth(1);
            cardDJ.setAlpha(1f);
            cardClubber.setAlpha(0.6f);
        }
    }

    private void confirmRole() {
        if (selectedRole == null) return;
        if (selectedRole.equals(Constants.ROLE_CLUBBER)) {
//                Getting the method from MainActivity (this is the approach because the standard is to have one activity
//                and just change the navigation graphs, and we do this from the main activity, but we cant access
//                the elements of the fragment from the activity so we can do this)

            ((MainActivity) requireActivity()).switchToGraphClubber();
        } else if (selectedRole.equals(Constants.ROLE_DJ)) {
//                Getting the method from MainActivity (this is the approach because the standard is to have one activity
//                and just change the navigation graphs, and we do this from the main activity, but we cant access
//                the elements of the fragment from the activity so we can do this)

            ((MainActivity) requireActivity()).switchToFormDJ();
        }
    }
}