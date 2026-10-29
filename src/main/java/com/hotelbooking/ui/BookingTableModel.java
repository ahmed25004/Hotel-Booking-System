package com.hotelbooking.ui;

import com.hotelbooking.model.Booking;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingTableModel extends AbstractTableModel {
    private final String[] columns = {"Guest", "Room", "Check-in", "Check-out", "Status", "Notes"};
    private List<Booking> bookings = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    public void setBookings(List<Booking> bookings) {
        this.bookings = new ArrayList<>(bookings);
        fireTableDataChanged();
    }

    public Booking getBookingAt(int row) {
        if (row < 0 || row >= bookings.size()) return null;
        return bookings.get(row);
    }

    @Override
    public int getRowCount() {
        return bookings.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Booking b = bookings.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return b.getGuestName();
            case 1:
                return b.getRoomNumber();
            case 2:
                return b.getCheckIn().format(fmt);
            case 3:
                return b.getCheckOut().format(fmt);
            case 4:
                return b.getStatus();
            case 5:
                return b.getNotes();
        }
        return null;
    }
}
