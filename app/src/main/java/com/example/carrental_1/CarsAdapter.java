package com.example.carrental_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrental_1.data.model.Car;

import java.io.Serializable;
import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarViewHolder> {

    private List<Car> carList;
    private
    ViewCarsAdminFragment onItemClickListener;

    public CarsAdapter(List<Car> carList,
                       ViewCarsAdminFragment onItemClickListener) {
        this.carList = carList;
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onDeleteClick(Car car);
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.makeModel.setText(car.getMake() + " " + car.getModel());
        holder.year.setText(car.getYear());
        holder.pricePerDay.setText(car.getPricePerDay());
        holder.editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("car", car);
            Navigation.findNavController(v).navigate(R.id.nav_edit_car, bundle);
        });
        holder.deleteButton.setOnClickListener(v -> {
            // Update the car with isDeleted = true
            car.setIsDeleted(true);
            onItemClickListener.onDeleteClick(car);
        });
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(car));
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView makeModel, year, pricePerDay;
        Button editButton, deleteButton;

        CarViewHolder(View view) {
            super(view);
            makeModel = view.findViewById(R.id.textMakeModel);
            year = view.findViewById(R.id.textYear);
            pricePerDay = view.findViewById(R.id.textPricePerDay);
            editButton = view.findViewById(R.id.buttonEditCar);
            deleteButton = view.findViewById(R.id.buttonDeleteCar);
        }
    }
}