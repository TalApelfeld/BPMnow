package com.example.bpmnow.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bpmnow.MainActivity;
import com.example.bpmnow.R;
import com.example.bpmnow.db.UsersManager;
import com.example.bpmnow.network.FirebaseAuthConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signIn extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userRole;

    public signIn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signIn.
     */
    // TODO: Rename and change types and number of parameters
    public static signIn newInstance(String param1, String param2) {
        signIn fragment = new signIn();
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
        View view = inflater.inflate(R.layout.fragment_auth_sign_in, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView toSignUpPageBtn = view.findViewById(R.id.toSignUpPageBtn);
        MaterialButton loginBtn = view.findViewById(R.id.logInBtn);

        toSignUpPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(signIn.this).navigate(R.id.action_signIn_to_signUp);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText emailField = view.findViewById(R.id.emailTextInputEditText);
                TextInputEditText passwordField = view.findViewById(R.id.passwordTextInputEditText);
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                FirebaseAuthConnection.getInstance().getAuth().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    FirebaseUser user = FirebaseAuthConnection.getInstance().getAuth().getCurrentUser();
                                    userExistsInDB(email);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(requireActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void userExistsInDB(String email) {
        UsersManager.getInstance().getUserDocument(FirebaseAuthConnection.getInstance().getUserId())
                .addOnSuccessListener(document -> {
                    String role = document.getString("role");
                    // use role here
                    userRole = role;

                    if (userRole.equals("dj")) {
                        ((MainActivity) requireActivity()).switchToGraphDJ();
                    } else if (userRole.equals("clubber")) {
                        ((MainActivity) requireActivity()).switchToGraphClubber();
                    } else {
                        NavHostFragment.findNavController(signIn.this).navigate(R.id.action_signIn_to_roleSelection);
                    }
                })
                .addOnFailureListener(e -> {
                    // handle error
                    NavHostFragment.findNavController(signIn.this).navigate(R.id.action_signIn_to_roleSelection);
                });
    }

}