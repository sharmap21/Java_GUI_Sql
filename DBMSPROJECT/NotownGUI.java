import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class NotownGUI {
    private JTextField tableNameField;
    private JTextField searchTextField;
    private JButton searchButton;
    private JTextArea resultTextArea;
    private JButton alterTableButton;
    private JButton addRowButton;
    private JButton deleteRowButton;
    private JTextField securityCodeField;

    public NotownGUI() {
        JFrame frame = new JFrame("Notown Records");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel tableNameLabel = new JLabel("Table:");
        tableNameLabel.setBounds(10, 20, 80, 25);
        panel.add(tableNameLabel);

        tableNameField = new JTextField(20);
        tableNameField.setBounds(100, 20, 165, 25);
        panel.add(tableNameField);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(10, 60, 80, 25);
        panel.add(searchLabel);

        searchTextField = new JTextField(20);
        searchTextField.setBounds(100, 60, 165, 25);
        panel.add(searchTextField);

        searchButton = new JButton("Search");
        searchButton.setBounds(280, 60, 80, 25);
        panel.add(searchButton);

        resultTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBounds(10, 100, 560, 200);
        panel.add(scrollPane);

        alterTableButton = new JButton("Alter Table");
        alterTableButton.setBounds(10, 310, 120, 25);
        panel.add(alterTableButton);

        addRowButton = new JButton("Add Row");
        addRowButton.setBounds(140, 310, 100, 25);
        panel.add(addRowButton);

        deleteRowButton = new JButton("Delete Row");
        deleteRowButton.setBounds(250, 310, 120, 25);
        panel.add(deleteRowButton);

        securityCodeField = new JTextField(10);
        securityCodeField.setBounds(380, 310, 165, 25);
        panel.add(securityCodeField);

        // Action listeners
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        alterTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performAlterTable();
            }
        });

        addRowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performAddRow();
            }
        });

        deleteRowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performDeleteRow();
            }
        });
    }

    private void performSearch() {
        String tableName = tableNameField.getText();
        String searchText = searchTextField.getText();

        // Perform search based on tableName and searchText
        try {
            ResultSet resultSet;

            // Check if searchText is empty or not
            if (searchText.isEmpty()) {
                // Case 1: If searchText is empty, retrieve all columns
                resultSet = DatabaseManager.searchRecords("SELECT DISTINCT * FROM " + tableName);
            } else {
                // Case 2: If searchText is not empty, dynamically fetch column names and create a WHERE clause
                String whereClause = getWhereClause(tableName, searchText);
                resultSet = DatabaseManager.searchRecords("SELECT DISTINCT * FROM " + tableName + whereClause);
            }

            // Clear existing content in the resultTextArea
            resultTextArea.setText("");

            // Process and display the result in resultTextArea
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Use a HashSet to keep track of unique rows
            HashSet<String> uniqueRows = new HashSet<>();

            while (resultSet.next()) {
                StringBuilder rowText = new StringBuilder();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    rowText.append(columnName).append(": ").append(columnValue).append("\n");
                }

                // Check if the row is already processed
                if (uniqueRows.contains(rowText.toString())) {
                    continue;
                }

                // Add the row to the set to avoid duplicates
                uniqueRows.add(rowText.toString());

                resultTextArea.append(rowText.toString());
                resultTextArea.append("\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Display an error message in resultTextArea in case of an exception
            resultTextArea.setText("Error retrieving data: " + ex.getMessage());
        }
    }

    private String getWhereClause(String tableName, String searchText) throws SQLException {
        if ("musicians".equals(tableName) || "lives".equals(tableName)) {
            // For "musicians" and "lives" tables, join on the common attribute "ssn"
            return " LEFT JOIN lives ON musicians.ssn = lives.ssn" +
                    " LEFT JOIN album_producer ON musicians.ssn = album_producer.ssn" +
                    " WHERE musicians.name LIKE '%" + searchText + "%' OR lives.address LIKE '%" + searchText + "%' OR album_producer.title LIKE '%" + searchText + "%'";
        } else {
            // For other tables, search in all columns
            ResultSetMetaData metaData = DatabaseManager.searchRecords("SELECT * FROM " + tableName).getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder whereClause = new StringBuilder();

            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    whereClause.append(" OR ");
                }
                whereClause.append(tableName + "." + metaData.getColumnName(i)).append(" LIKE '%").append(searchText).append("%'");
            }

            return " WHERE " + whereClause.toString();
        }
    }

    private void performAlterTable() {
        // Create a new frame for Alter Table
        JFrame alterFrame = new JFrame("Alter Table");
        alterFrame.setSize(400, 200);
        JPanel alterPanel = new JPanel();
        alterFrame.add(alterPanel);
        placeAlterComponents(alterPanel, alterFrame);
        alterFrame.setVisible(true);
    }

    private void placeAlterComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel columnLabel = new JLabel("Column:");
        columnLabel.setBounds(10, 20, 80, 25);
        panel.add(columnLabel);

        JTextField columnField = new JTextField(10);
        columnField.setBounds(100, 20, 165, 25);
        panel.add(columnField);

        JLabel newValueLabel = new JLabel("New Value:");
        newValueLabel.setBounds(10, 50, 80, 25);
        panel.add(newValueLabel);

        JTextField newValueField = new JTextField(10);
        newValueField.setBounds(100, 50, 165, 25);
        panel.add(newValueField);

        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setBounds(10, 80, 80, 25);
        panel.add(conditionLabel);

        JTextField conditionField = new JTextField(10);
        conditionField.setBounds(100, 80, 165, 25);
        panel.add(conditionField);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(10, 120, 80, 25);
        panel.add(updateButton);

        // Action listener for the Update button
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String securityCode = securityCodeField.getText();
                if ("cs430@SIUC".equals(securityCode)) {
                    String tableName = tableNameField.getText();
                    String column = columnField.getText();
                    String newValue = newValueField.getText();
                    String condition = conditionField.getText();

                    // Generate and execute the update query
                    String updateQuery = "UPDATE " + tableName + " SET " + column + " = '" + newValue + "' WHERE " + condition;

                    try {
                        DatabaseManager.updateRecord(updateQuery);
                        // Display a success message
                        JOptionPane.showMessageDialog(null, "Update successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Close the Alter Table frame
                        frame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Display an error message
                        JOptionPane.showMessageDialog(null, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid security code!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void performAddRow() {
        // Create a new frame for Add Row
        JFrame addRowFrame = new JFrame("Add Row");
        addRowFrame.setSize(400, 200);
        JPanel addRowPanel = new JPanel();
        addRowFrame.add(addRowPanel);
        placeAddRowComponents(addRowPanel, addRowFrame);
        addRowFrame.setVisible(true);
    }

    private void placeAddRowComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel columnLabel = new JLabel("Column:");
        columnLabel.setBounds(10, 20, 80, 25);
        panel.add(columnLabel);

        JTextField columnField = new JTextField(10);
        columnField.setBounds(100, 20, 165, 25);
        panel.add(columnField);

        JLabel valueLabel = new JLabel("Value:");
        valueLabel.setBounds(10, 50, 80, 25);
        panel.add(valueLabel);

        JTextField valueField = new JTextField(10);
        valueField.setBounds(100, 50, 165, 25);
        panel.add(valueField);

        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 120, 80, 25);
        panel.add(addButton);

        // Action listener for the Add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String securityCode = securityCodeField.getText();
                if ("cs430@SIUC".equals(securityCode)) {
                    String tableName = tableNameField.getText();
                    String column = columnField.getText();
                    String value = valueField.getText();

                    // Generate and execute the insert query
                    String insertQuery = "INSERT INTO " + tableName + " (" + column + ") VALUES ('" + value + "')";

                    try {
                        DatabaseManager.updateRecord(insertQuery);
                        // Display a success message
                        JOptionPane.showMessageDialog(null, "Row added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Close the Add Row frame
                        frame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Display an error message
                        JOptionPane.showMessageDialog(null, "Error adding row: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid security code!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void performDeleteRow() {
        // Create a new frame for Delete Row
        JFrame deleteRowFrame = new JFrame("Delete Row");
        deleteRowFrame.setSize(400, 150);
        JPanel deleteRowPanel = new JPanel();
        deleteRowFrame.add(deleteRowPanel);
        placeDeleteRowComponents(deleteRowPanel, deleteRowFrame);
        deleteRowFrame.setVisible(true);
    }

    private void placeDeleteRowComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setBounds(10, 20, 80, 25);
        panel.add(conditionLabel);

        JTextField conditionField = new JTextField(10);
        conditionField.setBounds(100, 20, 165, 25);
        panel.add(conditionField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(10, 80, 80, 25);
        panel.add(deleteButton);

        // Action listener for the Delete button
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String securityCode = securityCodeField.getText();
                if ("cs430@SIUC".equals(securityCode)) {
                    String tableName = tableNameField.getText();
                    String condition = conditionField.getText();

                    // Generate and execute the delete query
                    String deleteQuery = "DELETE FROM " + tableName + " WHERE " + condition;

                    try {
                        DatabaseManager.updateRecord(deleteQuery);
                        // Display a success message
                        JOptionPane.showMessageDialog(null, "Row deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Close the Delete Row frame
                        frame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Display an error message
                        JOptionPane.showMessageDialog(null, "Error deleting row: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid security code!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NotownGUI();
            }
        });
    }
}
