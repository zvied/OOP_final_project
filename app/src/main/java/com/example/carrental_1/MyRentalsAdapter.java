package com.example.carrental_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrental_1.data.model.Car;
import com.example.carrental_1.data.model.Reservation;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyRentalsAdapter extends RecyclerView.Adapter<MyRentalsAdapter.RentalViewHolder> {

    private List<Reservation> rentalList;

    public MyRentalsAdapter(List<Reservation> rentalList) {
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental, parent, false);
        return new RentalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        Reservation reservation = rentalList.get(position);
        holder.makeModel.setText(reservation.getCar().getMake() + " " + reservation.getCar().getModel());
        holder.year.setText(reservation.getCar().getYear());
        holder.fuelType.setText(reservation.getCar().getFuelType());
        holder.transmissionType.setText(reservation.getCar().getTransmissionType());
        holder.pricePerDay.setText(String.format("%.2f", Double.parseDouble(reservation.getCar().getPricePerDay())) + "€");
        holder.reservationStartTime.setText(reservation.getReservationStartTime().toString());
        holder.reservationEndTime.setText(reservation.getReservationEndTime().toString());
        holder.totalPrice.setText(String.format("%.2f", (double) reservation.getTotalPrice()) + "€");

        long duration = reservation.getReservationEndTime().getTime() - reservation.getReservationStartTime().getTime();
        String durationText = formatDuration(duration);
        holder.reservationDuration.setText(durationText);
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    static class RentalViewHolder extends RecyclerView.ViewHolder {
        TextView makeModel, year, fuelType, transmissionType, pricePerDay, reservationStartTime, reservationEndTime, totalPrice, reservationDuration;

        RentalViewHolder(View view) {
            super(view);
            makeModel = view.findViewById(R.id.textMakeModel);
            year = view.findViewById(R.id.textYear);
            fuelType = view.findViewById(R.id.textFuelType);
            transmissionType = view.findViewById(R.id.textTransmissionType);
            pricePerDay = view.findViewById(R.id.textPricePerDay);
            reservationStartTime = view.findViewById(R.id.textReservationStartTime);
            reservationEndTime = view.findViewById(R.id.textReservationEndTime);
            totalPrice = view.findViewById(R.id.textTotalPrice);
            reservationDuration = view.findViewById(R.id.textReservationDuration);
        }
    }

    private String formatDuration(long durationMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);

        StringBuilder duration = new StringBuilder();
        if (days > 0) {
            duration.append(days).append(" days ");
        }
        if (hours > 0 || days > 0) {
            duration.append(hours).append(" hours ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            duration.append(minutes).append(" minutes ");
        }
        duration.append(seconds).append(" seconds");

        return duration.toString();
    }
}
