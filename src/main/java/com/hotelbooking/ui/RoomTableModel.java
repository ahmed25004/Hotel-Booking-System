package com.hotelbooking.ui;

import com.hotelbooking.model.Room;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class RoomTableModel extends AbstractTableModel {
    private final String[] columns = {"Number", "Type", "Price / Night", "Status"};
    private List<Room> rooms = new ArrayList<>();

    public void setRooms(Collection<Room> rooms) {
        this.rooms = new ArrayList<>(rooms);
        this.rooms.sort(Comparator.comparingInt(Room::getNumber));
        fireTableDataChanged();
    }

    public Room getRoomAt(int row) {
        if (row < 0 || row >= rooms.size()) return null;
        return rooms.get(row);
    }

    @Override
    public int getRowCount() {
        return rooms.size();
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
        Room r = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return r.getNumber();
            case 1:
                return r.getType();
            case 2:
                return r.getPricePerNight();
            case 3:
                return r.getStatus();
        }
        return null;
    }
}
