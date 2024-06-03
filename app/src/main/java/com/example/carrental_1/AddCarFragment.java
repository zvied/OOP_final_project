package com.example.carrental_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.carrental_1.databinding.FragmentAddCarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCarFragment extends Fragment {

    private FragmentAddCarBinding binding;
    private FirebaseFirestore db;

    public AddCarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using data binding
        binding = FragmentAddCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setupSpinners();

        binding.buttonSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarToFirestore();
            }
        });
    }

    private void setupSpinners() {
        // Setup Fuel Type Spinner
        Spinner spinnerFuelType = binding.spinnerFuelType;
        ArrayAdapter<CharSequence> adapterFuelType = ArrayAdapter.createFromResource(getContext(),
                R.array.fuel_type_array, android.R.layout.simple_spinner_item);
        adapterFuelType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapterFuelType);

        // Setup Transmission Type Spinner
        Spinner spinnerTransmissionType = binding.spinnerTransmissionType;
        ArrayAdapter<CharSequence> adapterTransmissionType = ArrayAdapter.createFromResource(getContext(),
                R.array.transmission_type_array, android.R.layout.simple_spinner_item);
        adapterTransmissionType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransmissionType.setAdapter(adapterTransmissionType);
    }

    private void addCarToFirestore() {
        String make = binding.editTextMake.getText().toString().trim();
        String model = binding.editTextModel.getText().toString().trim();
        String year = binding.editTextYear.getText().toString().trim();
        String fuelType = binding.spinnerFuelType.getSelectedItem().toString();
        String transmissionType = binding.spinnerTransmissionType.getSelectedItem().toString();
        String pricePerDay = binding.editTextPricePerDay.getText().toString().trim();

        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || fuelType.equals("Select Fuel Type") || transmissionType.equals("Select Transmission Type") || pricePerDay.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> car = new HashMap<>();
        car.put("make", make);
        car.put("model", model);
        car.put("year", year);
        car.put("fuelType", fuelType);
        car.put("transmissionType", transmissionType);
        car.put("pricePerDay", pricePerDay);
        car.put("isAvailable", true);
        car.put("isDeleted", false);

        db.collection("cars")
                .add(car)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Car added successfully", Toast.LENGTH_SHORT).show();
                        NavController navController = NavHostFragment.findNavController(AddCarFragment.this);
                        navController.popBackStack();
                        navController.navigate(R.id.nav_view_cars_admin);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add car", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
