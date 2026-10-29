package com.hotelbooking.ui;

import com.hotelbooking.model.*;
import com.hotelbooking.service.Hotel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class HotelBookingFrame extends JFrame {
    private Hotel hotel = new Hotel();

    private final BookingTableModel bookingTableModel = new BookingTableModel();
    private final RoomTableModel roomTableModel = new RoomTableModel();

    private final JTable bookingTable = new JTable(bookingTableModel);
    private final JTable roomTable = new JTable(roomTableModel);

    private final JTextField searchField = new JTextField(15);
    private final JTextField dateFromField = new JTextField(10); // yyyy-MM-dd
    private final JTextField dateToField = new JTextField(10);

    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    public HotelBookingFrame() {
        super("Hotel Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initDemoData();
        initUI();
        refreshRooms();
        refreshBookings();
    }

    private void initDemoData() {
        // some rooms to start with
        hotel.addRoom(new Room(101, RoomType.SINGLE, 50, RoomStatus.AVAILABLE));
        hotel.addRoom(new Room(102, RoomType.DOUBLE, 80, RoomStatus.AVAILABLE));
        hotel.addRoom(new Room(201, RoomType.SUITE, 150, RoomStatus.AVAILABLE));
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // ---- Bookings tab ----
        JPanel bookingPanel = new JPanel(new BorderLayout());
        bookingPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel topFilter = new JPanel();
        topFilter.add(new JLabel("Search (guest/room):"));
        topFilter.add(searchField);
        topFilter.add(new JLabel("From (yyyy-MM-dd):"));
        topFilter.add(dateFromField);
        topFilter.add(new JLabel("To:"));
        topFilter.add(dateToField);
        JButton filterBtn = new JButton("Apply Filter");
        filterBtn.addActionListener(e -> refreshBookings());
        topFilter.add(filterBtn);
        bookingPanel.add(topFilter, BorderLayout.NORTH);

        JPanel bookingButtons = new JPanel();
        JButton addBookingBtn = new JButton("Add Booking");
        addBookingBtn.addActionListener(this::onAddBooking);
        JButton editBookingBtn = new JButton("Edit Booking");
        editBookingBtn.addActionListener(this::onEditBooking);
        JButton cancelBookingBtn = new JButton("Cancel Booking");
        cancelBookingBtn.addActionListener(this::onCancelBooking);
        bookingButtons.add(addBookingBtn);
        bookingButtons.add(editBookingBtn);
        bookingButtons.add(cancelBookingBtn);
        bookingPanel.add(bookingButtons, BorderLayout.SOUTH);

        tabs.addTab("Bookings", bookingPanel);

        // ---- Rooms tab ----
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel roomButtons = new JPanel();
        JButton addRoomBtn = new JButton("Add Room");
        addRoomBtn.addActionListener(this::onAddRoom);
        JButton editRoomBtn = new JButton("Edit Room");
        editRoomBtn.addActionListener(this::onEditRoom);
        JButton deleteRoomBtn = new JButton("Delete Room");
        deleteRoomBtn.addActionListener(this::onDeleteRoom);
        roomButtons.add(addRoomBtn);
        roomButtons.add(editRoomBtn);
        roomButtons.add(deleteRoomBtn);
        roomPanel.add(roomButtons, BorderLayout.SOUTH);

        tabs.addTab("Rooms", roomPanel);

        // ---- Top toolbar ----
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());
        JButton loadBtn = new JButton("Load");
        loadBtn.addActionListener(e -> onLoad());
        JButton statsBtn = new JButton("Statistics");
        statsBtn.addActionListener(e -> onStats());
        toolBar.add(saveBtn);
        toolBar.add(loadBtn);
        toolBar.addSeparator();
        toolBar.add(statsBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    // ----- Rooms handlers -----

    private void onAddRoom(ActionEvent e) {
        try {
            String numStr = JOptionPane.showInputDialog(this, "Room number:");
            if (numStr == null) return;
            int num = Integer.parseInt(numStr);

            RoomType type = (RoomType) JOptionPane.showInputDialog(
                    this, "Room type:", "Type",
                    JOptionPane.QUESTION_MESSAGE, null,
                    RoomType.values(), RoomType.SINGLE);
            if (type == null) return;

            String priceStr = JOptionPane.showInputDialog(this, "Price per night:");
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            RoomStatus status = (RoomStatus) JOptionPane.showInputDialog(
                    this, "Status:", "Status",
                    JOptionPane.QUESTION_MESSAGE, null,
                    RoomStatus.values(), RoomStatus.AVAILABLE);
            if (status == null) return;

            hotel.addRoom(new Room(num, type, price, status));
            refreshRooms();
        } catch (NumberFormatException ex) {
            showError("Invalid number.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onEditRoom(ActionEvent e) {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            showError("Select a room first.");
            return;
        }
        Room r = roomTableModel.getRoomAt(row);
        if (r == null) return;

        try {
            String priceStr = JOptionPane.showInputDialog(this,
                    "Price per night:", r.getPricePerNight());
            if (priceStr == null) return;
            double price = Double.parseDouble(priceStr);

            RoomType type = (RoomType) JOptionPane.showInputDialog(
                    this, "Room type:", "Type",
                    JOptionPane.QUESTION_MESSAGE, null,
                    RoomType.values(), r.getType());
            if (type == null) return;

            RoomStatus status = (RoomStatus) JOptionPane.showInputDialog(
                    this, "Status:", "Status",
                    JOptionPane.QUESTION_MESSAGE, null,
                    RoomStatus.values(), r.getStatus());
            if (status == null) return;

            hotel.editRoom(r.getNumber(), type, price, status);
            refreshRooms();
        } catch (NumberFormatException ex) {
            showError("Invalid number.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onDeleteRoom(ActionEvent e) {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            showError("Select a room first.");
            return;
        }
        Room r = roomTableModel.getRoomAt(row);
        if (r == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete room " + r.getNumber() + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            hotel.deleteRoom(r.getNumber());
            refreshRooms();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    // ----- Booking handlers -----

    private void onAddBooking(ActionEvent e) {
        try {
            String guest = JOptionPane.showInputDialog(this, "Guest name:");
            if (guest == null || guest.trim().isEmpty()) return;

            String roomStr = JOptionPane.showInputDialog(this, "Room number:");
            if (roomStr == null) return;
            int roomNumber = Integer.parseInt(roomStr);

            String checkInStr = JOptionPane.showInputDialog(this, "Check-in (yyyy-MM-dd):");
            if (checkInStr == null) return;
            String checkOutStr = JOptionPane.showInputDialog(this, "Check-out (yyyy-MM-dd):");
            if (checkOutStr == null) return;

            LocalDate checkIn = LocalDate.parse(checkInStr, fmt);
            LocalDate checkOut = LocalDate.parse(checkOutStr, fmt);

            String notes = JOptionPane.showInputDialog(this, "Notes:");
            if (notes == null) notes = "";

            hotel.createBooking(guest, roomNumber, checkIn, checkOut, notes);
            refreshBookings();
        } catch (NumberFormatException ex) {
            showError("Invalid number.");
        } catch (DateTimeParseException ex) {
            showError("Invalid date format.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void onEditBooking(ActionEvent e) {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            showError("Select a booking first.");
            return;
        }
        Booking b = bookingTableModel.getBookingAt(row);
        if (b == null) return;
        if (b.getStatus() == BookingStatus.CANCELLED) {
            showError("Cannot edit a cancelled booking.");
            return;
        }

        try {
            String guest = JOptionPane.showInputDialog(this, "Guest name:", b.getGuestName());
            if (guest == null || guest.trim().isEmpty()) return;

            String roomStr = JOptionPane.showInputDialog(this, "Room number:", b.getRoomNumber());
            if (roomStr == null) return;
            int roomNumber = Integer.parseInt(roomStr);

            String checkInStr = JOptionPane.showInputDialog(this, "Check-in (yyyy-MM-dd):",
                    b.getCheckIn().format(fmt));
            if (checkInStr == null) return;
            String checkOutStr = JOptionPane.showInputDialog(this, "Check-out (yyyy-MM-dd):",
                    b.getCheckOut().format(fmt));
            if (checkOutStr == null) return;

            LocalDate checkIn = LocalDate.parse(checkInStr, fmt);
            LocalDate checkOut = LocalDate.parse(checkOutStr, fmt);

            String notes = JOptionPane.showInputDialog(this, "Notes:", b.getNotes());
            if (notes == null) notes = "";

            hotel.editBooking(b.getId(), guest, roomNumber, checkIn, checkOut, notes);
            refreshBookings();
        } catch (NumberFormatException ex) {
            showError("Invalid number.");
        } catch (DateTimeParseException ex) {
            showError("Invalid date.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void onCancelBooking(ActionEvent e) {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            showError("Select a booking first.");
            return;
        }
        Booking b = bookingTableModel.getBookingAt(row);
        if (b == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking for " + b.getGuestName() + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            hotel.cancelBooking(b.getId());
            refreshBookings();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    // ----- Save / Load / Stats -----

    private void onSave() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                hotel.saveToFile(file);
                JOptionPane.showMessageDialog(this, "Saved.");
            } catch (IOException ex) {
                showError("Error saving: " + ex.getMessage());
            }
        }
    }

    private void onLoad() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                hotel = Hotel.loadFromFile(file);
                refreshRooms();
                refreshBookings();
                JOptionPane.showMessageDialog(this, "Loaded.");
            } catch (IOException | ClassNotFoundException ex) {
                showError("Error loading: " + ex.getMessage());
            }
        }
    }

    private void onStats() {
        String dateStr = JOptionPane.showInputDialog(this,
                "Date for availability (yyyy-MM-dd):", LocalDate.now().format(fmt));
        if (dateStr == null) return;
        try {
            LocalDate date = LocalDate.parse(dateStr, fmt);
            Map<RoomType, Long> freeByType = hotel.countFreeRoomsByType(date);
            YearMonth currentMonth = YearMonth.now();
            long nights = hotel.totalBookedNightsInMonth(currentMonth);

            StringBuilder sb = new StringBuilder();
            sb.append("Availability on ").append(date).append(":\n");
            for (RoomType type : RoomType.values()) {
                sb.append(type).append(": ").append(freeByType.get(type)).append(" free\n");
            }
            sb.append("\nTotal booked nights this month (")
                    .append(currentMonth).append("): ").append(nights);

            JOptionPane.showMessageDialog(this, sb.toString(), "Statistics",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            showError("Invalid date.");
        }
    }

    // ----- Helpers -----

    private void refreshRooms() {
        roomTableModel.setRooms(hotel.getRooms());
    }

    private void refreshBookings() {
        LocalDate from = parseDateOrNull(dateFromField.getText());
        LocalDate to = parseDateOrNull(dateToField.getText());
        List<Booking> filtered = hotel.searchBookings(searchField.getText(), from, to);
        bookingTableModel.setBookings(filtered);
    }

    private LocalDate parseDateOrNull(String text) {
        String t = text.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDate.parse(t, fmt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
