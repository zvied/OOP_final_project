package com.example.carrental_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrental_1.data.model.Car;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarViewHolder> {

    private List<Car> carList;
    private OnItemClickListener onItemClickListener;
    private boolean isAdmin;

    public CarsAdapter(List<Car> carList, OnItemClickListener onItemClickListener, boolean isAdmin) {
        this.carList = carList;
        this.onItemClickListener = onItemClickListener;
        this.isAdmin = isAdmin;
    }

    public interface OnItemClickListener {
        void onDeleteClick(Car car);
        void onEditClick(Car car);
        void onMakeReservationClick(Car car);
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
        holder.pricePerDay.setText(String.format("%.2f", Double.parseDouble(car.getPricePerDay())) + "€");
        holder.fuel.setText(car.getFuelType());
        holder.transmission.setText(car.getTransmissionType());

        if (isAdmin) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.makeReservationButton.setVisibility(View.GONE);

            holder.editButton.setOnClickListener(v -> onItemClickListener.onEditClick(car));
            holder.deleteButton.setOnClickListener(v -> {
                car.setIsDeleted(true);
                onItemClickListener.onDeleteClick(car);
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.makeReservationButton.setVisibility(View.VISIBLE);

            holder.makeReservationButton.setOnClickListener(v -> onItemClickListener.onMakeReservationClick(car));
        }

        RelativeLayout.LayoutParams borderParams = (RelativeLayout.LayoutParams) holder.borderView.getLayoutParams();
        if (holder.makeReservationButton.getVisibility() == View.VISIBLE) {
            borderParams.addRule(RelativeLayout.BELOW, R.id.buttonMakeReservation);
        } else {
            borderParams.addRule(RelativeLayout.BELOW, R.id.buttonDeleteCar);
        }
        holder.borderView.setLayoutParams(borderParams);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView makeModel, year, pricePerDay, fuel, transmission;
        Button editButton, deleteButton, makeReservationButton;
        View borderView;

        CarViewHolder(View view) {
            super(view);
            makeModel = view.findViewById(R.id.textMakeModel);
            year = view.findViewById(R.id.textYear);
            pricePerDay = view.findViewById(R.id.textPricePerDay);
            fuel = view.findViewById(R.id.textFuel);
            transmission = view.findViewById(R.id.textTransmission);
            editButton = view.findViewById(R.id.buttonEditCar);
            deleteButton = view.findViewById(R.id.buttonDeleteCar);
            makeReservationButton = view.findViewById(R.id.buttonMakeReservation);
            borderView = view.findViewById(R.id.borderView);
        }
    }
}
