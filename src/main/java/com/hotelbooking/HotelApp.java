package com.hotelbooking;

import com.hotelbooking.ui.HotelBookingFrame;

import javax.swing.*;

public class HotelApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // nice look & feel if available
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new HotelBookingFrame().setVisible(true);
        });
    }
}
