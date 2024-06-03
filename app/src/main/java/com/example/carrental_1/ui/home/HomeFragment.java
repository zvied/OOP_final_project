package com.example.carrental_1.ui.home;

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

import com.example.carrental_1.CarsAdapter;
import com.example.carrental_1.HomeReservationsAdapter;
import com.example.carrental_1.MainActivity;
import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.data.model.Reservation;
import com.example.carrental_1.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment implements CarsAdapter.OnItemClickListener , HomeReservationsAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CarsAdapter adapter;
    private HomeReservationsAdapter reservationsAdapter;
    private List<Car> carList;
    private List<Reservation> reservationList;
    private boolean isAdmin;
    private int pendingFetches = 0;
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        carList = new ArrayList<>();
        reservationList = new ArrayList<>();
        adapter = new CarsAdapter(carList, this, false);
        reservationsAdapter = new HomeReservationsAdapter(reservationList, this);

        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(adapter);

        checkUserRoleAndFetchData();
    }



    private void checkUserRoleAndFetchData() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            isAdmin = documentSnapshot.getBoolean("isAdmin") != null && Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"));
                            if (isAdmin) {
                                binding.recyclerViewCars.setAdapter(reservationsAdapter);
                                setFragmentLabel("Current active reservations");
                                fetchActiveReservations();
                            } else {
                                binding.recyclerViewCars.setAdapter(adapter);
                                setFragmentLabel("Available cars");
                                fetchAvailableCars();
                            }
                        } else {
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setFragmentLabel(String label) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(label);
    }

    private void fetchAvailableCars() {
        db.collection("cars")
                .whereEqualTo("isAvailable", true)
                .whereEqualTo("isDeleted", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        carList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Car car = document.toObject(Car.class);
                            car.setId(document.getId());
                            carList.add(car);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch cars", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchActiveReservations() {
        db.collection("reservations")
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
        reservationsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDeleteClick(Car car) {

    }

    @Override
    public void onEditClick(Car car) {

    }

    @Override
    public void onMakeReservationClick(Car car) {
        makeReservation(car);
    }

    private void makeReservation(Car car) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Reservation reservation = new Reservation();
        reservation.setCarId(car.getId());
        reservation.setUserId(userId);
        reservation.setReservationStartTime(new Date());
        reservation.setIsReservationOver(false);

        db.collection("reservations")
                .add(reservation)
                .addOnSuccessListener(documentReference -> {

                    db.collection("cars").document(car.getId())
                            .update("isAvailable", false)
                            .addOnSuccessListener(aVoid -> { fetchAvailableCars(); Toast.makeText(getContext(), "Reservation made successfully", Toast.LENGTH_SHORT).show();})
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update car availability", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to make reservation", Toast.LENGTH_SHORT).show());
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
                                        fetchActiveReservations();
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
