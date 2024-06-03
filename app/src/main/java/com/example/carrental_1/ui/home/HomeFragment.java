package com.example.carrental_1.ui.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrental_1.CarsAdapter;
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

public class HomeFragment extends Fragment implements CarsAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private CarsAdapter adapter;
    private List<Car> carList;
    private boolean isAdmin;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin");
        }

        db = FirebaseFirestore.getInstance();
        carList = new ArrayList<>();
        adapter = new CarsAdapter(carList, this, isAdmin);

        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(adapter);

        fetchAvailableCars();
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

    @Override
    public void onDeleteClick(Car car) {
        // Handle car deletion logic
    }

    @Override
    public void onEditClick(Car car) {
        // Handle car edit logic
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
                    // Update the car record's isAvailable field to false
                    db.collection("cars").document(car.getId())
                            .update("isAvailable", false)
                            .addOnSuccessListener(aVoid -> { fetchAvailableCars(); Toast.makeText(getContext(), "Reservation made successfully", Toast.LENGTH_SHORT).show();})
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update car availability", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to make reservation", Toast.LENGTH_SHORT).show());
    }
}
