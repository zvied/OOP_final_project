package com.example.carrental_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.data.model.Reservation;
import com.example.carrental_1.databinding.FragmentReturnCarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReturnCarFragment extends Fragment implements ReturnCarAdapter.OnItemClickListener {
    private static final String TAG = "ReturnCarFragment";
    private FragmentReturnCarBinding binding;
    private FirebaseFirestore db;
    private ReturnCarAdapter adapter;
    private List<Reservation> reservationList;
    private FirebaseAuth auth;
    private int pendingFetches = 0;

    public ReturnCarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReturnCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        reservationList = new ArrayList<>();
        adapter = new ReturnCarAdapter(reservationList, this);

        binding.recyclerViewReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewReservations.setAdapter(adapter);

        fetchCurrentReservations();
    }

    private void fetchCurrentReservations() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("reservations")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isReservationOver", false)
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
                            adapter.notifyDataSetChanged();
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
            Log.d(TAG, "No current reservations");
            binding.textNoReservations.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Current reservations available");
            binding.textNoReservations.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onReturnCarClick(Reservation reservation) {
        returnCar(reservation);
    }

    private void returnCar(Reservation reservation) {
        Date endTime = new Date();
        long duration = endTime.getTime() - reservation.getReservationStartTime().getTime();
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        if (days == 0) {
            days = 1;
        }
        double totalPrice = days * Integer.parseInt(reservation.getCar().getPricePerDay());


        reservation.setReservationEndTime(endTime);
        reservation.setIsReservationOver(true);
        reservation.setTotalPrice(totalPrice);
        reservation.setReservationDuration(duration);

        db.collection("cars").document(reservation.getCarId())
                .update("isAvailable", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("reservations").document(reservation.getId())
                                .set(reservation)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Car returned successfully", Toast.LENGTH_SHORT).show();
                                        fetchCurrentReservations();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to update reservation", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update car availability", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
