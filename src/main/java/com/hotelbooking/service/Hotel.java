package com.hotelbooking.service;

import com.hotelbooking.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class Hotel implements Serializable {
    private final HashMap<Integer, Room> rooms = new HashMap<>();
    private final HashMap<UUID, Booking> bookings = new HashMap<>();

    // ----- Rooms -----

    public void addRoom(Room room) {
        if (rooms.containsKey(room.getNumber())) {
            throw new IllegalArgumentException("Room already exists: " + room.getNumber());
        }
        rooms.put(room.getNumber(), room);
    }

    public void editRoom(int number, RoomType type, double price, RoomStatus status) {
        Room r = rooms.get(number);
        if (r == null) {
            throw new IllegalArgumentException("Room not found: " + number);
        }
        r.setType(type);
        r.setPricePerNight(price);
        r.setStatus(status);
    }

    public void deleteRoom(int number) {
        Room r = rooms.get(number);
        if (r == null) {
            throw new IllegalArgumentException("Room not found: " + number);
        }
        LocalDate today = LocalDate.now();
        for (Booking b : bookings.values()) {
            if (b.getRoomNumber() == number &&
                    b.getStatus() == BookingStatus.ACTIVE &&
                    b.getCheckOut().isAfter(today)) {
                throw new IllegalStateException("Cannot delete room with future active bookings.");
            }
        }
        rooms.remove(number);
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public Room getRoom(int number) {
        return rooms.get(number);
    }

    // ----- Bookings -----

    public Booking createBooking(String guestName, int roomNumber,
                                 LocalDate checkIn, LocalDate checkOut,
                                 String notes) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out.");
        }
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room does not exist.");
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException("Room is not available.");
        }

        for (Booking b : bookings.values()) {
            if (b.getStatus() == BookingStatus.ACTIVE &&
                    b.getRoomNumber() == roomNumber &&
                    datesOverlap(checkIn, checkOut, b.getCheckIn(), b.getCheckOut())) {
                throw new IllegalStateException("Overlapping booking for this room.");
            }
        }

        Booking booking = new Booking(guestName, roomNumber, checkIn, checkOut, notes);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public void editBooking(UUID id, String guestName, int roomNumber,
                            LocalDate checkIn, LocalDate checkOut, String notes) {
        Booking b = bookings.get(id);
        if (b == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot edit cancelled booking.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out.");
        }
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room does not exist.");
        }

        // Check overlap except this booking
        for (Booking other : bookings.values()) {
            if (!other.getId().equals(id) &&
                    other.getStatus() == BookingStatus.ACTIVE &&
                    other.getRoomNumber() == roomNumber &&
                    datesOverlap(checkIn, checkOut, other.getCheckIn(), other.getCheckOut())) {
                throw new IllegalStateException("Overlapping booking for this room.");
            }
        }

        b.setGuestName(guestName);
        b.setRoomNumber(roomNumber);
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setNotes(notes);
    }

    public void cancelBooking(UUID id) {
        Booking b = bookings.get(id);
        if (b == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        b.setStatus(BookingStatus.CANCELLED);
    }

    public Collection<Booking> getBookings() {
        return bookings.values();
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1,
                                 LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // ----- Search / filter helpers -----

    public List<Booking> searchBookings(String text, LocalDate from, LocalDate to) {
        String t = text == null ? "" : text.trim().toLowerCase();
        return bookings.values().stream()
                .filter(b -> {
                    boolean matchesText = t.isEmpty() ||
                            b.getGuestName().toLowerCase().contains(t) ||
                            String.valueOf(b.getRoomNumber()).contains(t);
                    boolean matchesDate = true;
                    if (from != null) {
                        matchesDate = !b.getCheckIn().isBefore(from);
                    }
                    if (matchesDate && to != null) {
                        matchesDate = !b.getCheckIn().isAfter(to);
                    }
                    return matchesText && matchesDate;
                })
                .sorted(Comparator.comparing(Booking::getCheckIn))
                .collect(Collectors.toList());
    }

    // ----- Statistics -----

    public Map<RoomType, Long> countFreeRoomsByType(LocalDate date) {
        Map<RoomType, Long> result = new EnumMap<>(RoomType.class);
        for (RoomType type : RoomType.values()) {
            result.put(type, 0L);
        }

        for (Room room : rooms.values()) {
            if (room.getStatus() != RoomStatus.AVAILABLE) continue;
            boolean occupied = false;
            for (Booking b : bookings.values()) {
                if (b.getStatus() == BookingStatus.ACTIVE &&
                        b.getRoomNumber() == room.getNumber() &&
                        !date.isBefore(b.getCheckIn()) &&
                        date.isBefore(b.getCheckOut())) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) {
                result.put(room.getType(), result.get(room.getType()) + 1);
            }
        }
        return result;
    }

    public long totalBookedNightsInMonth(YearMonth ym) {
        long nights = 0;
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.plusMonths(1).atDay(1); // exclusive
        for (Booking b : bookings.values()) {
            if (b.getStatus() != BookingStatus.ACTIVE) continue;
            LocalDate start = b.getCheckIn().isBefore(monthStart) ? monthStart : b.getCheckIn();
            LocalDate end = b.getCheckOut().isAfter(monthEnd) ? monthEnd : b.getCheckOut();
            if (start.isBefore(end)) {
                nights += (end.toEpochDay() - start.toEpochDay());
            }
        }
        return nights;
    }

    // ----- Persistence -----

    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    public static Hotel loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Hotel) ois.readObject();
        }
    }
}
