package com.example.carrental_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.data.model.Reservation;
import com.example.carrental_1.databinding.FragmentMyRentalsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRentalsFragment extends Fragment {
    private FragmentMyRentalsBinding binding;
    private FirebaseFirestore db;
    private MyRentalsAdapter adapter;
    private List<Reservation> rentalList;
    private FirebaseAuth auth;

    public MyRentalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyRentalsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        rentalList = new ArrayList<>();
        adapter = new MyRentalsAdapter(rentalList);

        binding.recyclerViewRentals.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewRentals.setAdapter(adapter);

        fetchPreviousRentals();
    }

    private void fetchPreviousRentals() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("reservations")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isReservationOver", true)
                .orderBy("reservationStartTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        rentalList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if (reservation != null) {
                                reservation.setId(document.getId());
                                fetchCarDetails(reservation);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch rentals", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCarDetails(Reservation reservation) {
        db.collection("cars").document(reservation.getCarId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Car car = documentSnapshot.toObject(Car.class);
                        if (car != null) {
                            car.setId(documentSnapshot.getId());
                            reservation.setCar(car);
                            rentalList.add(reservation);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch car details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
