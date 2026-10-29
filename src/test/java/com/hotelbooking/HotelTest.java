package com.hotelbooking;

import com.hotelbooking.model.*;
import com.hotelbooking.service.Hotel;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HotelTest {

    // Helper: create a hotel with a couple of rooms
    private Hotel createSampleHotel() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, RoomType.SINGLE, 50.0, RoomStatus.AVAILABLE));
        hotel.addRoom(new Room(102, RoomType.DOUBLE, 80.0, RoomStatus.AVAILABLE));
        return hotel;
    }

    @Test
    public void testCreateBooking_Success() {
        Hotel hotel = createSampleHotel();

        LocalDate checkIn = LocalDate.of(2024, 5, 1);
        LocalDate checkOut = LocalDate.of(2024, 5, 5);

        Booking booking = hotel.createBooking(
                "Alice Smith",
                101,
                checkIn,
                checkOut,
                "Business trip"
        );

        assertNotNull("Booking should not be null", booking);
        assertNotNull("Booking id should be generated", booking.getId());
        assertEquals("Alice Smith", booking.getGuestName());
        assertEquals(101, booking.getRoomNumber());
        assertEquals(checkIn, booking.getCheckIn());
        assertEquals(checkOut, booking.getCheckOut());
        assertEquals(BookingStatus.ACTIVE, booking.getStatus());

        // hotel should actually contain this booking
        assertTrue(hotel.getBookings().contains(booking));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateBooking_OverlappingDatesFail() {
        Hotel hotel = createSampleHotel();

        // First booking
        LocalDate in1 = LocalDate.of(2024, 5, 1);
        LocalDate out1 = LocalDate.of(2024, 5, 5);
        hotel.createBooking("Alice", 101, in1, out1, "");

        // Overlapping booking for the same room should throw IllegalStateException
        LocalDate in2 = LocalDate.of(2024, 5, 3);
        LocalDate out2 = LocalDate.of(2024, 5, 7);
        hotel.createBooking("Bob", 101, in2, out2, "");
    }

    @Test
    public void testCancelBooking_ChangesStatusToCancelled() {
        Hotel hotel = createSampleHotel();

        LocalDate in = LocalDate.of(2024, 6, 1);
        LocalDate out = LocalDate.of(2024, 6, 3);
        Booking booking = hotel.createBooking("Charlie", 101, in, out, "");

        UUID id = booking.getId();
        hotel.cancelBooking(id);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    public void testSearchBookings_ByTextAndDate() {
        Hotel hotel = createSampleHotel();

        LocalDate in1 = LocalDate.of(2024, 5, 1);
        LocalDate out1 = LocalDate.of(2024, 5, 3);
        Booking b1 = hotel.createBooking("Alice Johnson", 101, in1, out1, "");

        LocalDate in2 = LocalDate.of(2024, 6, 10);
        LocalDate out2 = LocalDate.of(2024, 6, 12);
        Booking b2 = hotel.createBooking("Bob Smith", 102, in2, out2, "");

        // Search by guest name (case-insensitive)
        List<Booking> byName = hotel.searchBookings("alice", null, null);
        assertEquals(1, byName.size());
        assertEquals(b1.getId(), byName.get(0).getId());

        // Search by date range
        List<Booking> juneBookings = hotel.searchBookings(
                "",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30)
        );
        assertEquals(1, juneBookings.size());
        assertEquals(b2.getId(), juneBookings.get(0).getId());
    }

    @Test
    public void testCountFreeRoomsByType() {
        Hotel hotel = createSampleHotel();

        // Book SINGLE room 101 for a few days in May
        LocalDate in = LocalDate.of(2024, 5, 1);
        LocalDate out = LocalDate.of(2024, 5, 4);
        hotel.createBooking("Alice", 101, in, out, "");

        Map<RoomType, Long> free = hotel.countFreeRoomsByType(LocalDate.of(2024, 5, 2));

        // On May 2: room 101 (SINGLE) is occupied, room 102 (DOUBLE) is free
        assertEquals(Long.valueOf(0L), free.get(RoomType.SINGLE));
        assertEquals(Long.valueOf(1L), free.get(RoomType.DOUBLE));
    }

    @Test
    public void testTotalBookedNightsInMonth() {
        Hotel hotel = createSampleHotel();

        // 1–4 May = 3 nights
        LocalDate in = LocalDate.of(2024, 5, 1);
        LocalDate out = LocalDate.of(2024, 5, 4);
        hotel.createBooking("Alice", 101, in, out, "");

        long nights = hotel.totalBookedNightsInMonth(YearMonth.of(2024, 5));
        assertEquals(3L, nights);
    }
}
