package com.hotelbooking.model;

import java.io.Serializable;

public class Room implements Serializable {
    private final int number;
    private RoomType type;
    private double pricePerNight;
    private RoomStatus status;

    public Room(int number, RoomType type, double pricePerNight, RoomStatus status) {
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return number + " (" + type + ")";
    }
}
