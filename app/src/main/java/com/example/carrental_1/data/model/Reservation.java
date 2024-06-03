package com.example.carrental_1.data.model;

import java.util.Date;

public class Reservation {
    private String id;
    private String carId;
    private String userId;
    private Date reservationStartTime;
    private Date reservationEndTime;
    private boolean isReservationOver;
    private double totalPrice;
    private long reservationDuration;
    private Car car;


    public Reservation() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getReservationStartTime() {
        return reservationStartTime;
    }

    public void setReservationStartTime(Date reservationStartTime) {
        this.reservationStartTime = reservationStartTime;
    }

    public Date getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(Date reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    public boolean getIsReservationOver() {
        return isReservationOver;
    }

    public void setIsReservationOver(boolean isReservationOver) {
        this.isReservationOver = isReservationOver;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getReservationDuration() {
        return reservationDuration;
    }

    public void setReservationDuration(long reservationDuration) {
        this.reservationDuration = reservationDuration;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
