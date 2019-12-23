package com.johnyhawkdesigns.a56_dentistapp.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.johnyhawkdesigns.a56_dentistapp.MainActivity;
import com.johnyhawkdesigns.a56_dentistapp.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class DashboardFragment extends Fragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();

    private DashboardViewModel dashboardViewModel;

    FirebaseFirestore firestoreDatabase;

    Button btnAddDataToFireStore;
    Button btnRetrieveDataFromFireStore;
    TextView tvRetrievedData;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);

        Log.d(TAG, "onCreateView: DashboardFragment");

        final TextView textView = view.findViewById(R.id.textView3);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        firestoreDatabase = FirebaseFirestore.getInstance();

        btnAddDataToFireStore = view.findViewById(R.id.btnStoreData);
        btnRetrieveDataFromFireStore = view.findViewById(R.id.btnRetrieveData);
        tvRetrievedData = view.findViewById(R.id.retrievedData);

        btnAddDataToFireStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Store data", Toast.LENGTH_SHORT).show();
                addDataToFireStore();
            }
        });


        btnRetrieveDataFromFireStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Retrieve data", Toast.LENGTH_SHORT).show();
                retrieveDataFromFireStore();
            }
        });


        return view;
    }

    private void retrieveDataFromFireStore() {

        firestoreDatabase.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                tvRetrievedData.setText(document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void addDataToFireStore(){

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("username", "Ahsan");
        user.put("password", 123);

        // Add a new document with a generated ID
        firestoreDatabase.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}
