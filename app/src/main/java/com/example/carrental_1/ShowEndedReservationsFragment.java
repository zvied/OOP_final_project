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
import com.example.carrental_1.databinding.FragmentShowEndedReservationsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowEndedReservationsFragment extends Fragment {

    private FragmentShowEndedReservationsBinding binding;
    private FirebaseFirestore db;
    private EndedReservationsAdapter adapter;
    private List<Reservation> reservationList;
    private int pendingFetches = 0;

    public ShowEndedReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowEndedReservationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        reservationList = new ArrayList<>();
        adapter = new EndedReservationsAdapter(reservationList);

        binding.recyclerViewEndedReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewEndedReservations.setAdapter(adapter);

        fetchEndedReservations();
    }

    private void fetchEndedReservations() {
        db.collection("reservations")
                .whereEqualTo("isReservationOver", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        reservationList.clear();
                        pendingFetches = queryDocumentSnapshots.size();
                        if (pendingFetches == 0) {
                            updateReservationsUI();
                        } else {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Reservation reservation = document.toObject(Reservation.class);
                                if (reservation != null) {
                                    reservation.setId(document.getId());
                                    fetchCarDetails(reservation);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch reservations", Toast.LENGTH_SHORT).show();
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
                            reservationList.add(reservation);
                        }
                        pendingFetches--;
                        if (pendingFetches == 0) {
                            updateReservationsUI();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pendingFetches--;
                        if (pendingFetches == 0) {
                            updateReservationsUI();
                        }
                        Toast.makeText(getContext(), "Failed to fetch car details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateReservationsUI() {
        if (reservationList.isEmpty()) {
            binding.textNoEndedReservations.setVisibility(View.VISIBLE);
        } else {
            binding.textNoEndedReservations.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
