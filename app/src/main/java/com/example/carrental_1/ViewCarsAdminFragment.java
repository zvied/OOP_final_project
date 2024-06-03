package com.example.carrental_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.databinding.FragmentViewCarsAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewCarsAdminFragment extends Fragment implements CarsAdapter.OnItemClickListener {
    private static final String TAG = "ViewCarsAdminFragment";
    private FragmentViewCarsAdminBinding binding;
    private FirebaseFirestore db;
    private CarsAdapter adapter;
    private List<Car> carList;

    public ViewCarsAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewCarsAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        carList = new ArrayList<>();
        adapter = new CarsAdapter(carList, this, true); // Admin view

        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(adapter);

        fetchCars();
    }

    private void fetchCars() {
        db.collection("cars")
                .whereEqualTo("isDeleted", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        carList.clear();
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "No cars found");
                        } else {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Car car = document.toObject(Car.class);
                                if (car != null) {
                                    car.setId(document.getId());
                                    carList.add(car);
                                    Log.d(TAG, "Car found: " + car.getMake() + " " + car.getModel());
                                } else {
                                    Log.d(TAG, "Car object is null");
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to fetch cars", e);
                        Toast.makeText(getContext(), "Failed to fetch cars", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeleteClick(Car car) {
        // Get a reference to the Firestore document corresponding to the car
        DocumentReference carRef = FirebaseFirestore.getInstance().collection("cars").document(car.getId());

        // Update the isDeleted field to true
        car.setIsDeleted(true);

        // Update the Firestore document with the modified data
        carRef.set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Car successfully updated
                        Toast.makeText(getContext(), "Car deleted successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the car list or UI if needed
                        fetchCars();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error updating car
                        Log.e(TAG, "Error deleting car", e);
                        Toast.makeText(getContext(), "Failed to delete car", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditClick(Car car) {
        // Navigate to the edit car fragment with the car details
        Bundle bundle = new Bundle();
        bundle.putSerializable("car", car);
        Navigation.findNavController(getView()).navigate(R.id.nav_edit_car, bundle);
    }

    @Override
    public void onMakeReservationClick(Car car) {
        // Admin should not be making reservations, no implementation needed
    }
}
