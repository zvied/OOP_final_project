package com.example.carrental_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.databinding.FragmentEditCarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCarFragment extends Fragment {
    private static final String TAG = "ViewCarsAdminFragment";
    private FragmentEditCarBinding binding;
    private FirebaseFirestore db;
    private Car car;

    public EditCarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditCarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setupSpinners();

        if (getArguments() != null) {
            car = (Car) getArguments().getSerializable("car");
            if (car != null) {
                populateFields();
            }
        }

        binding.buttonSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCarInfo();
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapterFuelType = ArrayAdapter.createFromResource(getContext(),
                R.array.fuel_type_array, android.R.layout.simple_spinner_item);
        adapterFuelType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFuelType.setAdapter(adapterFuelType);

        ArrayAdapter<CharSequence> adapterTransmissionType = ArrayAdapter.createFromResource(getContext(),
                R.array.transmission_type_array, android.R.layout.simple_spinner_item);
        adapterTransmissionType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTransmissionType.setAdapter(adapterTransmissionType);
    }

    private void populateFields() {
        Log.d(TAG, "populateFields" + car.toString());
        binding.editTextMake.setText(car.getMake());
        binding.editTextModel.setText(car.getModel());
        binding.editTextYear.setText(car.getYear());
        binding.spinnerFuelType.setSelection(getIndex(binding.spinnerFuelType, car.getFuelType()));
        binding.spinnerTransmissionType.setSelection(getIndex(binding.spinnerTransmissionType, car.getTransmissionType()));
        binding.editTextPricePerDay.setText(car.getPricePerDay());
    }

    private int getIndex(android.widget.Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private void updateCarInfo() {
        Log.d(TAG, "updateCarInfo" + car.toString());
        String make = binding.editTextMake.getText().toString().trim();
        String model = binding.editTextModel.getText().toString().trim();
        String year = binding.editTextYear.getText().toString().trim();
        String fuelType = binding.spinnerFuelType.getSelectedItem().toString();
        String transmissionType = binding.spinnerTransmissionType.getSelectedItem().toString();
        String pricePerDay = binding.editTextPricePerDay.getText().toString().trim();

        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || fuelType.isEmpty() || transmissionType.isEmpty() || pricePerDay.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        car.setMake(make);
        car.setModel(model);
        car.setYear(year);
        car.setFuelType(fuelType);
        car.setTransmissionType(transmissionType);
        car.setPricePerDay(pricePerDay);


        if (car.getId() == null) {
            Toast.makeText(getContext(), "Car ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("cars").document(car.getId())
                .set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Car updated successfully", Toast.LENGTH_SHORT).show();
                        NavController navController = NavHostFragment.findNavController(EditCarFragment.this);
                        navController.popBackStack(R.id.nav_view_cars_admin, false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update car", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
