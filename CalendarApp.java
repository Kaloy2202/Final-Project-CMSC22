import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.TreeMap;

public class CalendarApp extends JFrame {
    TimeblockManager timeblockManager = TimeblockManager.getInstance();     //singleton because it will be used for the unscheduled
    EntryManager entryManager = new EntryManager();
    private JTextField scheduledNameField, startTimeField, endTimeField;
    private JTextField unscheduledNameField, unitsField, unitsPerTimeSlotField, dueTimeField;
    private JButton addScheduledButton, addUnscheduledButton, displayAllEntriesButton;
    private DefaultTableModel tableModel;
    private JTable entriesTable;

    public CalendarApp() {

        // Set up the main frame
        setTitle("CalendarApp");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        scheduledNameField = new JTextField(10);
        startTimeField = new JTextField(10);
        endTimeField = new JTextField(10);
        addScheduledButton = new JButton("Add Scheduled Entry");

        unscheduledNameField = new JTextField(10);
        unitsField = new JTextField(10);
        unitsPerTimeSlotField = new JTextField(10);
        dueTimeField = new JTextField(10);
        addUnscheduledButton = new JButton("Add Unscheduled Entry");

        displayAllEntriesButton = new JButton("Display Entries");

        // Create a table for displaying entries
        tableModel = new DefaultTableModel(new String[]{"Name", "Details"}, 0);
        entriesTable = new JTable(tableModel);

        // Set up the layout using GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add scheduled entry components
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Scheduled Entry"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(scheduledNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Start Time:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(startTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("End Time:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(endTimeField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(addScheduledButton, gbc);

        // Add unscheduled entry components
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Unscheduled Entry"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        add(unscheduledNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Units:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        add(unitsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Units per Time Slot:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        add(unitsPerTimeSlotField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(new JLabel("Due Time:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        add(dueTimeField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        add(addUnscheduledButton, gbc);

        // Add display entries button
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        add(displayAllEntriesButton, gbc);

        // Add table for displaying entries
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(entriesTable), gbc);

        // Add action listeners
        addScheduledButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addScheduledEntry();
            }
        });

        addUnscheduledButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUnscheduledEntry();
            }
        });

        displayAllEntriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllEntries();
            }
        });
    }

    private void addScheduledEntry() {
        String taskName = scheduledNameField.getText();
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        try {     // checks for valid input for start and end time
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            if (startTime.isAfter(endTime)) {    // invalid input if start time is later than end time
                JOptionPane.showMessageDialog(this, "Error: Invalid start/end time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int startSlot = entryManager.calculateMinutes(startTime);
            int endSlot = entryManager.calculateMinutes(endTime);

            if (timeblockManager.isTimeslotOccupied(startSlot) || timeblockManager.isTimeslotOccupied(endSlot)) {    // invalid input if timeslot is occupied
                JOptionPane.showMessageDialog(this, "Timeslots are occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            entryManager.addScheduledEntry(startTime, endTime, taskName);
            JOptionPane.showMessageDialog(this, "Scheduled Entry Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String entryDetails = String.format("Start Time: %s, End Time: %s", startTimeStr, endTimeStr);
    }

    private void addUnscheduledEntry() {
        String name = unscheduledNameField.getText();
        String unitsStr = unitsField.getText();
        String unitsPerTimeSlotStr = unitsPerTimeSlotField.getText();
        String dueTimeStr = dueTimeField.getText();
        try {
            // Correct import statement for LocalTime
            java.time.LocalTime dueTime = java.time.LocalTime.parse(dueTimeStr);
            int units = Integer.parseInt(unitsStr);
            int unitsPerSlot = Integer.parseInt(unitsPerTimeSlotStr);
    
            // Add to EntryManager
            entryManager.getUnscheduledEntriesQueue().add(new UnscheduledEntry(name, dueTime, units, unitsPerSlot));
    
            JOptionPane.showMessageDialog(this, "Unscheduled Entry Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (java.time.format.DateTimeParseException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
        String entryDetails = String.format("Units: %s, Units per Time Slot: %s, Due Time: %s", unitsStr, unitsPerTimeSlotStr, dueTimeStr);
    }

    
    private void displayAllEntries() {
        TreeMap<Integer, CalendarEntry> allEntries = entryManager.getAllEntries();
        // Assuming MAX_TIME_SLOTS is a constant in your TimeblockManager class
        int maxTimeSlots = TimeblockManager.MAX_TIME_SLOTS;
    
        // Create a DefaultTableModel with columns "Time Slot", "Time Block", "Status", "Task Name"
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Time Slot", "Time Block", "Status", "Task Name"}, 0);
    
        for (int i = 0; i < maxTimeSlots; i++) {
            int hour = i / 4;
            int minute = (i % 4) * 15;
    
            String timeBlock = String.format("%02d:%02d", hour, minute);
    
            Map.Entry<Integer, CalendarEntry> floorEntry = allEntries.floorEntry(i);
            String taskName = (floorEntry != null && floorEntry.getValue() != null) ? floorEntry.getValue().getName() : "N/A";
            String status = (floorEntry != null && floorEntry.getValue() != null) ? "Occupied" : "Available";
    
            // Add a row to the model
            model.addRow(new Object[]{i, timeBlock, status, taskName});
        }
    
        // Set the model for the JTable
        entriesTable.setModel(model);
        setCellColors();
    }

    private void setCellColors() {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    // Check if the cell represents an occupied time slot
                    String status = table.getValueAt(row, 2).toString();
                    if ("Occupied".equals(status)) {
                        c.setBackground(Color.decode("#B0FC38")); // Set the color for occupied slots
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }

                    return c;
                }
            };

            for (int i = 0; i < entriesTable.getColumnCount(); i++) {
                entriesTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalendarApp().setVisible(true);
            }
        });
    }
}
