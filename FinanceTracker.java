import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FinanceTracker extends JFrame {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private double totalBalance = 0;
    
    public FinanceTracker() {
        setTitle("Finance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Input Panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        JLabel titleLabel = new JLabel("Finance Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(balanceLabel);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));
        
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(15);
        
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(10);
        
        JLabel typeLabel = new JLabel("Type:");
        String[] types = {"Income", "Expense"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        
        JButton addBtn = new JButton("Add");
        addBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addBtn.addActionListener(e -> {
            try {
                String description = descField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();
                
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a description", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                addTransaction(description, amount, type);
                descField.setText("");
                amountField.setText("");
                typeCombo.setSelectedIndex(0);
                descField.requestFocus();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        panel.add(descLabel);
        panel.add(descField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(typeLabel);
        panel.add(typeCombo);
        panel.add(addBtn);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        
        String[] columns = {"Date", "Description", "Type", "Amount", "Balance"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setBackground(new Color(52, 152, 219));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(750, 250));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deleteBtn.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a transaction to delete", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString());
            String type = tableModel.getValueAt(selectedRow, 2).toString();
            
            if (type.equals("Expense")) {
                totalBalance += amount;
            } else {
                totalBalance -= amount;
            }
            
            tableModel.removeRow(selectedRow);
            updateBalance();
        });
        
        JButton clearBtn = new JButton("Clear All");
        clearBtn.setBackground(new Color(155, 155, 155));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        clearBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Clear all transactions?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                tableModel.setRowCount(0);
                totalBalance = 0;
                updateBalance();
            }
        });
        
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addTransaction(String description, double amount, String type) {
        if (type.equals("Expense")) {
            totalBalance -= amount;
        } else {
            totalBalance += amount;
        }
        
        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        Object[] row = {date, description, type, String.format("$%.2f", amount), String.format("$%.2f", totalBalance)};
        tableModel.insertRow(0, row);
        
        updateBalance();
    }
    
    private void updateBalance() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", totalBalance));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceTracker app = new FinanceTracker();
            app.setVisible(true);
        });
    }
}