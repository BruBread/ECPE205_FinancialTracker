package UI;

import Data_backend.AccountManager;
import Data_backend.BankAccount;
import Data_backend.SubAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddSubAccountDialog extends JDialog {

    private final JTextField nameField    = new JTextField();
    private final JTextField balanceField = new JTextField();
    private final BankAccount bank;
    private final SubAccount  editing;

    // Pre-defined sub-account types for the dropdown
    private final String[] TYPES = {
            "Custom...", "Savings", "Expenses", "Insurance",
            "Emergency Fund", "Investments"
    };

    public AddSubAccountDialog(JFrame parent, BankAccount bank, SubAccount sub) {
        super(parent, sub == null ? "Add Sub-Account" : "Edit Sub-Account", true);
        this.bank    = bank;
        this.editing = sub;
        build();
        if (sub != null) {
            nameField.setText(sub.name);
            balanceField.setText(String.valueOf(sub.balance));
        }
        setVisible(true);
    }

    private void build() {
        setSize(300, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(ThemeManager.card());
        setContentPane(root);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(ThemeManager.card());

        // Preset type dropdown
        JComboBox<String> typeDropdown = new JComboBox<>(TYPES);
        typeDropdown.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        typeDropdown.setForeground(ThemeManager.text());
        typeDropdown.addActionListener(e -> {
            String selected = (String) typeDropdown.getSelectedItem();
            if (selected != null && !selected.equals("Custom..."))
                nameField.setText(selected);
        });

        nameField.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        nameField.setForeground(ThemeManager.text());
        nameField.setCaretColor(ThemeManager.text());

        balanceField.setBackground(ThemeManager.isDark() ? new Color(50,50,50) : Color.WHITE);
        balanceField.setForeground(ThemeManager.text());
        balanceField.setCaretColor(ThemeManager.text());

        form.add(label("Account Type")); form.add(typeDropdown);
        form.add(label("Sub-Account Name")); form.add(nameField);
        form.add(label("Balance (₱)")); form.add(balanceField);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, editing == null ? 1 : 2, 8, 0));
        buttons.setBackground(ThemeManager.card());

        JButton saveBtn = new JButton(editing == null ? "Add" : "Save");
        saveBtn.setBackground(new Color(30, 110, 50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());
        buttons.add(saveBtn);

        if (editing != null) {
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setBackground(new Color(200, 60, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.addActionListener(e -> {
                AccountManager.deleteSubAccount(bank, editing);
                dispose();
            });
            buttons.add(deleteBtn);
        }

        root.add(form,    BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
    }

    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }
        double balance;
        try {
            String text = balanceField.getText().trim();
            balance = text.isEmpty() ? 0 : Double.parseDouble(text);
            if (balance < 0) {
                JOptionPane.showMessageDialog(this, "Balance cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance.");
            return;
        }

        if (editing == null) {
            AccountManager.addSubAccount(bank, new SubAccount(name, balance));
        } else {
            double old = editing.balance;
            editing.name    = name;
            editing.balance = balance;
            AccountManager.updateSubAccount(bank, editing, old);
        }
        dispose();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(ThemeManager.text());
        return l;
    }
}