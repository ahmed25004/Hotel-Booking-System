package com.hotelbooking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Booking implements Serializable {
    private final UUID id;
    private String guestName;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String notes;
    private BookingStatus status;

    public Booking(String guestName, int roomNumber, LocalDate checkIn,
                   LocalDate checkOut, String notes) {
        this.id = UUID.randomUUID();
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.notes = notes;
        this.status = BookingStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
